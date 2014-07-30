package rb_tree;

import tree.*;

public class RBNode extends Node {
	public RBColor Color;
	
	public RBTree Owner;
	
	public RBNode () {
		Color = RBColor.BLACK;
	}
	
	public RBNode (RBTree t) {
		super ((Tree) t);
		Color = RBColor.BLACK;
		Owner = t;
	}
	
	public void colorize (RBColor color) {
		Color = color;
		
		if (Owner != null)
			Owner.informAll (2, "Paint x "+(Color == RBColor.BLACK ? "BLACK" : "RED")+", key[x]="+Key);
	}
}