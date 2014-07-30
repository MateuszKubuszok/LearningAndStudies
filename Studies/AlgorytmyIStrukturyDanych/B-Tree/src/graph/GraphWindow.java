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
import b_tree.*;
import coordinate.Coordinator;

@SuppressWarnings("serial")
public class GraphWindow extends JFrame implements Observer, Runnable {
	private Coordinator	Controller;
	
	private int Width;
	private int Height;
	private int ReservedHeight;
	private int Reserved;
	private int LeftMargin;
	private int DownMargin;
	private int TreeHeight;
	
	private int[] Inorder;
	private boolean[] InorderLeafsKeys;
	
	public GraphWindow (Coordinator controller) {
		Controller = controller;
		
		GraphListener Listener = new GraphListener ();
		addKeyListener (Listener);
		addWindowListener (Listener);
		setMinimumSize (new Dimension (640, 480));
		setResizable (true);
		setSize (640, 480);
		setTitle ("B-Tree graph");
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
				
				this.repaint ();
				
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
		
		obtainData ();
		
		drawInorder (gDC);
		
		gDC.setColor (Color.DARK_GRAY);
		gDC.drawString (Message, LeftMargin, Reserved + MessageHeight);
		
		if (Controller.Tree.Root.N > 0)
			drawKeys (gDC, Controller.Tree.Root, 1);
		
		oldImage.drawImage (NewImage, 0, 0, this);
	}
	
	private void drawInorder (Graphics gDC) {
		int 	Maximum = Controller.Tree.maximum ();
		double	HeightUnit = Maximum > 0 ? ((double) (Height-ReservedHeight*3)) / ((double) Maximum) : 1.0,
				Width = ((double) this.Width) / ((double) Controller.Size);
		
		for (double C = Inorder.length-1; C >= 0.0; C--) {
			int Height = (int) (HeightUnit * ((double) Inorder [(int) C]));
			
			if (InorderLeafsKeys [(int) C])
				gDC.setColor (Color.RED);
			else
				gDC.setColor (Color.BLACK);
			
			gDC.fillRect ((int) (LeftMargin + (C)*Width), (int) (Reserved + (this.Height - ReservedHeight*2) - (double) Height), (Width >= 1.0) ? ((int) (Width)) : 1, (int) Height);
			gDC.draw3DRect ((int) (LeftMargin + (C)*Width), (int) (Reserved + (this.Height - ReservedHeight*2) - (double) Height), (Width >= 1.0) ? ((int) (Width)) : 1, (int) Height, true);
			gDC.drawString (""+Inorder [(int) C], (int) (LeftMargin + (C)*Width), (int) (Reserved + this.Height - ReservedHeight));
		}
	}
	
	private void drawKey (Graphics gDC, int key, int depth, boolean leaf) {
		int Position;
		for (Position = 0; Inorder [Position] != key; Position++) ;
		
		int Height = (int) (((double) this.Height) / ((double) TreeHeight)),
			PosX = LeftMargin + (int) ((double) Width / (double) Inorder.length * ((double) Position + 0.5)),
			StartY = DownMargin-(Height*(TreeHeight-depth+1)),
			EndY = DownMargin-(Height*(TreeHeight-depth));
		
		if (InorderLeafsKeys [Position])
			gDC.setColor (Color.RED);
		else
			gDC.setColor (Color.BLACK);
		
		gDC.drawString(""+key, PosX, StartY);
		gDC.drawLine(PosX, StartY, PosX, EndY);
	}
	
	private void drawKeys (Graphics gDC, BTree.BNode x, int depth) {
		int FirstKeyPos,
		LastKeyPos,
		Height = (int) (((double) this.Height) / ((double) TreeHeight)),
		PosY,
		StartX,
		EndX;
		
		if (!x.Leaf) {
			for (FirstKeyPos = 0; Inorder [FirstKeyPos] != x.Children [1].Keys [1]; FirstKeyPos++) ;
			for (LastKeyPos = 0; Inorder [LastKeyPos] != x.Children [x.N+1].Keys [x.Children [x.N+1].N]; LastKeyPos++) ;
			
			PosY = DownMargin-(Height*(TreeHeight-depth));
			StartX = LeftMargin + (int) ((double) Width / (double) Inorder.length * ((double) FirstKeyPos + 0.5));
			EndX = LeftMargin + (int) ((double) Width / (double) Inorder.length * ((double) LastKeyPos + 0.5));
			
			gDC.setColor (Color.BLACK);
			gDC.drawLine (StartX, PosY, EndX, PosY);
		}
		
		for (FirstKeyPos = 0; Inorder [FirstKeyPos] != x.Keys [1]; FirstKeyPos++) ;
		for (LastKeyPos = 0; Inorder [LastKeyPos] != x.Keys [x.N]; LastKeyPos++) ;
		
		PosY = DownMargin - (int) ((double) Height * ((double) (TreeHeight-depth) + 0.5));
		StartX = LeftMargin + (int) ((double) Width / (double) Inorder.length * ((double) FirstKeyPos + 0.5));
		EndX = LeftMargin + (int) ((double) Width / (double) Inorder.length * ((double) LastKeyPos + 0.5));
		
		if (x.Leaf)
			gDC.setColor (Color.RED);
		gDC.drawLine (StartX, PosY, EndX, PosY);
		
		for (int i = 1; i <= x.N; i++)
			drawKey (gDC, x.Keys [i], depth, x.Leaf);
		
		if (!x.Leaf)
			for (int i = 1; i <= x.N+1; i++)
				drawKeys (gDC, x.Children [i], depth+1);
	}

	private void obtainData () {
		Insets Borders = getInsets ();
		
		Reserved = Borders.top;
		Width = getWidth () - Borders.left - Borders.right;
		Height = (getHeight () - Borders.top - Borders.bottom) / 2;
		LeftMargin = Borders.left;
		DownMargin = getHeight () - Borders.bottom;
		TreeHeight = Controller.Tree.getHeight ();
		Inorder = Controller.Tree.inorder ();
		InorderLeafsKeys = Controller.Tree.inorderLeafsKeys ();
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
