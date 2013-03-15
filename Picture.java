package bcibot;


import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import javax.imageio.ImageIO;



public class Picture
{
	/**
	 * Constructor, creates picture object
	 * @param IP - address of camera
	 * @param ArchitectureClient - reference to client
	 */

	public Picture(ArchitectureClient _client)
	{
		client = _client;

		client.sendCommand("CAM " + getLocalAddress() + " grab image" +  "\n");
		if (cam == null)
		{
			cam = new Camera();
		}
		image = cam.grabImage();
		height = image.getHeight();
		width = image.getWidth();
	}
	
	public void retake()
	{
		client.sendCommand("CAM " + getLocalAddress() + " grab image" +  "\n");
		image = cam.grabImage();
		height = image.getHeight();
		width = image.getWidth();
	}
	
	public void save(String filename)
	{
		try {
			ImageIO.write(image, "PNG", new File("C:\\Documents and Settings\\csuser\\Start Menu\\My Documents\\My Pictures\\" + filename));
		} catch (IOException e) {
			System.err.println("Failed to save " + filename);
		}
	}
	
	public void load(String filename)
	{
		try {
			ImageIO.read(new File(filename));
		} catch (IOException e) {
			System.err.println("Failed to open " + filename);
		}
	}
	
	/**
	 * Find the address of the machine the client is running on
	 * @return String The address of the local machine
	 */
	private String getLocalAddress()
	{
		String lAddress = null;
		
		try{lAddress = InetAddress.getLocalHost().toString();}
		catch(Exception e){e.printStackTrace();}
		
		int pos = lAddress.indexOf("/");
		//System.out.println("LOCAL: " + lAddress); 
		return lAddress.substring(pos + 1);
	}
	
	/**
	 * Gets a new picture from the robot.
	 * Less overhead than creating a new picture object everytime.
	 */
	public void retakePic()
	{
		client.sendCommand("CAM" + " grab image" + "\n");
		image = cam.grabImage();
	}

    /**
	 * Calls another UI class to display image
	 */
	public void displayPicture()
	{
		if(display != null)
			display.setVisible(false);
		
		display = new PictureDisplay(image, width, height, title, 20, 20);		
	}
	
	/**
	 * Calls another UI class to display image
	 */
	public void displayPicture(int width, int height)
	{
		if(display != null)
			display.setVisible(false);
		
		new PictureDisplay(image, width, height, title, 20, 20);		
	}
	
	/**
	 * Calls another UI class to display image
	 */
	public void displayPicture(int width, int height, String title)
	{
		if(display != null)
			display.setVisible(false);
		
		display = new PictureDisplay(image, width, height, title, 20, 20);		
	}
	
	public void hidePicture()
	{
		if(display != null)
			display.Hide();
	}
	
	/**
	 * Calls another UI class to display image
	 */
	public void displayPicture(String title)
	{
		if(display != null)
			display.setVisible(false);
		
		new PictureDisplay(image, width, height, title, 20, 20);		
	}
	
	/**
	 * Calls another UI class to display image
	 */
	public void displayPicture(int width, int height, String title, int x_pos, int y_pos)
	{
		if(display != null)
			display.setVisible(false);
		
		new PictureDisplay(image, width, height, title, x_pos, y_pos);		
	}

	/**
	 * Accessor method for image as 3D array of RBG values
	 * format: [height][width][R, G, B]
	 * @return image as 3D array
	 */ 
	public int[][][] getThreeDArray()
	{
		int[][][] array = new int[height][width][3];
		
		Raster ras = image.getData();
		
		for(int i = 0; i < height; i++)
			for(int j = 0; j < width; j++)
			{
				int[] RGBpixel = ras.getPixel(j, i, new int[3]);
				
				array[i][j][0] = RGBpixel[0]; //R
				array[i][j][1] = RGBpixel[1]; //G
				array[i][j][2] = RGBpixel[2]; //B
			}
		
		return array;
	}
    
    /**
     * Mutator method for image as 3D array of RBG values
     * @param array 3D array of pixels; format: [height][width][R, G, B]
     */
    public void setThreeDArray(int[][][] array)
    {
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int rgb;
        for(int i = 0; i < height; i++)
        {
            for(int j = 0; j < width; j++)
            {
                rgb = array[i][j][0];
                rgb *= 256;  // asl 8
                rgb += array[i][j][1];
                rgb *= 256;  // asl 8
                rgb += array[i][j][2];
                
                newImage.setRGB(j, i, rgb);
            }
        }
        image = newImage;
        height = image.getHeight();
        width = image.getWidth();
    }
    
    /**
     * Accessor for image.
     * @return BufferedImage returns the wrapped image
     */
    public BufferedImage getImage()
    {
    	return image;
    }
    
    /**
     * Accessor for raster version of the image.
     * @return Raster returns the wrapped image's raster
     */
    public Raster getRaster()
    {
    	return image.getData();
    }

	/**
	 * Accessor method for height of image
	 * @return height
	 */
	public int getHeight()
	{
		return height;
	}
	
	/**
	 * Accessor method for width of image
	 * @return width
	 */
	public int getWidth()
	{
		return width;
	}

	//**************************************************************************
	//Instance Variables********************************************************
	//**************************************************************************
	
	private static final long serialVersionUID = 1L;
	
	private static Camera cam;
	private BufferedImage image;
	private int height, width;
	private String title = "My Picture";
	private ArchitectureClient client;
	private PictureDisplay display;

}
