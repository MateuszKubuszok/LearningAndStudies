package calculator;

import java.awt.Component;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.*;

public interface CalcInterface {
	public Component add (Component comp);
	
	public void add (Component comp, Object constains); 
	
	public void addListeners (HierarchyBoundsListener bounds, WindowListener window);
	
	public int getHeight ();
	
	public Insets getInsets ();
	
	public int getWidth ();
	
	public void setLayout (LayoutManager manager);
}
