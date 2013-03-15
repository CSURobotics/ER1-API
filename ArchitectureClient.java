package bcibot;

/**
 * This class implements a communication interface for an ER1 robot.
 * 
 * @author  Nick Parrish & Christina Williams
 * @version 26 March 2006 - Deleted useless commands and code
 * Project:	Robot_Architecture_Client
 * File: 	ArchitectureClient.java
 */

public class ArchitectureClient 
{
	/**
	 * Constructor, initializes connection objects.
	 * @param _address String - the IP of the robot
	 */
	public ArchitectureClient(String _address)
	{	
		address = _address;
		Move = new Connection(address, MovePort, this, "Move");
		Speak = new Connection(address, SpeakPort, this, "Speak");
		Gripper = new Connection(address, GripperPort, this, "Gripper");
		Camera = new Connection(address, CameraPort, this, "Camera");
		
		moveThread = new Thread(Move, "Move");
		speakThread = new Thread(Speak, "Speak");
		gripperThread = new Thread(Gripper, "Gripper");
		cameraThread = new Thread(Camera, "Camera");
		
		moveThread.start();
		speakThread.start();
		gripperThread.start();
		cameraThread.start();
	}
	
	/**
	 * Sends a command to appropriate connection
	 * @param command String - the command to send
	 */
	public void sendCommand(String command)//decision making
	{
		//Cut the prefix off the beginning to route the command to the correct socket
		String prefix = command.substring(0, 3);
		//then, send the command minus the prefix to the correct socket
		String postfix = command.substring(4, command.length());
		
		try
		{
			if (prefix.compareTo("ER1") == 0)
			{
				//MOVE_DONE = false;
				Move.addCommand(postfix);
				if(!moveThread.isAlive())
				{
					Thread moveThread = new Thread(Move, "Move");
					moveThread.start();
				}
			}
			
			if (prefix.compareTo("SPK") == 0)
			{
				//SPEAK_DONE = false;
				Speak.addCommand(postfix);
				if (!speakThread.isAlive())
				{
					Thread speakThread = new Thread(Speak, "Speak");
					speakThread.start();
				}
			}
			
			if (prefix.compareTo("GRP") == 0)
			{
				//GRIPPER_DONE = false;
				Gripper.addCommand(postfix);
				if (!gripperThread.isAlive())
				{
					Thread gripperThread = new Thread(Gripper, "Gripper");
					gripperThread.start();
				}
			}
			
			if (prefix.compareTo("CAM") == 0)
			{
				//CAMERA_DONE = false;
				Camera.addCommand(postfix);
				if (!cameraThread.isAlive())
				{
					Thread cameraThread = new Thread(Camera, "Camera");
					cameraThread.start();
				}
			}
			
		}catch (Exception e){architectureError(e.toString());}
	}
	
	/**
	 * Handles errors from this and Connection class
	 * @param error String - error to be handled
	 */
	public void architectureError(String error)
	{
		System.err.println(error);
	}
	
	/**
	 * Disconnect the client.
	 * Disconnects the connections.
	 */
	public void disconnect()
	{
		if (Move != null)
			Move.close();
		if (Speak != null)
			Speak.close();
		if (Gripper != null)
			Gripper.close();
		if (Camera != null)
			Camera.close();
	}
	
	public void close()
    {
		if (Move != null)
			Move.close();
		if (Speak != null)
			Speak.close();
		if (Gripper != null)
			Gripper.close();
		if (Camera != null)
			Camera.close();
    }
	
	public synchronized boolean isCAMERA_DONE() {
		return CAMERA_DONE;
	}

	public synchronized boolean isGRIPPER_DONE() {
		return GRIPPER_DONE;
	}

	public synchronized boolean isMOVE_DONE() {
		return MOVE_DONE;
	}

	public synchronized boolean isSPEAK_DONE() {
		return SPEAK_DONE;
	}

	public synchronized void setCAMERA_DONE(boolean camera_done) {
		CAMERA_DONE = camera_done;
	}

	public synchronized void setGRIPPER_DONE(boolean gripper_done) {
		GRIPPER_DONE = gripper_done;
	}

	public synchronized void setMOVE_DONE(boolean move_done) {
		MOVE_DONE = move_done;
	}

	public synchronized void setSPEAK_DONE(boolean speak_done) {
		SPEAK_DONE = speak_done;
	}
	
	//**************************************************************************
	//Instance Variables********************************************************
	//**************************************************************************

	private Thread moveThread;
	private Thread speakThread;
	private Thread gripperThread;
	private Thread cameraThread;
	
	private Connection Move;
	private Connection Speak;
	private Connection Gripper;
	private Connection Camera;
	
	private int MovePort = 9010;
	private int SpeakPort = 9011;
	private int GripperPort = 9012;
	private int CameraPort = 9013;
	
	private boolean MOVE_DONE = true;
	private boolean CAMERA_DONE = true;
	private boolean SPEAK_DONE = true;
	private boolean GRIPPER_DONE = true;
	
	private String address = "127.0.0.1";
}
