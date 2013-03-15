package bcibot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;


public class Connection implements Runnable
{
	/**
	 * Constructor, creates the connection, then calls connect
	 * @param _address String - IP of the robot
	 * @param _port int - port to connect to on the robot
	 * @param _parent ArchitectureClient - client reference
	 * @param _name String - name of this connection (move, speech, etc)
	 */
	public Connection(String _address, int _port, ArchitectureClient _parent, String _name)
	{
		address = _address;
		port = _port;
		parent = _parent;
		name = _name;
		
		q = new LinkedBlockingQueue<String>();
		
		if (!doConnect())
			reportError("Connection on port " + port + " failed!");
	}
	
	/**
	 * Adds a command to the queue of commands
	 * @param command String - the command to add
	 */
	public void addCommand(String command)
	{
		setStatus(false);
		synchronized(q)
		{
			q.add(command);
		}
	}
	
	/**
	 * Closes the connection
	 * closes the sockets and streams when queue is empty
	 */
	public void close()
	{
		boolean empty = false;
		while(!empty)
		{
			synchronized(q)
			{
				empty = q.isEmpty();			
			}
			
			try {Thread.sleep(1000);}
			catch (InterruptedException e1) {e1.printStackTrace();}
		}
		
		try {Thread.sleep(1000);} catch (InterruptedException e1) {}
		
		try
		{
			if (socket != null)
				socket.close();
			if (write_stream != null)
				write_stream.close();
			if (read_stream != null)
				read_stream.close();
		}
		catch (IOException e) {reportError("Could not close connection!");} 
	}
	
	/**
	 * Connects the socket and read/write streams
	 * @return true if successful, false otherwise
	 */
	private boolean doConnect()
	{
		try
		{
			// Create a new socket connection.
			socket = new Socket(address, port);
			
			// Set the output stream.
			write_stream = new PrintWriter(socket.getOutputStream(), true);
			read_stream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			return true;
		}
		catch (Exception ex){reportError(ex.toString());return false;}
	}
	
	//**************************************************************************
	//Threaded Methods**********************************************************
	//**************************************************************************
	
	/**
	 * Loop, sending commands, until queue is empty
	 */
	public void run()
	{	
		while (true)
		{
			synchronized(q)
			{
				if (q.isEmpty())
				{
					setStatus(true);
					break;
				}
				
				else
				{
					setStatus(false);
					try {send(q.remove());}
					catch (Exception e) {reportError("Error was: " + e.toString());}
				}
			}
		}

	}
	
	/**
	 * send command over the socket
	 * @param command String - the command to send
	 */
	public void send(String command) throws Exception
	{
		System.out.println("Sending: " + command);
		write_stream.println(command);
		
		String echo = recv();//check echo for errors
		System.out.println("Response: " + echo);
		if(echo.contains("error"))
			reportError(echo);
		
		synchronized(q)
		{
			if (!q.isEmpty())
			{
				try {send(q.remove());}
				catch (Exception e) {reportError("Error was: " + e.toString());}
			}
		}
	}
	
	/**
	 * waits for a return message from robot
	 * @return message returned as a String
	 * @throws Exception - if IO error occurs
	 */
	private String recv() throws Exception
	{
		String returnMessage= "OK";
		while(true)
		{
			if (read_stream.ready())
			{
				returnMessage = read_stream.readLine();
				if (!returnMessage.equals("OK")) break;
				
				else
				{
					Thread.sleep(500);
					write_stream.println("events");
				}
			}	
			
			Thread.sleep(100);
		}
		return returnMessage;
	}
	
	/**
	 * passes an error to the client
	 * @param error String - error to return
	 */
	private void reportError(String error)
	{
		parent.architectureError(name + " " + error);
	}
	
	private void setStatus(boolean status)
	{
		if(Thread.currentThread().getName().equalsIgnoreCase("move"))
			parent.setMOVE_DONE(status);
		if(Thread.currentThread().getName().equalsIgnoreCase("speak"))
			parent.setSPEAK_DONE(status);
		if(Thread.currentThread().getName().equalsIgnoreCase("gripper"))
			parent.setGRIPPER_DONE(status);
		if(Thread.currentThread().getName().equalsIgnoreCase("camera"))
			parent.setCAMERA_DONE(status);
	}
	
	//**************************************************************************
	//Instance Variables********************************************************
	//**************************************************************************

	private String name;
	private String address;
	private int port;
	private Socket socket = null;
	private PrintWriter write_stream = null;
	private BufferedReader read_stream = null;
	private LinkedBlockingQueue<String> q;
	private ArchitectureClient parent;
}
