package db_gui;

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class DBApplication extends JFrame {
	public DBApplication () {
		DBPanel Panel = new DBPanel ();
		Panel.init ();
		Panel.start ();
		add (Panel);
		addWindowListener (new WindowListener () {
			public void windowActivated(WindowEvent arg0) {}
			public void windowClosed(WindowEvent arg0) {
				System.exit (0);
			}
			public void windowClosing(WindowEvent arg0) {
				System.exit (0);
			}
			public void windowDeactivated(WindowEvent arg0) {}
			public void windowDeiconified(WindowEvent arg0) {}
			public void windowIconified(WindowEvent arg0) {}
			public void windowOpened(WindowEvent arg0) {}
		});
		setSize (640, 480);
		setMinimumSize (new Dimension (640, 480));
		setTitle ("Exams database");
		setVisible (true);
	}
}
