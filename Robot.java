package bcibot;

/**
 * This class implements a control interface for an ER1 robot. It includes  
 * everything that is needed to drive the robot around, have the robot recite
 * messages, take pictures, and interact with the gripper.
 * 
 * @author  Nick Parrish & Christie Williams
 * 		    Original concept by Weston Pace and Jason Remington
 * @version 20 April 2006 - Fixed error printing for robot IP aquisition
 * Project:	Robot_Architecture_Client
 * File: 	bcibot.java
 */

public class Robot
{
	//**************************************************************************
	//CONSTRUCTORS *************************************************************
	//**************************************************************************
	
	/**
	 * Connects to a robot using the default address. Verbose output is specified as true.
	 */
	public Robot() 
	{initializeRobot(DEFAULT_ADDRESS, DEFAULT_VERBOSE);}

	/**
	 * Connects to a robot with the given address. Verbose output is specified as true.
	 * @param address String - the IP Address of the robot.
	 */
	public Robot(String _address)
	{initializeRobot(_address, DEFAULT_VERBOSE);}
	
	/**
	 * Connects to a robot with the option to specify verbose output.
	 * @param verbose boolean - true if verbose output is desired, else false.
	 */
	public Robot(boolean _verbose)
	{initializeRobot(DEFAULT_ADDRESS, _verbose);}

	/**
	 * Connects to a robot with the given address with the option to specify verbose output.
	 * @param address String - the IP Address of the robot.
	 * @param verbose boolean - true if verbose output is desired, else false.
	 */
	public Robot(String _address, boolean _verbose)
	{initializeRobot(_address, _verbose);}	
	
	/**
	 * Connects to a robot with the specified address with the option to specify verbose output.
	 * @param address String - the IP Address of the robot.
	 * @param verbose boolean - true if verbose output is desired, else false.
	 */
	private void initializeRobot(String _address, boolean _verbose)
	{
		try{
			client = new ArchitectureClient(_address);

			//set booleans
			multiTasking = false;
			linkedCommands = false;
			verbose = _verbose;
			
			setMovementSpeed(LOW_MOVEMENT_SPEED);
			setTurningSpeed(LOW_TURNING_SPEED);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.err.println("Error connecting to bcibot, make sure the architecture is running!!");
			System.exit(0);
		}
	}
	
	//**************************************************************************
	//MOVEMENT COMMANDS ********************************************************
	//**************************************************************************
	
	/**
	 * Moves the robot forward until it is told to stop.
	 */
	public void moveForward()
	{
		if(!multiTasking && !linkedCommands)
			waitFor(ALL_DONE);
		sendCommand(ROUTE_MOVE + MOVE + FORWARD + NEWLINE);
	}
	
	/**
	 * Moves the robot backwards until it is told to stop.
	 */
	public void moveBackward()
	{
		if(!multiTasking && !linkedCommands)
			waitFor(ALL_DONE);
		sendCommand(ROUTE_MOVE + MOVE + BACKWARD + NEWLINE);
	}
	
	/**
	 * Moves the robot forward the specified distance in default units.
	 * @param amount double - the number of units to move
	 */
	public void moveForward(double amount)
	{
		moveForward(amount, DEFAULT_MOVE_UNITS);
	}
	
	/**
	 * Moves the robot backward the specified distance in default units.
	 * @param amount double - the number of units to move
	 */
	public void moveBackward(double amount)
	{
		moveBackward(amount, DEFAULT_MOVE_UNITS);
	}
	
	/**
	 * Moves the robot forward the specified distance using specified units.
	 * @param distance double - the number of units to move
	 * @param units String - can be "inches" "feet" "meters" "centimeters"
	 */
	public void moveForward(double amount, String units)
	{
		if(!multiTasking && !linkedCommands)
			waitFor(ALL_DONE);
		
		double inches = amount;
		if(units.compareTo(INCHES) != 0)
			inches = doUnitConversion(amount, units, INCHES);

		sendCommand(ROUTE_MOVE + MOVE + FORWARD + SPACE + (int)inches + NEWLINE);
	}
	
	/**
	 * Moves the robot backward the specified distance using specified units.
	 * @param distance double - the number of units to move
	 * @param units String - can be "inches" "feet" "meters" "centimeters"
	 */
	public void moveBackward(double amount, String units)
	{
		if(!multiTasking && !linkedCommands)
			waitFor(ALL_DONE);
		
		double inches = amount;
		if(units.compareTo(INCHES) != 0)
			inches = doUnitConversion(amount, units, INCHES);

		sendCommand(ROUTE_MOVE + MOVE + BACKWARD + SPACE + (int)inches + NEWLINE);
	}
	
	/**
	 * Moves the robot forward the specified distance in inches.
	 * @param distance double - the number of inches to move
	 */
	public void move(double inches)
	{
		moveForward(inches, DEFAULT_MOVE_UNITS);
	}
	
	/**
	 * Either turns or moves the robot depending on specified units.
	 * @param distance double - the number of units to move
	 * @param units String - can be "inches" "feet" "meters" "centimeters" "degrees" "radians"
	 */
	public void move(double amount, String units)
	{		
		if(units.compareTo(DEGREES) == 0 || units.compareTo(RADIANS) == 0)
			turn(amount, units);
		else
			moveForward(amount, units);
	}
	
	//**************************************************************************
	//TURNING COMMANDS *********************************************************
	//**************************************************************************
		
	/**
	 * Turns the robot left (counter-clockwise) until it is told to stop.
	 */
	public void turnLeft()
	{
		if(!multiTasking && !linkedCommands)
			waitFor(ALL_DONE);
		
		sendCommand(ROUTE_MOVE + MOVE + LEFT + NEWLINE);
	}
	
	/**
	 * Turns the robot right (clockwise) until it is told to stop.
	 */
	public void turnRight()
	{
		if(!multiTasking && !linkedCommands)
			waitFor(ALL_DONE);
		
		sendCommand(ROUTE_MOVE + MOVE + RIGHT + NEWLINE);
	}
	
	/**
	 * Turns the robot left (counter-clockwise) for the specified number of default units. 
	 * @param amount double - the number of degrees to turn
	 */
	public void turnLeft(double amount)
	{		
		turnLeft(amount, DEFAULT_TURN_UNITS);
	}
	
	/**
	 * Turns the robot right (clockwise) for the specified number of default units. 
	 * @param amount double - the number of degrees to turn
	 */
	public void turnRight(double amount)
	{		
		turnRight(amount, DEFAULT_TURN_UNITS);
	}

	/**
	 * Turns the robot left (counter-clockwise) for the specified units. 
	 * @param amount double - the number of units to turn
	 * @param units String - can be "degrees" or "radians"
	 */
	public void turnLeft(double amount, String units)
	{
		if(!multiTasking && !linkedCommands)
			waitFor(ALL_DONE);
		
		double degrees = amount;
		if(units.compareTo(DEGREES) != 0)
			degrees = doUnitConversion(amount, units, DEGREES);

		sendCommand(ROUTE_MOVE + MOVE + LEFT + SPACE + (int)degrees + NEWLINE);
	}
	
	/**
	 * Turns the robot right (clockwise) for the specified units. 
	 * @param amount double - the number of units to turn
	 * @param units String - can be "degrees" or "radians"
	 */
	public void turnRight(double amount, String units)
	{
		if(!multiTasking && !linkedCommands)
			waitFor(ALL_DONE);
		
		double degrees = amount;
		if(units.compareTo(DEGREES) != 0)
			degrees = doUnitConversion(amount, units, DEGREES);

		sendCommand(ROUTE_MOVE + MOVE + RIGHT + SPACE + (int)degrees + NEWLINE);
	}
	
	/**
	 * Turns the robot the specified number of degrees. A negative number turns
	 * the robot right (clockwise), and a positive number turns the robot left (counter-clockwise).
	 * @param degrees double - the number of degrees to turn the robot.
	 */
	public void turn(double degrees)
	{
		turnLeft(degrees, DEFAULT_TURN_UNITS);
	}

	/**
	 * Turns the robot the specified number of units. A negative number turns
	 * the robot right (clockwise), and a positive number turns the robot left (counter-clockwise).
	 * @param amount double - the number of degrees to turn the robot.
	 * @param units String - can be "degrees" or "radians"
	 */
	public void turn(double amount, String units)
	{
		turnLeft(amount, units);
	}
	
	//**************************************************************************
	//ARCTURN COMMANDS *********************************************************
	//**************************************************************************

	/**
	 * performs an arc turn forward along an arc of radius.
	 * If radius is positive, arcs to the right, else to the left.
	 * Moves forward along arc until a stop command.
	 * Radius must be in default units.
	 * @param radius double - radius of arc
	 * @param angle double - angle to move along arc
	 */
	public void doForwardArcTurn(double radius)
	{//TODO implement in motion
		if(!multiTasking && !linkedCommands)
			waitFor(ALL_DONE);
		
		if(DEFAULT_MOVE_UNITS.compareTo(INCHES) != 0)
			radius = doUnitConversion(radius, DEFAULT_MOVE_UNITS, INCHES);
		
		sendCommand(ROUTE_MOVE + ARC_CONT_F + (int)radius + NEWLINE);
	}
	
	/**
	 * performs an arc turn backward along an arc of radius.
	 * If radius is positive, arcs to the right, else to the left.
	 * Moves backward along arc until a stop command.
	 * Radius must be in default units.
	 * @param radius double - radius of arc
	 * @param angle double - angle to move along arc
	 */
	public void doBackwardArcTurn(double radius)
	{
		if(!multiTasking && !linkedCommands)
			waitFor(ALL_DONE);
		
		if(DEFAULT_MOVE_UNITS.compareTo(INCHES) != 0)
			radius = doUnitConversion(radius, DEFAULT_MOVE_UNITS, INCHES);
		
		sendCommand(ROUTE_MOVE + ARC_CONT_B + (int)radius + NEWLINE);
	}
	
	/**
	 * performs an arc turn of distance along an arc of radius.
	 * If radius is positive, arcs to the right, else to the left.
	 * Moves forward along arc if dist > 0, else backward.
	 * Both radius and distance must be in default units.
	 * @param radius double - radius of arc
	 * @param distance double - dist to move along arc
	 */
	public void doDistArcTurn(double radius, double distance)
	{
		if(!multiTasking && !linkedCommands)
			waitFor(ALL_DONE);
		
		if(DEFAULT_MOVE_UNITS.compareTo(INCHES) != 0)
		{
			radius = doUnitConversion(radius, DEFAULT_MOVE_UNITS, INCHES);
			distance = doUnitConversion(distance, DEFAULT_MOVE_UNITS, INCHES);
		}
		double circumference = PI * radius * 2;
		double angle = distance/circumference * 360;
		
		doAngleArcTurn(radius, angle);
	}
	
	/**
	 * performs an arc turn of distance along an arc of radius.
	 * If radius is positive, arcs to the right, else to the left.
	 * Moves forward along arc if dist > 0, else backward.
	 * Both radius and distance must be in default units.
	 * @param radius double - radius of arc
	 * @param angle double - angle to move along arc
	 */
	public void doAngleArcTurn(double radius, double angle)
	{
		if(!multiTasking && !linkedCommands)
			waitFor(ALL_DONE);
		
		if(DEFAULT_MOVE_UNITS.compareTo(INCHES) != 0)
			radius = doUnitConversion(radius, DEFAULT_MOVE_UNITS, INCHES);
		if(DEFAULT_TURN_UNITS.compareTo(DEGREES) != 0)
			angle = doUnitConversion(angle, DEFAULT_TURN_UNITS, DEGREES);
		
		sendCommand(ROUTE_MOVE + ARC + (int)radius + SPACE + (int)angle + NEWLINE);
	}
	
	//**************************************************************************
	//MOVEMENT SPEED COMMANDS **************************************************
	//**************************************************************************
	
	/**
	 * Makes the robot move and turn faster.
	 * Can be called multiple times for a given robot.
	 */
	public void increaseSpeed()
	{
		sendCommand(ROUTE_MOVE + INCREASE_SPEED + NEWLINE);
	}
	
	/**
	 * Makes the robot move and turn slower.
	 * Can be called multiple times for a given robot.
	 */
	public void decreaseSpeed()
	{
		sendCommand(ROUTE_MOVE + DECREASE_SPEED + NEWLINE);
	}
	
	/**
	 * Sets the movement speed of the robot.
	 * @param speed int - the speed of the robot.
	 */
	public void setMovementSpeed(int speed)
	{//TODO implement in motion
		sendCommand(ROUTE_MOVE + SET_MOVEMENT_SPEED + speed + NEWLINE);
	}
	
	/**
	 * Sets the movement speed of the robot.
	 * @param speed int - the speed of the robot.
	 */
	public void setTurningSpeed(int speed)
	{
		sendCommand(ROUTE_MOVE + SET_TURNING_SPEED + speed + NEWLINE);
	}
	
	/**
	 * Changes the default units used by movement commands.
	 * @param units String - the new units to be used
	 */
	public void setDefaultUnits(String units)
	{
		if(units.equalsIgnoreCase(INCHES))
			DEFAULT_MOVE_UNITS = INCHES;
		else if(units.equalsIgnoreCase(FEET))
			DEFAULT_MOVE_UNITS = FEET;
		else if(units.equalsIgnoreCase(METERS))
			DEFAULT_MOVE_UNITS = METERS;
		else if(units.equalsIgnoreCase(CENTIMETERS))
			DEFAULT_MOVE_UNITS = CENTIMETERS;
		else if(units.equalsIgnoreCase(DEGREES))
			DEFAULT_TURN_UNITS = DEGREES;
		else if(units.equalsIgnoreCase(RADIANS))
			DEFAULT_TURN_UNITS = RADIANS;
		else
			System.err.println("\'" + units + "\' is not a valid unit type"); 
	}
	
	//**************************************************************************
	//LINKED MOVEMENT METHODS **************************************************
	//**************************************************************************
	
	/**
	 * All distance-based movement commands after this statement will be queued
	 * Also clears any previous entries in queue
	 */
	public void beginLinkedCommands()
	{
		linkedCommands = true;
		LINKED_COMMANDS = "";
	}
	
	/**
	 * Stop queueing commands after this point
	 */
	public void endLinkedCommands()
	{
		linkedCommands = false;
	}	
	
	/**
	 * Send queue of commands
	 */
	public void sendLinkedCommands()
	{//TODO implement in motion
		sendCommand(ROUTE_MOVE + SEND_LINKED_COMMANDS + LINKED_COMMANDS	+ STOP + NEWLINE);
	}
	
	//**************************************************************************
	//MULTITASKING MOVEMENT METHODS ********************************************
	//**************************************************************************
	
	public void enableMultitasking()
	{
		multiTasking = true;
	}
	
	public void disableMultitasking()
	{
		multiTasking = false;
	}
	
	/**
	 * Pauses the execution of the user's program.  The current use is to allow
	 * the robot to move or turn a desired distance before telling it to stop.
	 * @param time int - The time for the program to wait.
	 */
	public void waitFor(long milliseconds)
	{
		long begin = System.currentTimeMillis();
		long end = begin + milliseconds;
		while (System.currentTimeMillis() < end);
	}
	
	public void waitFor(String commandType)
	{
		if(!commandType.equalsIgnoreCase(MOVE_DONE)
		&& !commandType.equalsIgnoreCase(SPEAK_DONE)
		&& !commandType.equalsIgnoreCase(GRIPPER_DONE)
		&& !commandType.equalsIgnoreCase(CAMERA_DONE)
		&& !commandType.equalsIgnoreCase(ALL_DONE))
			System.err.println("INVALID type to wait for: \'" + commandType + "\'");
		
		while(true)
		{
			try{Thread.sleep(250);}
			catch (InterruptedException e){}
			
			//String response = sendCommand("STATUS" + NEWLINE); 		
			//Scanner scan = new Scanner(response);
			
			/*if(scan.next().equals("true"))
				IS_MOVE_DONE = true;
			else 
				IS_MOVE_DONE = false;
			
			if(scan.next().equals("true"))
				IS_SPEAK_DONE = true;
			else 
				IS_SPEAK_DONE = false;
			
			if(scan.next().equals("true"))
				IS_CAMERA_DONE = true;
			else 
				IS_CAMERA_DONE = false;
			
			if(scan.next().equals("true"))
				IS_GRIPPER_DONE = true;
			else 
				IS_GRIPPER_DONE = false;
			 */
			
			if(commandType.equalsIgnoreCase(ALL_DONE) 
			&& client.isMOVE_DONE() && client.isSPEAK_DONE() 
			&& client.isCAMERA_DONE() && client.isGRIPPER_DONE())
				break;
			if(commandType.equalsIgnoreCase(MOVE_DONE) && client.isMOVE_DONE())
				break;
			if(commandType.equalsIgnoreCase(SPEAK_DONE) && client.isSPEAK_DONE())
				break;
			if(commandType.equalsIgnoreCase(CAMERA_DONE) && client.isCAMERA_DONE())
				break;
			if(commandType.equalsIgnoreCase(GRIPPER_DONE) && client.isGRIPPER_DONE())
				break;		
		}
	}
	
	//**************************************************************************
	//MOVEMENT HELPER METHODS **************************************************
	//**************************************************************************
	
	/**
	 * Converts from one type of unit to another
	 * @param amount double - amount of original units
	 * @param initUnits String - original units
	 * @param endUnits String - destination units
	 * @return number of default units to move
	 */
	private double doUnitConversion(double amount, String initUnits, String endUnits)
	{
		//handle capitalization
		initUnits = initUnits.toLowerCase();
		endUnits = endUnits.toLowerCase();
		
		if(endUnits.compareTo(INCHES) == 0)//for linear movement
		{
			if(initUnits.compareTo(INCHES) == 0)
				return amount;
			else if(initUnits.compareTo(FEET) == 0)
				return amount * FEET_TO_INCHES;
			else if(initUnits.compareTo(METERS) == 0)
				return amount * METERS_TO_INCHES;
			else if(initUnits.compareTo(CENTIMETERS) == 0)
				return amount / CM_TO_INCHES;
			else
				System.err.println("Invalid initial units: " + initUnits);
		}
		else if(endUnits.compareTo(DEGREES) == 0)//for turning movement
		{
			if(initUnits.compareTo(DEGREES) == 0)
				return amount;
			if(initUnits.compareTo(RADIANS) == 0)
				return amount * RAD_TO_DEGREES;
			else
				System.err.println("Invalid initial units: " + initUnits);
		}
		else
			System.err.println("Invalid destination units: " + endUnits);
		return 0;
	}
	
	/**
	 * Stops the movement of the robot.
	 */
	public void stop() 
	{
        sendCommand(ROUTE_MOVE + STOP + NEWLINE);
    }
	
	//**************************************************************************
	//SPEECH COMMANDS **********************************************************
	//**************************************************************************
	
	/**
	 * Dictates the given message verbally.
	 * @param message String - the message to be spoken.
	 */
	public void speak(String message)
	{
		System.out.println("About to speak"); 
		if(!multiTasking)
			waitFor(ALL_DONE);
		System.out.println("Can speak!");
		sendCommand(ROUTE_SPEAK + SPEAK + QUOTE + message + QUOTE + NEWLINE);
	}
	
	//**************************************************************************
	//CAMERA COMMANDS **********************************************************
	//**************************************************************************
		
	/**
	 * Take a picture from the robot's camera and return it to the user.
	 * @return the picture that the robot's camera took.
	 */
	public Picture getPicture()
	{
		if (!havePicture())
			takePicture();
		
		return picture;
	}
	
	/**
	 * Take a picture with the robot's camera.
	 * Use getPicture() to take a picture and return it to the user.
	 */
	public void takePicture()
	{
		if(!multiTasking)
			waitFor(ALL_DONE);
		
		if (!havePicture())
			picture = new Picture(client);
		else
			picture.retake();
	}
	
	/**
	 * Check if the robot has a picture already taken.
	 * @return true if there is currently a picture, false if there isn't
	 */
	public boolean havePicture()
	{
		if (picture != null)
			return true;
		else		
			return false;
	}
	
	/**
	 * Save an image
	 */
	public void savePicture(String filename)
	{
		picture.save(filename);
	}
	
	/**
	 * Load an image
	 */
	public void loadPicture(String filename)
	{
		picture.load(filename);
	}
	
	/**
	 * Displays the last-taken picture from the robot, or if there is no previous
	 * picture, it takes a picture and displays it.
	 */
	public void displayPicture()
	{
		if(!multiTasking)
			waitFor(ALL_DONE);
		
		if (!havePicture())
			takePicture();
		
		picture.displayPicture();
	}
	
	/**
	 * Displays the last-taken picture from the robot, or if there is no previous
	 * picture, it takes a picture and displays it.
	 */
	public void displayPicture(int width, int height)
	{
		if(!multiTasking)
			waitFor(ALL_DONE);
		
		if (!havePicture())
			takePicture();
		
		picture.displayPicture(width, height);
	}
	
	/**
	 * Displays the last-taken picture from the robot, or if there is no previous
	 * picture, it takes a picture and displays it.
	 */
	public void displayPicture(int width, int height, String title)
	{
		if(!multiTasking)
			waitFor(ALL_DONE);
		
		if (!havePicture())
			takePicture();
		
		picture.displayPicture(width, height, title);
	}
	
	/**
	 * Displays the last-taken picture from the robot, or if there is no previous
	 * picture, it takes a picture and displays it.
	 */
	public void displayPicture(int width, int height, String title, int x_pos, int y_pos)
	{
		if(!multiTasking)
			waitFor(ALL_DONE);
		
		if (!havePicture())
			takePicture();
		
		picture.displayPicture(width, height, title, x_pos, y_pos);
	}
	
	/**
	 * Displays the last-taken picture from the robot, or if there is no previous
	 * picture, it takes a picture and displays it.
	 */
	public void displayPicture(String title)
	{
		if(!multiTasking)
			waitFor(ALL_DONE);
		
		if (!havePicture())
			takePicture();
		
		picture.displayPicture(title);
	}
	
	public void hidePicture()
	{
		if(!multiTasking)
			waitFor(ALL_DONE);
		
		if (!havePicture())
			takePicture();
		
		picture.hidePicture();
	}
	
	//**************************************************************************
	//GRIPPER COMMANDS *********************************************************
	//**************************************************************************

	/**
	 * If the gripper is not already open, it opens it. If the gripper is not
	 * detected, a short error message is printed to the terminal.
	 */
	public void openGripper()
	{
		if(isGripperDetected())
		{
			if(!isGripperOpen())
			{
				sendCommand(ROUTE_GRIPPER + OPEN_GRIPPER + NEWLINE);
			}
			else{System.out.println("Gripper already open");}
		}
	}
	
	/**
	 * If the gripper is not already closed, it closes it. If the gripper is not
	 * detected, a short error message is printed to the terminal.
	 */
	public void closeGripper()
	{
		if(isGripperDetected())
		{
			if(!isGripperClosed())
			{
				sendCommand(ROUTE_GRIPPER + CLOSE_GRIPPER + NEWLINE);
			}
			else{System.out.println("Gripper already closed");}
		}
	}

	/**
	 * If the gripper is moving and multitasking is enabled, this will stop
	 * the gripper from doing whatever it is doing.
	 */
	public void stopGripper()
	{
		if(isGripperDetected())
		{
			if(isGripperOpening() || isGripperClosing())
			{
				sendCommand(ROUTE_GRIPPER + STOP_GRIPPER + NEWLINE);
			}
			else{System.out.println("Gripper is already stopped.");}
		}
	}

	/**
	 * Determines whether the gripper is detected or not
	 * @return boolean true if gripper is detected, else false.
	 */
	public boolean isGripperDetected(){return false;}
		
	/**
	 * Determines whether or not the gripper is completely open. True if it is, else false
	 * @return boolean True if the gripper is completely open, else false.
	 */
	public boolean isGripperOpen() {return false;}
	
	/**
	 * Determines whether or not the gripper is completely closed. True if it is, else false
	 * @return boolean True if the gripper is completely closed, else false.
	 */
	public boolean isGripperClosed() {return false;}
	
	/**
	 * Determines whether or not the gripper is in the process of closing. 
	 */
	public boolean isGripperClosing(){return false;} 
	
	/**
	 * Determines whether or not the gripper is in the process of opening. 
	 */
	public boolean isGripperOpening(){return false;} 
	
	/**
	 * Determines if autogrip is enabled. If it is, then true is returned, else false.
	 * @return boolean True if enabled, else false.
	 */
	public boolean isAutogripEnabled(){return false;}

	/**
	 * Enables the auto grip if it is disabled.
	 */
	public void enableAutoGrip(){}

	/**
	 * Disables the auto grip if it is enabled.
	 */
	public void disableAutoGrip(){}
	
	//**************************************************************************
	//OTHER COMMANDS ***********************************************************
	//**************************************************************************
	
	/**
	 * Helper method for sending commands
	 * Prints command to output if verbose is turned on
	 * @param command String - command to send
	 */
	private String sendCommand(String command)
	{
		if(command.startsWith(ROUTE_MOVE + SEND_LINKED_COMMANDS))
		{
			if(verbose)
			{
				System.out.println("Sending list: ");
				String[] list = command.replace(ROUTE_MOVE + SEND_LINKED_COMMANDS, "").split("[" + LINK_COM_SEPARATOR + "]");
				for(int i = 0; i < list.length; i++)
					if(list[i].length() > 4)
						System.out.print(list[i].replace(ROUTE_MOVE, ""));
			}
			client.sendCommand(ROUTE_MOVE + command.replace(ROUTE_MOVE, "").replace("\n", "") + "\n");
			return "OK";
		}
		else if(linkedCommands && command.startsWith(ROUTE_MOVE))
		{
			LINKED_COMMANDS += command + LINK_COM_SEPARATOR;
			return "holding on to linked movement commands";
		}
		else 
		{
			if(verbose)
				System.out.print("Sending command: " + command);
			System.out.flush();//dont forget to flush
			
			client.sendCommand(command);
			return "OK";
		}
	}
	
	//**************************************************************************
	//INSTANCE VARIABLES *******************************************************
	//**************************************************************************
	
	private ArchitectureClient client;//The Architecture Client Software
	private Picture picture;//the robot's current picture object
	
	//BOOLEAN CONSTANTS
	private boolean verbose;
	private boolean multiTasking;
	private boolean linkedCommands;
	
	//CONNECTION CONSTANTS
	private final String DEFAULT_ADDRESS = "127.0.0.1";//Used when no address is specified
	private final boolean DEFAULT_VERBOSE = false;//Used when no verbosity is specified
	
	//MOVEMENT COMMAND CONSTANTS
	private final String STOP = "stop ";//Used to signal the robot to stop
	private final String MOVE = "move ";//Tells the robot to move
	private final String FORWARD = "forward";//signal move forward
	private final String BACKWARD = "backward";//signal move backward
	private final String LEFT = "left";//signal turn counterclockwise
	private final String RIGHT = "right";//signal turn clockwise
	private final String ARC_CONT_B = "b cont arc ";//signal to move and turn backward
	private final String ARC_CONT_F = "f cont arc ";//signal to move and turn forward
	private final String ARC = "arc "; //command to arc turn
	
	//WAIT FOR CONSTANTS
	public static final String ALL_DONE = "all done";
	public static final String MOVE_DONE = "move done";
	public static final String SPEAK_DONE = "speak done";
	public static final String CAMERA_DONE = "camera done";
	public static final String GRIPPER_DONE = "gripper done";
	
	/*private boolean IS_MOVE_DONE = true;
	private boolean IS_SPEAK_DONE = true;
	private boolean IS_CAMERA_DONE = true;
	private boolean IS_GRIPPER_DONE = true;*/
	
	//LINKED MOVEMENT VARIABLES
	private static final String SEND_LINKED_COMMANDS = "link "; //command to send list
	private static final String LINK_COM_SEPARATOR = "|";//separator between linked commands
	private String LINKED_COMMANDS = ""; //storage for list fo commands, separated by '|'
	
	//MOVEMENT SPEED COMMANDS
	private final String INCREASE_SPEED = "increase speed";//used to increase speed 
	private final String DECREASE_SPEED = "decrease speed";//used to decrease speed
	private final String SET_MOVEMENT_SPEED = "set m ";//used to decrease speed
	private final String SET_TURNING_SPEED = "set t ";//used to decrease speed
	
	//SPEECH COMMAND CONSTANTS
	private final String SPEAK = "speak ";//Signals the robot to speak
	
	//GRIPPER COMMAND CONSTANTS
	private final String STOP_GRIPPER = "gripper stop";//Signal to stop gripper
	private final String OPEN_GRIPPER = "gripper open";//Signal to open gripper
	private final String CLOSE_GRIPPER = "gripper close";//Signal to close gripper
	
	//GENERAL COMMAND CONSTANTS
	private final String SPACE = " ";//space character
	private final String NEWLINE = "\n";//Newline character
	private final String QUOTE = "\"";//Quote character
	
	//COMMAND-SENDING CONSTANTS
	private final String ROUTE_MOVE = "ER1 ";//routes to movement module
	private final String ROUTE_SPEAK = "SPK ";//routes to speech module
	private final String ROUTE_GRIPPER = "GRP ";//routes to gripper module
	//private final String ROUTE_CAMERA = "CAM ";//routes to camera module
	//private final String ROUTE_BATTERY = "BAT ";//routes to battery module
	
	//CALCULATION CONSTANTS    constant * units = inches
	private static final double CM_TO_INCHES = 2.54;
	private static final double FEET_TO_INCHES = 0.08333333;
	private static final double METERS_TO_INCHES = 0.0254;
	private static final double RAD_TO_DEGREES = 0.0174532925;
	private static final double PI = 3.14159265;
	
	//GIVEN SPEED CONSTANTS
	public static final int LOW_MOVEMENT_SPEED = 1;
	public static final int MEDIUM_MOVEMENT_SPEED = 3;
	public static final int HIGH_MOVEMENT_SPEED = 5;	
	public static final int LOW_TURNING_SPEED = 1;
	public static final int MEDIUM_TURNING_SPEED = 3;
	public static final int HIGH_TURNING_SPEED = 5;
	
	//DEFAULT UNIT VARIABLES
	public String DEFAULT_MOVE_UNITS = "inches";
	public String DEFAULT_TURN_UNITS = "degrees";
	
	//UNIT CONSTANTS
	public static final String INCHES = "inches";
	public static final String FEET = "feet";
	public static final String METERS = "meters";
	public static final String CENTIMETERS = "centimeters";	
	public static final String DEGREES = "degrees";
	public static final String RADIANS = "radians";
}
