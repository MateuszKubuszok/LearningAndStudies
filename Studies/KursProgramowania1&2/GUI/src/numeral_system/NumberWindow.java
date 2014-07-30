package numeral_system;

import java.awt.Button;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import basement.DefaultWindow;

public class NumberWindow extends DefaultWindow {
	private TextField Basement;
	private TextField Number;
	private Label[] Results;
	
	public NumberWindow () {
		super ();
		this.startup ();
	}
	
	public NumberWindow (String title) {
		super (title);
		this.startup ();
	}
	
	private void startup () {
		this.setSize (500, 400);
		
		Label BasementLabel = new Label ("Converted number's basement");
		this.Basement = new TextField ("Enter basement here");
		Label NumberLabel = new Label ("Converted number");
		this.Number = new TextField ("Enter converted number here");
		Button Check = new Button ("Convert");
		this.Results = new Label[15];
		int i;
		for (i = 0; i < 15; i++)
			this.Results [i] = new Label ();
		
		Check.addActionListener (new InputListener (this));
		
		this.setLayout (new GridLayout (10, 1, 10, 10));
		this.add (BasementLabel);
		this.add (this.Basement);
		this.add (NumberLabel);
		this.add (this.Number);
		this.add (Check);
		for (i = 0; i < 15; i++)
			this.add (this.Results [i]);
		
		this.setVisible (true);
	}
	
	public void parse () {		
		String SBasement = this.Basement.getText ();
		String SNumber = this.Number.getText ();
		
		for (int i = 0; i < 15; i++)
			this.Results [i].setText ("");
		
		if (SBasement.isEmpty () || SNumber.isEmpty ())
			return;
		
		Number N;
		int Basement;
		
		try {
            Basement = Integer.parseInt (SBasement);
            N = new Number (SNumber.toLowerCase (), Basement);
        } catch (NumberFormatException ex) {
        	this.Results [0].setText (SBasement + " is not a natural number!");
            return;
        } catch (NumberException ex3) {
        	this.Results [0].setText (ex3.getMessage ());
            return;
        }
		
		
        for (int i = 0; i < 15; i++)
        	try {
	            this.Results [i].setText( (i+2)+": " + N.form (i+2) );
			} catch (Exception ex) {
				this.Results [i].setText ( ex.getMessage () );
			}
	}
}

class InputListener implements ActionListener {
	public NumberWindow Window;
	
	public InputListener () {}
	
	public InputListener (NumberWindow window) {
		this.Window = window;
	}
	
	public void actionPerformed (ActionEvent e) {
		if (this.Window == null)
			return;
		
		this.Window.parse ();
	}
}