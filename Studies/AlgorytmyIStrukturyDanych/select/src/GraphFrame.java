import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class GraphFrame extends Frame implements Observer, WindowListener  {
	public	Container 	Array;

	private int 		StartX;
	private int 		StartY;
	private int			Thickness;
	
	private float		Ratio;
	
	public GraphFrame () {
		this.init (new Container (), "");
	}
	
	public GraphFrame (Container Array) {
		this.init (Array, "");
	}
	
	public GraphFrame (String Title) {
		this.init (new Container (), Title);
	}
	
	public GraphFrame (Container Array, String Title) {
		this.init (Array, Title);
	}
	
	public void init (Container Array, String Title) {
		this.Thickness = 6;
		
		this.Array = Array;
		this.Array.addObserver (this);
		
		this.addWindowListener (this);
		this.setSize (800, 600);
		this.setTitle (Title);
		this.setVisible (true);
		
		this.setXY (
			(this.getWidth ()-(this.Array.size ()*this.Thickness))/2,
			0
		);
		
		int Max = 1;
		for (int i = 1; i <= this.Array.size (); i++) {
			try {
			if (this.Array.get (i).Value > Max)
				Max = this.Array.get (i).Value;
			} catch (ContainerException ex) {}
		}
		this.Ratio = 550/Max;
	} 
	
	public void paint (Graphics gDC) {
		int Size = this.Array.size (),
			Value,
			Width,
			Height;
		
		for (int i = 1; i <= Size; i++) {
			try {
				Value = this.Array.get (i).Value;
				if (this.Array.get (i) == this.Array.getFollowed ())
					gDC.setColor (Color.MAGENTA);
				else
					gDC.setColor (Color.BLACK);
			} catch (ContainerException ex) {
				continue;
			}
			Width = this.Thickness-1;
			Height = (int) (Value*this.Ratio);
			
			gDC.drawRect ((i-5)*this.Thickness + this.StartX, this.getHeight () - Height - this.StartY, Width, Height);
			//gDC.drawLine (i-1 + this.StartX, this.getHeight () - this.StartY, i-1 + this.StartX, this.getHeight ()-Value - this.StartY);
			//gDC.drawLine (i-1 + this.StartX, this.getHeight () - this.StartY, i-1 + this.StartX, this.getHeight () - (int)(Value*this.Ratio) - this.StartY);
		}
		gDC.setColor (Color.RED);
		if (this.Array.getMessage () != null)
			gDC.drawString (this.Array.getMessage (), 10, 40);
	}
	
	public void refresh () {
		this.repaint ();
	}
	
	public void setXY (int x, int y) {
		this.StartX = x+10;
		this.StartY = y+10;
	}
	
	public void windowActivated(WindowEvent event) {}
	public void windowClosed (WindowEvent event) {}
	public void windowClosing(WindowEvent event) {
		this.dispose ();
	}
	public void windowDeactivated(WindowEvent event) {}
	public void windowDeiconified(WindowEvent event) {}
	public void windowIconified(WindowEvent event) {}
	public void windowOpened(WindowEvent event) {
		this.repaint ();
	}
}
