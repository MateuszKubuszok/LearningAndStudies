package window;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import applet.Display;

/**
 * 
 * @author Mateusz Kubuszok
 *
 */
@SuppressWarnings("serial")
public class DisplayWindow extends JFrame {
	/**
	 * Przechowuje applet.
	 */
	public Display DApplet;
	
	/**
	 * Tworzy okno zawieraj¹ce applet.
	 * 
	 * @see		applet.Display
	 * 
	 * @param	n		liczba komorek w poziomie
	 * @param	m		liczba komórek w pionie
	 * @param	k		liczba milisekund miêdzy kolejnymi odœwie¿eniami +/- 50% (misi byæ wiêksza od 0)
	 * @param	cells	liczba ¿ywych komórek na starcie (musi byæ nie wiêksza ni¿ iloœæ dost.êpnych komórek)
	 */
	public DisplayWindow (int n, int m, int k, int cells) {
		DApplet = new Display (n, m, k, cells);
		add (DApplet);
		
		addWindowListener (new WindowListener () {
			public void windowActivated(WindowEvent event) {}
			public void windowClosed (WindowEvent event) {}
			public void windowClosing(WindowEvent event) {
				System.exit (0);
			}
			public void windowDeactivated(WindowEvent event) {}
			public void windowDeiconified(WindowEvent event) {}
			public void windowIconified(WindowEvent event) {}
			public void windowOpened(WindowEvent event) {
				repaint ();
			}
		});
		
		setTitle ("Life Game");
		setSize (640, 480);
		setVisible (true);
		
		DApplet.init ();
		DApplet.start ();
	}
	
	/**
	 * Uruchamia aplet jako program.
	 * 
	 * @param args pobiera parametry programu
	 */
	public static void main (String[] args) {
		int n, m, k, cells;
		try {
			n = 	Integer.parseInt (args [0]);
			m = 	Integer.parseInt (args [1]);
			k = 	Integer.parseInt (args [2]);
			cells = Integer.parseInt (args [3]);
			
			new DisplayWindow (n, m, k, cells);
		} catch (Exception ex) {
			System.out.println (ex.getMessage ());
			JOptionPane.showMessageDialog (null, "Not enough parameters passed!", "Error", 0);
			System.exit (0);
		}
	}
}
