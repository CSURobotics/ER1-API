package bcibot;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.net.*;


public class Camera
{
	/**
	 * Constructs a camera with the given IP address, where the IP address is 
	 * the address of the computer with the desired webcam.
	 */
	public Camera()//OPEN SECOND SOCKET
	{
		socket = null;
		
		//LISTEN ON SOCKET
		try {server = new ServerSocket(port);} 
		catch (IOException e3) {e3.printStackTrace();}
		try{socket = server.accept();}
		catch (IOException e){e.printStackTrace();}
		//SET UP STREAMS
		try{inStream = new BufferedInputStream(socket.getInputStream());}
		catch (IOException e1){e1.printStackTrace();}
		try{outStream = new PrintWriter(socket.getOutputStream(), true);}
		catch (IOException e2){e2.printStackTrace();}
	}
	
	/**
	 * Grabs an image from the robot and returns the raster of the image.
	 * @return BufferedImage - the raster of the image.
	 */
	public BufferedImage grabImage()
	{
		try {while (inStream.available() == 0);}
		catch (IOException e1) {e1.printStackTrace();}
		
		BufferedImage image = null;
		try {image = ImageIO.read(inStream);} 
		catch (Exception e) {e.printStackTrace();}
		return image;
	}
	
	/**
	 * Closes the client. Must be called when camera is no longer in use.
	 */
	public void close()
	{
		try{inStream.close();}
		catch (Exception e){e.printStackTrace();}
		try{outStream.close();}
		catch (Exception e){e.printStackTrace();}
		try{socket.close();}
		catch (Exception e){e.printStackTrace();}
	}

	//**************************************************************************
	//Instance Variables********************************************************
	//**************************************************************************
	
	private Socket socket;
	private int port = 9023;	
	private BufferedInputStream inStream; 
	private PrintWriter outStream;
	private ServerSocket server;

	//private String GRAB_IMAGE = "grab image";
	//private String DISCONNECT = "disconnect";
	//private String EXIT = "exit";
}
