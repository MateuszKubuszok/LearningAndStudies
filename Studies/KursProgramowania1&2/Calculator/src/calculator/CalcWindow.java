package calculator;

import java.awt.*;
import java.awt.event.*;

import javax.swing.JFrame;

public class CalcWindow extends JFrame implements CalcInterface {
	public static void main (String[] args) {
		CalcWindow Window = new CalcWindow ();
		
		if (args.length == 2) {
			int X, Y;
			try {
				X = Integer.parseInt (args [0]);
				Y = Integer.parseInt (args [1]);
			} catch (Exception ex) {
				return;
			}
			
			System.out.println ("block size: " + X + " " + Y);
			Window.setSize (X, Y);
			Window.setResizable (false);
		}
	}
	
	public CalcWindow () {
		new CalcContent (this, (Component) this);
		
		this.setSize (640, 480);
		this.setTitle ("Binary Calculator");
		this.setVisible (true);
	}
	
	public void addListeners (HierarchyBoundsListener bounds, WindowListener window) {
		this.getContentPane ().addHierarchyBoundsListener (bounds);
		this.addWindowListener (window);
	}
}
