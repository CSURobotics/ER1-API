package bcibot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class GetRobotIP
{
	/**
	 * Constructor, creates the connection, then calls connect
	 * @param _address String - IP of the robot
	 * @param _port int - port to connect to on the robot
	 * @param _parent ArchitectureClient - client reference
	 * @param _name String - name of this connection (move, speech, etc)
	 */
	public static String getIP(String _address, String name)
	{
		address = _address;
		

		
		if (!doConnect())
		System.err.println("Connection on port " + port + " failed!");
		
		//send("SET " + name + " " + "129.82.19.27");
		String IP = send("GET " + name + "\n");
		
		close();
		return IP;
	}
	
	/**
	 * Closes the connection, and closes the sockets and streams
	 */
	public static void close()
	{		
		try
		{
			if (socket != null)
				socket.close();
			if (write_stream != null)
				write_stream.close();
			if (read_stream != null)
				read_stream.close();
		}
		catch (IOException e) {System.err.println("Could not close connection!");} 
	}
	
	/**
	 * Connects the socket and read/write streams
	 * @return true if successful, false otherwise
	 */
	private static boolean doConnect()
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
		catch (Exception ex){System.err.println(ex.toString());return false;}
	}

	
	/**
	 * send command over the socket
	 * @param command String - the command to send
	 */
	public static String send(String command)
	{
		write_stream.println(command);
		
		String echo = "error";
		try {echo = recv();}
		catch (Exception e) {e.printStackTrace();}

		return echo;
	}
	
	/**
	 * waits for a return message from registration server
	 * @return message returned as a String
	 * @throws Exception - if IO error occurs
	 */
	private static String recv() throws Exception
	{
		while(!read_stream.ready())
			Thread.sleep(100);
		
		return read_stream.readLine();
	}
	
	//**************************************************************************
	//Instance Variables********************************************************
	//**************************************************************************

	private static String address;
	private static final int port = 9050;
	private static Socket socket = null;
	private static PrintWriter write_stream = null;
	private static BufferedReader read_stream = null;

}
