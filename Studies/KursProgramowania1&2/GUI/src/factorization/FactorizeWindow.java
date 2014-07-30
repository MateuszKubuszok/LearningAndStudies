package factorization;

import java.awt.Button;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import basement.DefaultWindow;

public class FactorizeWindow extends DefaultWindow {
	private TextField Input;
	private Label Result;
	private Factorize Sieve;
	
	public FactorizeWindow () {
		super ();
		this.startup ();
	}
	
	public FactorizeWindow (String title) {
		super (title);
		this.startup ();
	}
	
	private void startup () {
		this.setSize (400, 240);
		
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
				this.Sieve = new Factorize (n);
			} catch (Exception ex) {
				this.result (ex.getMessage ());
				return;
			}
		}
		
		try {
			boolean First = true;
			int[] Factors = this.Sieve.factorize (n);
			String Result = new String ("");
			
			for (int i = 2; i <= n; i++)
                if (Factors [i-2] > 0) {
                    Result += ((First ? "" : " * ") + i + "^" + Factors [i-2]);
                    First = false;
                }
			
			this.result (Result);
		} catch (Exception ex) {
			this.result (ex.getMessage ());
		}
	}
	
	private void result (String message) {
		this.Result.setText (message);
	}
}

class InputListener implements ActionListener {
	public FactorizeWindow Window;
	
	public InputListener () {}
	
	public InputListener (FactorizeWindow window) {
		this.Window = window;
	}
	
	public void actionPerformed (ActionEvent e) {
		if (this.Window == null)
			return;
		
		this.Window.parse ();
	}
}


