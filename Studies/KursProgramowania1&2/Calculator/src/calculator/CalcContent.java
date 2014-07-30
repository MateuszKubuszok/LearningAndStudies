package calculator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.*;

import javax.swing.*;

import binary_system.BinarySystem;
import binary_system.BinarySystem.DisplayException;

public class CalcContent {
	public JLabel Display;
	public JButton Digit0;
	public JButton Digit1;
	public JButton Info;
	public JButton Multiply;
	public JButton Divide;
	public JButton Modulo;
	public JButton Plus;
	public JButton Minus;
	public JButton Equals;
	
	public CalcInterface Container;
	private Component Frame;
	
	private BinarySystem BS;
	
	public CalcContent (CalcInterface container, Component component) {
		this.Container = container;
		this.Container.addListeners (
			new ResizeListener (),
			new CalcWindowListener ()
		);
		this.Container.setLayout (null);
		
		this.Frame = component;
		
		this.BS = new BinarySystem ();
		
		ActionListener Action = new CalcActionListener ();
		KeyListener Key = new CalcKeyListener ();
		
		try {
			this.Display =	new JLabel (this.BS.refresh ());
		} catch (Exception ex) {}
		this.Container.add (this.Display);
		
		this.Digit0 =	new JButton ("0");
		this.Digit1 =	new JButton ("1");
		this.Info =		new JButton ("Info");
		this.Multiply =	new JButton ("*");
		this.Divide =	new JButton ("/");
		this.Modulo =	new JButton ("%");
		this.Plus =		new JButton ("+");
		this.Minus =	new JButton ("-");
		this.Equals =	new JButton ("=");
		
		this.Digit0.setForeground (Color.BLUE);
		this.Digit1.setForeground (Color.BLUE);
		
		this.Multiply.setForeground (Color.RED);
		this.Divide.setForeground (Color.RED);
		this.Modulo.setForeground (Color.RED);
		
		this.Plus.setForeground (Color.ORANGE);
		this.Minus.setForeground (Color.ORANGE);
		
		this.Info.setForeground (Color.DARK_GRAY);
		this.Equals.setForeground (Color.DARK_GRAY);
		
		JButton[] Buttons = {
				this.Digit0,
				this.Digit1,
				this.Info,
				this.Multiply,
				this.Divide,
				this.Modulo,
				this.Plus,
				this.Minus,
				this.Equals
		};
		
		for (int i = 0; i < Buttons.length; i++) {
			Buttons [i].addActionListener (Action);
			Buttons [i].addKeyListener (Key);
			Buttons [i].setBackground (Color.WHITE);
			this.Container.add (Buttons [i]);
		}
		
		this.setSize ();
	}
	
	public void setSize () {
		JComponent[] Components = {
				this.Digit0,
				this.Digit1,
				this.Info,
				this.Multiply,
				this.Divide,
				this.Modulo,
				this.Plus,
				this.Minus,
				this.Equals
		};
		
		Insets WInsets = this.Container.getInsets ();
		
		int Width = this.Container.getWidth () - WInsets.right - WInsets.left,
			Height = this.Container.getHeight () - WInsets.bottom - WInsets.top;
		
		System.out.println (Width + " " + Height);
		if ((Width < 200 || Height < 100) && Width != 0 && Height != 0) {
			JOptionPane.showMessageDialog (Frame, "Window's size is too small! Program will exit!", "Error", 0);
			System.exit (0);
		}
		
		this.Display.setFont (this.Display.getFont ().deriveFont ((float) ((Width)*(0.08) < (Height)*(0.22) ? (Width)*(0.08) : (Height)*(0.22))));
		
		Font NewSize = this.Display.getFont ().deriveFont ((float) ((Width)*(0.08) < (Height)*(0.25) ? (Width)*(0.08) : (Height)*(0.25)));
		for (int i = 0; i < Components.length; i++)
			Components [i].setFont (NewSize);
		
		
		this.Display.setSize	(Width/1,	Height/4);
		this.Digit0.setSize		(Width/3,	Height/4);
		this.Digit1.setSize		(Width/3,	Height/4);
		this.Info.setSize		(Width/3,	Height/4);
		this.Multiply.setSize	(Width/3,	Height/4);
		this.Divide.setSize		(Width/3,	Height/4);
		this.Modulo.setSize		(Width/3,	Height/4);
		this.Plus.setSize		(Width/3,	Height/4);
		this.Minus.setSize		(Width/3,	Height/4);
		
		this.Display.setBounds	(Width*0/3,	Height*0/4,	Width/1,	Height/4);
		this.Digit0.setBounds	(Width*0/3,	Height*1/4,	Width/3,	Height/4);
		this.Digit1.setBounds	(Width*1/3,	Height*1/4,	Width/3,	Height/4);
		this.Info.setBounds		(Width*2/3,	Height*1/4,	Width/3,	Height/4);
		this.Multiply.setBounds	(Width*0/3,	Height*2/4,	Width/3,	Height/4);
		this.Divide.setBounds	(Width*1/3,	Height*2/4,	Width/3,	Height/4);
		this.Modulo.setBounds	(Width*2/3,	Height*2/4,	Width/3,	Height/4);
		this.Plus.setBounds		(Width*0/3,	Height*3/4,	Width/3,	Height/4);
		this.Minus.setBounds	(Width*1/3,	Height*3/4,	Width/3,	Height/4);
		this.Equals.setBounds	(Width*2/3,	Height*3/4,	Width/3,	Height/4);
	}
	
	private void viewInfo () {
		String Content = "Binary Calculator made as a Java practice's program\n"
			+"Author: Mateusz Kubuszok";
		JOptionPane.showMessageDialog (this.Frame, Content, "About", 1);
	}
	
	public class CalcActionListener implements ActionListener {
		public void actionPerformed (ActionEvent event) {
			try {
				if (event.getSource () == Digit0)
					Display.setText (BS.pressed0 ());
				else if (event.getSource () == Digit1)
					Display.setText (BS.pressed1 ());
				else if (event.getSource () == Divide)
					Display.setText (BS.pressedDivide ());
				else if (event.getSource () == Equals)
					Display.setText (BS.pressedEquals ());
				else if (event.getSource () == Plus)
					Display.setText (BS.pressedPlus ());
				else if (event.getSource () == Minus)
					Display.setText (BS.pressedMinus ());
				else if (event.getSource () == Modulo)
					Display.setText (BS.pressedModulo ());
				else if (event.getSource () == Multiply)
					Display.setText (BS.pressedMultiply ());
				else if (event.getSource () == Info)
					viewInfo ();
			} catch (DisplayException ex) {
				JOptionPane.showMessageDialog (Frame, ex.getMessage (), "Error", 0);
				Display.setText ("0");
			}
		}
	}
	
	public class CalcKeyListener implements KeyListener {
		public void keyPressed (KeyEvent event) {
			System.out.println ("pressed:"+event.getKeyChar ());
			try {
				if (event.getKeyChar () == '0')
					Display.setText (BS.pressed0 ());
				else if (event.getKeyChar () == '1')
					Display.setText (BS.pressed1 ());
				else if (event.getKeyChar () == '/')
					Display.setText (BS.pressedDivide ());
				else if (event.getKeyChar () == '\n')
					Display.setText (BS.pressedEquals ());
				else if (event.getKeyChar () == '+')
					Display.setText (BS.pressedPlus ());
				else if (event.getKeyChar () == '-')
					Display.setText (BS.pressedMinus ());
				else if (event.getKeyChar () == 'm')
					Display.setText (BS.pressedModulo ());
				else if (event.getKeyChar () == '*')
					Display.setText (BS.pressedMultiply ());
				else if (event.getKeyCode () == 112 /* f1 */)
					viewInfo ();
				else
					System.out.println ("key:"+event.getKeyChar());
			} catch (DisplayException ex) {
				JOptionPane.showMessageDialog (Frame, ex.getMessage (), "Error", 0);
				Display.setText ("0");
			}
		}
		public void keyReleased (KeyEvent event) {
			System.out.println ("released:"+event.getKeyChar ());
		}
		public void keyTyped (KeyEvent event) {
			System.out.println ("typed:"+event.getKeyChar ());
		}
	}
	
	public class ResizeListener implements HierarchyBoundsListener {
		public void ancestorMoved (HierarchyEvent event) {}
		public void ancestorResized (HierarchyEvent event) {
			setSize ();
		}
	}
	
	public class CalcWindowListener implements WindowListener {
		public void windowActivated (WindowEvent event) {}
		public void windowClosed (WindowEvent event) {}
		public void windowClosing (WindowEvent event) {
			System.exit (0);
		}
		public void windowDeactivated (WindowEvent event) {}
		public void windowDeiconified (WindowEvent event) {}
		public void windowIconified (WindowEvent event) {}
		public void windowOpened (WindowEvent event) {}
	}
}
