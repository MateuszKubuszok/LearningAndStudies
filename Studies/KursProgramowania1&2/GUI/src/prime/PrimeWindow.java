package prime;

import java.awt.*;
import java.awt.event.*;
import basement.DefaultWindow;

public class PrimeWindow extends DefaultWindow {
	private TextField Input;
	private Label Result;
	
	public PrimeWindow () {
		super ();
		this.startup ();
	}
	
	public PrimeWindow (String title) {
		super (title);
		this.startup ();
	}
	
	private void startup () {
		this.setSize (320, 240);
		
		Label InputLabel = new Label ("Checked number");
		this.Input = new TextField ("Enter checked number here");
		Button Check = new Button ("Check number");
		this.Result = new Label ();
		
		
		Check.addActionListener (new InputListener (this));
		
		this.setLayout (new GridLayout (5, 1, 10, 10));
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
			this.result ("\"" + Input + "\" is not an integer!");
			return;
		}
		
		if (Prime.prime (n))
			this.result ("\"" + Input + "\" is a prime number.");
		else
			this.result ("\"" + Input + "\" is NOT a prime number.");
	}
	
	private void result (String message) {
		this.Result.setText (message);
	}
}

class InputListener implements ActionListener {
	public PrimeWindow Window;
	
	public InputListener () {}
	
	public InputListener (PrimeWindow window) {
		this.Window = window;
	}
	
	public void actionPerformed (ActionEvent e) {
		if (this.Window == null)
			return;
		
		this.Window.parse ();
	}
}
