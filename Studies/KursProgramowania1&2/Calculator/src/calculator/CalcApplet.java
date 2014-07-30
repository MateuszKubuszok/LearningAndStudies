package calculator;

import java.awt.*;
import java.awt.event.*;

import javax.swing.JApplet;

public class CalcApplet extends JApplet implements CalcInterface {
	private CalcContent Content;
	
	public CalcApplet () {
		Content = new CalcContent (this, (Component) this);
	}
	
	public void init () {
		System.out.println ("wtf " + size ().width + " " + size ().height);
		Content.setSize ();
	}
	
	public void start () {
		Content.setSize ();
	}
	
	public void paint () {
		Content.setSize ();
	}
	
	public int getHeight () {
		return size ().height;
	}
	
	public int getWidth () {
		return size ().width;
	}
	
	public void addListeners (HierarchyBoundsListener bounds, WindowListener window) {
		this.getContentPane ().addHierarchyBoundsListener (bounds);
	}
}
