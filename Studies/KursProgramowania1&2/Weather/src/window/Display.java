package window;

import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Event;
import java.awt.BorderLayout;
import java.awt.Graphics;

import javax.imageio.ImageIO;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.KeyStroke;
import java.awt.Point;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JFrame;
import javax.swing.JDialog;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JInternalFrame;

public class Display {

	private JFrame jFrame = null;
	private JPanel jContentPane = null;
	
	private int Temp;
	private int FeelTemp;
	private String ImagePath;  //  @jve:decl-index=0:
	private java.awt.Image Image;  //  @jve:decl-index=0:
	
	public void setValues (int temp, int feelTemp, String image) {
		Temp = temp;
		FeelTemp = feelTemp;
		
		try {
			URL URL = new URL (image);
			Image = ImageIO.read (URL);
		} catch (Exception e) {}
		
		getJFrame ().repaint ();
	}
	
	/**
	 * This method initializes jFrame
	 * 
	 * @return javax.swing.JFrame
	 */
	public JFrame getJFrame() {
		if (jFrame == null) {
			jFrame = new DrawingArea();
			jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jFrame.setSize(200, 200);
			jFrame.setContentPane(getJContentPane());
			jFrame.setResizable(false);
			jFrame.setTitle("Weather at Wroc³aw");
		}
		return jFrame;
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
		}
		return jContentPane;
	}

	private class DrawingArea extends JFrame {
		public void paint (Graphics gDC) {
			gDC.clearRect(0, 0, getWidth (), getHeight ());
			gDC.drawImage (Image, 0, 0, null);
			gDC.drawString ("Temperature: "+Temp+" C", 20, 150);
			gDC.drawString ("Felt temperature: "+FeelTemp+" C", 20, 170);
		}
	}

	/**
	 * Launches this application
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Display application = new Display();
				application.getJFrame().setVisible(true);
			}
		});
	}

}
