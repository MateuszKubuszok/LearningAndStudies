package rb_tree;

import java.util.Vector;

import observer.*;
import tree.*;

public class RBTree extends Tree {
	public int Rotations = 0;
	public int AllRotationsDeletion = 0;
	public int MaxRotationsDeletion = 0;
	public int AllRotationsInsertion = 0;
	public int MaxRotationsInsertion = 0;
	
	public RBTree () {
		Message = new String ("");
		
		Nil = new RBNode ();
		Nil.Parent = Nil.Left = Nil.Right = Nil;
		Root = Nil;
		
		Observers = new Vector<Observer> ();
	}
	
	public RBNode delete (RBNode z) {
		Comparisions = 0;
		Rotations = 0;
		Deletions++;
		
		RBNode	y = (compare (z.Left == Nil) || compare (z.Right == Nil)) ?
					z :
					(RBNode) successor (z),
				x = (RBNode) (compare (y.Left != Nil) ?
					y.Left :
					y.Right);
		
		x.Parent = y.Parent;
		
		if (compare (y.Parent == Nil))
			Root = x;
		else if (compare (y == y.Parent.Left))
			y.Parent.Left = x;
		else
			y.Parent.Right = x;
		
		if (compare (y != z))
			z.Key = y.Key;
		
		informAll (1, "Delete (T,x), key[x] = "+y.Key);
		
		if (y.Color == RBColor.BLACK)
			deleteFixup (x);
		
		informAll (0, "RB-Delete (T,x) (RB-Delete-Fixup done), key[x]="+y.Key);
		
		AllComparisionsDeletion += Comparisions; 
		MaxComparisionsDeletion = Comparisions > MaxComparisionsDeletion ? Comparisions : MaxComparisionsDeletion;
		AllRotationsDeletion += Rotations; 
		MaxRotationsDeletion = Rotations > MaxRotationsDeletion ? Rotations : MaxRotationsDeletion;
		
		return y;
	}
	
	private void deleteFixup (RBNode x) {
		RBNode w;
		
		while (compare (x != Root) && compare (x.Color == RBColor.BLACK)) {
			if (compare (x == x.Parent.Left)) {
				w = (RBNode) x.Parent.Right;
				if (compare (w.Color == RBColor.RED)) {
					w.colorize (RBColor.BLACK);
					((RBNode) x.Parent).colorize (RBColor.RED);
					this.leftRotate (x.Parent);
					w = (RBNode) x.Parent.Right;
				}
				if (compare (((RBNode) w.Left).Color == RBColor.BLACK) && compare (((RBNode) w.Right).Color == RBColor.BLACK)) {
					w.colorize (RBColor.RED);
					x = (RBNode) x.Parent;
				} else {
					if (compare (((RBNode) w.Right).Color == RBColor.BLACK)) {
						((RBNode) w.Left).colorize (RBColor.BLACK);
						w.colorize (RBColor.RED);
						rightRotate (w);
						w = (RBNode) x.Parent.Right;
					}
					w.colorize (((RBNode) x.Parent).Color);
					((RBNode) x.Parent).colorize (RBColor.BLACK);
					((RBNode) w.Right).colorize (RBColor.BLACK);
					leftRotate (x.Parent);
					x = (RBNode) Root;
				}
			} else {
				w = (RBNode) x.Parent.Left;
				if (compare (w.Color == RBColor.RED)) {
					w.colorize (RBColor.BLACK);
					((RBNode) x.Parent).colorize (RBColor.RED);
					rightRotate (x.Parent);
					w = (RBNode) x.Parent.Left;
				}
				if (compare (((RBNode) w.Right).Color == RBColor.BLACK) && compare (((RBNode) w.Left).Color == RBColor.BLACK)) {
					w.colorize (RBColor.RED);
					x = (RBNode) x.Parent;
				} else {
					if (compare (((RBNode) w.Left).Color == RBColor.BLACK)) {
						((RBNode) w.Right).colorize (RBColor.BLACK);
						w.colorize (RBColor.RED);
						leftRotate (w);
						w = (RBNode) x.Parent.Left;
					}
					w.colorize (((RBNode) x.Parent).Color);
					((RBNode) x.Parent).colorize (RBColor.BLACK);
					((RBNode) w.Left).colorize (RBColor.BLACK);
					rightRotate (x.Parent);
					x = (RBNode) Root;
				}
			}
		}
		
		x.colorize (RBColor.BLACK);
		
		informAll (2, "RB-Delete-Fixup");
	}
	
	public void insert (RBNode x) {
		super.insert ((Node) x);
		
		((RBNode) x).colorize (RBColor.RED);
		
		Rotations = 0;
		AllComparisionsInsertion -= Comparisions;
		
		insertFixup (x);
	
		informAll (0, "RB-Insert (T, x) (Insert, RB-Insert-Fixup done), key[x]="+x.Key);
		
		AllComparisionsInsertion += Comparisions; 
		MaxComparisionsInsertion = Comparisions > MaxComparisionsInsertion ? Comparisions : MaxComparisionsInsertion;
		AllRotationsInsertion += Rotations; 
		MaxRotationsInsertion = Rotations > MaxRotationsInsertion ? Rotations : MaxRotationsInsertion;
	}
	
	public void insertFixup (RBNode x) {
		Node y;
		
		while (compare (x != Root) && compare (((RBNode) x.Parent).Color == RBColor.RED)) {
			if (compare (x.Parent == x.Parent.Parent.Left)) {
				y = x.Parent.Parent.Right;
				if (compare (((RBNode) y).Color == RBColor.RED)) {
					((RBNode) x.Parent).colorize (RBColor.BLACK);
					((RBNode) y).colorize (RBColor.BLACK);
					((RBNode) x.Parent.Parent).colorize (RBColor.RED);
					x = (RBNode) x.Parent.Parent;
				} else {
					if (compare (x == x.Parent.Right)) {
						x = (RBNode) x.Parent;
						leftRotate (x);
					}
					((RBNode) x.Parent).colorize (RBColor.BLACK);
					((RBNode) x.Parent.Parent).colorize (RBColor.RED);
					this.rightRotate (x.Parent.Parent);
				}
			} else {
				y = x.Parent.Parent.Left;
				if (compare (((RBNode) y).Color == RBColor.RED)) {
					((RBNode) x.Parent).colorize (RBColor.BLACK);
					((RBNode) y).colorize (RBColor.BLACK);
					((RBNode) x.Parent.Parent).colorize (RBColor.RED);					
					x = (RBNode) x.Parent.Parent;
				} else {
					if (compare (x == x.Parent.Left)) {
						x = (RBNode) x.Parent;
						rightRotate (x);
					}
					((RBNode) x.Parent).colorize (RBColor.BLACK);
					((RBNode) x.Parent.Parent).colorize (RBColor.RED);
					leftRotate (x.Parent.Parent);
				}
			}
		}
		
		((RBNode) Root).colorize (RBColor.BLACK);
		
		informAll (2, "RB-Insert-Fixup");
	}
	
	private void leftRotate (Node x) {
		if (compare (x.Right == Nil))
			return;
		Root.Parent = Nil;
		
		Node y = x.Right;
		x.Right = y.Left;
		if (compare (y.Left != Nil))
			y.Left.Parent = x;
		y.Parent = x.Parent;
		
		if (compare (x.Parent == Nil))
			Root = y;
		else {
			if (compare (x == x.Parent.Left))
				x.Parent.Left = y;
			else
				x.Parent.Right = y;
		}
		
		y.Left = x;
		x.Parent = y;
		
		Rotations++;
		informAll (2, "Left-Rotate (T, x), key[x]="+x.Key);
	}
	
	private void rightRotate (Node x) {
		if (compare (x.Left == Nil))
			return;
		Root.Parent = Nil;
		
		Node y = x.Left;
		x.Left = y.Right;
		if (compare (y.Right != Nil))
			y.Right.Parent = x;
		y.Parent = x.Parent;
		
		
		if (compare (x.Parent == Nil))
			Root = y;
		else {
			if (compare (x == x.Parent.Right))
				x.Parent.Right = y;
			else
				x.Parent.Left = y;
		}
		
		y.Right = x;
		x.Parent = y;
		
		Rotations++;
		informAll (2, "Right-Rotate (T, x), key[x]="+x.Key);
	}
}
