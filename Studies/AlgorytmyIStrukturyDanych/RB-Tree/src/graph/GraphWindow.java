package graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import observer.Observer;
import rb_tree.RBColor;
import rb_tree.RBNode;
import coordinate.Coordinator;

@SuppressWarnings("serial")
public class GraphWindow extends JFrame implements Observer, Runnable {
	private Coordinator	Controller;
	
	private int Width;
	private int Height;
	private int ReservedHeight;
	private int Reserved;
	private int LeftMargin;
	private int TopMargin;
	private int TreeHeight;
	
	public GraphWindow (Coordinator controller) {
		Controller = controller;
		
		GraphListener Listener = new GraphListener ();
		addKeyListener (Listener);
		addWindowListener (Listener);
		setMinimumSize (new Dimension (640, 480));
		setResizable (true);
		setSize (640, 480);
		setTitle ("RB-Tree graph");
	}
	
	public void inform () {
		Thread Display = new Thread (this);
		Display.setPriority (Thread.MAX_PRIORITY);
		Display.start ();
		try {
			Display.join ();
		} catch (Exception ex) {
			System.out.println (ex.getMessage ());
		}
	}
	
	@SuppressWarnings("deprecation")
	public void run () {
		switch (Controller.Tree.getViewPriority ()) {
			default:
				if (Controller.Step == 0)
					return;
			case 0:
				if (Controller.Delay == 0)
					Controller.Execution.Action.suspend ();
				
				repaint ();
				
				if (Controller.Delay > 0)
					Controller.freeze ();
		}
	}
	
	public void paint (Graphics oldImage) {
		Image 		NewImage = createImage (getWidth (), getHeight ());
		Graphics	gDC = NewImage.getGraphics ();
		
		gDC.setColor (Color.WHITE);
		gDC.fillRect (0, 0, getWidth (), getHeight ());
		gDC.setFont (new Font (Font.MONOSPACED, Font.PLAIN, 9));
		
		String	Message = Controller.Tree.getMessage ();
		
		int MessageHeight = 10;
		
		ReservedHeight = MessageHeight;
		
		obtainSizes ();
		
		drawInorder (gDC);
		
		gDC.setColor (Color.DARK_GRAY);
		gDC.drawString (Message, LeftMargin, Reserved + MessageHeight);
		
		if (Controller.Tree.Root != Controller.Tree.Nil)
			drawNodes (gDC, (RBNode) Controller.Tree.Root, 1, 1);
		
		oldImage.drawImage (NewImage, 0, 0, this);
	}
	
	private void drawInorder (Graphics gDC) {
		RBNode	x = (RBNode) Controller.Tree.maximum ();
		double	HeightUnit = x.Key > 0 ? ((double) (this.Height-ReservedHeight*2)) / ((double) x.Key) : 1.0,
				Width = ((double) this.Width) / ((double) Controller.Size),
				C = 0;
		
		x = (RBNode) Controller.Tree.minimum ();
		while (x != Controller.Tree.Nil) {
			int Height = (int) (HeightUnit * ((double) x.Key));
			
			if (x.Color == RBColor.RED)
				gDC.setColor (Color.RED);
			else
				gDC.setColor (Color.BLACK);
			
			gDC.fillRect ((int) (LeftMargin + (C)*Width), (int) (Reserved + (this.Height - ReservedHeight) - (double) Height), ((int) (Width)) + 1, (int) Height);
			gDC.drawString (""+x.Key, (int) (LeftMargin + (C++)*Width), (int) (Reserved + this.Height));
			x = (RBNode) Controller.Tree.successor (x);
		}
	}
	
	private void drawNode (Graphics gDC, RBNode x, int position, int depth) {
		int Positions = (int) Math.pow ((double) 2, (double) (depth-1)),
			PosX = (int) ((((double) position) - 0.5) * ((double) Width / (double) Positions)) + LeftMargin,
			PosY = (int) ((((double) depth) - 0.5) * ((double) Height / (double) TreeHeight)) + TopMargin;
		
		if (x.Color == RBColor.RED)
			gDC.setColor (Color.RED);
		else
			gDC.setColor (Color.BLACK);
		
		gDC.fillRect (PosX-5, PosY-5, 10, 10);
		
		if (x.Color != RBColor.RED)
			gDC.setColor (Color.RED);
		else
			gDC.setColor (Color.BLACK);
		
		gDC.drawString ("" + x.Key, PosX-5, PosY-5);
	}
	
	private void drawNodes (Graphics gDC, RBNode x, int depth, int position) {		
		if (x.Left != Controller.Tree.Nil) {
			drawLeftLine (gDC, depth, position, ((RBNode) x.Left).Color);
			drawNodes (gDC, (RBNode) x.Left, depth+1, 2*position-1);
		} if (x.Right != Controller.Tree.Nil) {
			drawRightLine (gDC, depth, position, ((RBNode) x.Right).Color);
			drawNodes (gDC, (RBNode) x.Right, depth+1, 2*position);
		}
		
		drawNode (gDC, x, position, depth);
	}
	
	private void drawLeftLine (Graphics gDC, int parentDepth, int position, RBColor color) {
		int ParentPositions = (int) Math.pow ((double) 2, (double) (parentDepth-1)),
			ParentPosX = (int) ((((double) position) - 0.5) * ((double) Width / (double) ParentPositions)) + LeftMargin,
			ParentPosY = (int) ((((double) parentDepth) - 0.5) * ((double) Height /(double) TreeHeight)) + TopMargin,
			ChildPositions = (int) Math.pow ((double) 2, (double) (parentDepth)),
			ChildPosX = (int) ((((double) 2*position-1) - 0.5) * ((double) Width / (double) ChildPositions)) + LeftMargin,
			ChildPosY = (int) ((((double) parentDepth+1) - 0.5) * ((double) Height / (double) TreeHeight)) + TopMargin;
		
		if (color == RBColor.RED)
			gDC.setColor (Color.RED);
		else
			gDC.setColor (Color.BLACK);
		
		gDC.drawLine (ParentPosX, ParentPosY+5, ChildPosX, ChildPosY-5);
	}
	
	private void drawRightLine (Graphics gDC, int parentDepth, int position, RBColor color) {
		int ParentPositions = (int) Math.pow ((double) 2, (double) (parentDepth-1)),
			ParentPosX = (int) ((((double) position) - 0.5) * ((double) Width /(double) ParentPositions)) + LeftMargin,
			ParentPosY = (int) ((((double) parentDepth) - 0.5) * ((double) Height / (double) TreeHeight)) + TopMargin,
			ChildPositions = (int) Math.pow ((double) 2, (double) (parentDepth)),
			ChildPosX = (int) ((((double) 2*position) - 0.5) * ((double) Width / (double) ChildPositions)) + LeftMargin,
			ChildPosY = (int) ((((double) parentDepth+1) - 0.5) * ((double) Height / (double) TreeHeight)) + TopMargin;
		
		if (color == RBColor.RED)
			gDC.setColor (Color.RED);
		else
			gDC.setColor (Color.BLACK);
		
		gDC.drawLine (ParentPosX, ParentPosY+5, ChildPosX, ChildPosY-5);
	}
	
	private void obtainSizes () {
		Insets Borders = getInsets ();
		
		Reserved = Borders.top;
		Width = getWidth () - Borders.left - Borders.right;
		Height = (getHeight () - Borders.top - Borders.bottom) / 2;
		LeftMargin = Borders.left;
		TopMargin = Borders.top + Height;
		TreeHeight = Controller.Tree.getHeight (Controller.Tree.Root);
	}
	
	public class GraphListener implements WindowListener, KeyListener {
		public void keyPressed (KeyEvent arg0) {}
		public void keyReleased (KeyEvent arg0) {}
		@SuppressWarnings("deprecation")
		public void keyTyped (KeyEvent arg0) {
			if (Controller.Delay == 0 && Controller.Execution != null && Controller.Execution.isAlive ())
				Controller.Execution.Action.resume ();
		}
		public void windowActivated(WindowEvent event) {}
		public void windowClosed (WindowEvent event) {}
		public void windowClosing(WindowEvent event) {
			setVisible (false);
			Controller.finishAnimation ();
		}
		public void windowDeactivated(WindowEvent event) {}
		public void windowDeiconified(WindowEvent event) {}
		public void windowIconified(WindowEvent event) {}
		public void windowOpened(WindowEvent event) {
			repaint ();
		}
	}
}
