package bcibot;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class PictureDisplay extends JPanel implements ActionListener
{
	/**
	 * This creates a small AWT window to display a picture
	 * @param image BufferedImage - the image to display
	 * @param width int - the width of the picture
	 * @param height int - the height of the picture
	 * @param title String - what to call the window
	 */
	public PictureDisplay(BufferedImage _image, int width, int height, String title, int x_pos, int y_pos)
	{
		image = _image;
		
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JComponent newContentPane = this;
        newContentPane.setOpaque(true);
        frame.setContentPane(newContentPane);
        
        JPanel camArea = new JPanel();
        
        ImageIcon videoFrame = new ImageIcon();
        JLabel videoFeed = new JLabel(videoFrame);
        
        videoFrame.setImage(image);
        videoFeed.setIcon(videoFrame);
        
        videoFeed.setPreferredSize(new Dimension(width, height));
        
        camArea.add(videoFeed);
        add (camArea, BorderLayout.PAGE_START);
        
        //Create the menu bar.
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        saveImageMenuItem = new JMenuItem("Save");
        //saveImageMenuItem.setAction(saveImageMenuItemAction);
        closeMenuItem = new JMenuItem("Close");
        //closeMenuItem.setAction(closeMenuItemAction);
        saveImageMenuItem.addActionListener(this);
        closeMenuItem.addActionListener(this);
        fileMenu.add(saveImageMenuItem);
        fileMenu.add(closeMenuItem);
        frame.setJMenuBar(menuBar);
        
        //Display the window.
        frame.setLocation(x_pos, y_pos);
        frame.requestFocus();
        frame.pack();
        frame.setVisible(true);
        updateUI();
	}
	
	public void actionPerformed(ActionEvent action) 
	{
		if(action.getSource().equals(closeMenuItem))
			frame.dispose();
		if(action.getSource().equals(saveImageMenuItem))
		{
			FileDialog save_dialog = new FileDialog(frame, "Save image as PNG...", FileDialog.SAVE);
			save_dialog.setFile("C:\\Documents and Settings\\csuser\\Start Menu\\My Documents\\My Pictures\\myPicture.png");
			save_dialog.setVisible(true);
			String filename = save_dialog.getDirectory() + save_dialog.getFile();
			if(filename != null)
			{
				try {
					ImageIO.write(image, "PNG", new File(filename));
				} catch (IOException e) {
					System.err.println("Failed to save " + filename);
				}
			}
		}
	}
	
	public void Hide()
	{
		frame.setVisible(false);
        setVisible(false);
		updateUI();
		frame.dispose();
	}
	
	//**************************************************************************
	//Instance Variables********************************************************
	//**************************************************************************
	
	private static final long serialVersionUID = 1L;
	private BufferedImage image;
	private JFrame frame;
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem saveImageMenuItem, closeMenuItem;
	//private Action saveImageMenuItemAction, closeMenuItemAction;
}
