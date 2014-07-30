package roman_arabic;

import java.awt.Button;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import basement.DefaultWindow;

public class RomanArabicWindow extends DefaultWindow {
	private TextField Input;
	private Label Result;
	
	public RomanArabicWindow () {
		super ();
		this.startup ();
	}
	
	public RomanArabicWindow (String title) {
		super (title);
		this.startup ();
	}
	
	private void startup () {
		this.setSize (450, 150);
		
		Label InputLabel = new Label ("Converted number");
		this.Input = new TextField ("Enter converted number here");
		Button Check = new Button ("Convert");
		this.Result = new Label ();
		
		Check.addActionListener (new InputListener (this));
		
		this.setLayout (new GridLayout (4, 1, 10, 10));
		this.add (InputLabel);
		this.add (Input);
		this.add (Check);
		this.add (this.Result);
		
		this.setVisible (true);
	}
	
	public void parse () {		
		String Input = this.Input.getText ();
		
		if (Input.isEmpty ()) {
			this.result ("");
			return;
		}
		
		int n;
		
		try {
			n = Integer.parseInt (Input);
		} catch (NumberFormatException ex) {
			try {
				n = RomanArabic.roman2arabicic (Input.toUpperCase ());
			} catch (RomanArabicException ex2) {
				this.result (ex2.getMessage ());
				return;
			}
			
			this.result ("" + n);
			return;
		}
		
		try {
			this.result (RomanArabic.arabic2roman (n));
		} catch (RomanArabicException ex3) {
			this.result (ex3.getMessage ());
		}
	}
	
	private void result (String message) {
		this.Result.setText (message);
	}
}

class InputListener implements ActionListener {
	public RomanArabicWindow Window;
	
	public InputListener () {}
	
	public InputListener (RomanArabicWindow window) {
		this.Window = window;
	}
	
	public void actionPerformed (ActionEvent e) {
		if (this.Window == null)
			return;
		
		this.Window.parse ();
	}
}
