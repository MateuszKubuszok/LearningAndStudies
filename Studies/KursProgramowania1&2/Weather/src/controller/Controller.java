package controller;

import java.io.IOException;

import javax.swing.JOptionPane;

import Weather.Weather;
import window.Display;

public class Controller {
	public static void main (String[] args) {
		Display Display = new Display ();
		Display.getJFrame ().setVisible (true);
		
		Weather Weather = new Weather ();
		
		try {
			Weather.obtain ();
			Display.setValues (Weather.getTemp (), Weather.getFeelTemp (), Weather.getImagePath ());
		} catch (IOException e) {
			JOptionPane.showMessageDialog (Display.getJFrame (),
				"Error occured during loading data.", 
				"Error",
				JOptionPane.ERROR_MESSAGE
			);
		}
	}
}
