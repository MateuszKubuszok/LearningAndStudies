package erastotenes;

import java.awt.Button;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import basement.DefaultWindow;

public class ErastotenesWindow extends DefaultWindow {
	private TextField Input;
	private Label Result;
	private Erastotenes Sieve;
	
	public ErastotenesWindow () {
		super ();
		this.startup ();
	}
	
	public ErastotenesWindow (String title) {
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
		
		if (this.Sieve == null || n > this.Sieve.size ()) {
			try {
				this.Sieve = new Erastotenes (n);
			} catch (Exception ex) {
				this.result (ex.getMessage ());
				return;
			}
		}
		
		try {
			if (this.Sieve.prime (n))
				this.result ("\"" + Input + "\" is a prime number.");
			else
				this.result ("\"" + Input + "\" is NOT a prime number.");
		} catch (Exception ex) {
			this.result (ex.getMessage ());
		}
	}
	
	private void result (String message) {
		this.Result.setText (message);
	}
}

class InputListener implements ActionListener {
	public ErastotenesWindow Window;
	
	public InputListener () {}
	
	public InputListener (ErastotenesWindow window) {
		this.Window = window;
	}
	
	public void actionPerformed (ActionEvent e) {
		if (this.Window == null)
			return;
		
		this.Window.parse ();
	}
}

