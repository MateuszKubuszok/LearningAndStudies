package gui;

import javax.swing.JTextArea;

public class Printer {
	private JTextArea Printed;
	
	public Printer (JTextArea printed) {
		Printed = printed;
	}
	
	public void clean () {
		Printed.setText ("");
	}
	
	public void print () {
		print ("");
	}
	
	public void print (String str) {
		Printed.append (str);
	}
	
	public void println () {
		println ("");
	}
	
	public void println (String str) {
		Printed.append (str + "\n");
	}
}
