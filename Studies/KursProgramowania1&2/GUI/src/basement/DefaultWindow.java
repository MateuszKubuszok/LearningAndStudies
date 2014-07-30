package basement;

import java.awt.*;
import java.awt.event.*;

public class DefaultWindow extends Frame implements WindowListener {
	public DefaultWindow () {
		this.startup ();
	}
	
	public DefaultWindow (String title) {
		this.setTitle (title);
		this.startup ();
	}
	
	private void startup () {
		this.setSize (640, 480);
		this.setResizable (false);
		this.setFont (new Font (Font.SANS_SERIF, Font.PLAIN, 12));
		
		this.addWindowListener (this);
	}
	
	public void windowOpened (WindowEvent e) {}
	public void windowClosing (WindowEvent e) {
		this.dispose ();
		System.exit (0);
	}
	public void windowClosed (WindowEvent e) {}
	public void windowIconified (WindowEvent e) {}
	public void windowDeiconified (WindowEvent e) {}
	public void windowActivated (WindowEvent e) {}
	public void windowDeactivated (WindowEvent e) {}
}

