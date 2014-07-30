package tree;

import java.util.Vector;

import observer.*;

public class Tree implements Observable {
	public Node Nil;
	public Node Root;
	
	public int Comparisions = 0;
	public int Insertions = 0;
	public int Deletions = 0;
	public int AllComparisionsDeletion = 0;
	public int MaxComparisionsDeletion = 0;
	public int AllComparisionsInsertion = 0;
	public int MaxComparisionsInsertion = 0;
	
	protected String Message;
	
	/**
	 * Priority equals to:
	 * 0 - RB changing operation (rb-insert/rb-delete)
	 * 1 - BST changing operation (insert/delete)
	 * 2 - RB changing operation (left/right-rotate/rb-delete-fixup)
	 */
	protected int ViewPriority;
	
	protected Vector<Observer> Observers;
	
	public Tree () {
		Message = new String ("");
		
		Nil = new Node (this);
		Root = Nil;
		
		Observers = new Vector<Observer> ();
	}
	
	public void addObserver (Observer object) {
		Observers.add (object);
	}
	
	public boolean compare (boolean result) {
		Comparisions ++;
		return result;
	}
	
	public Node delete (Node z) {
		Comparisions = 0;
		Deletions++;
		
		Node 	y = (compare (z.Left == Nil) || compare (z.Right == Nil)) ?
					z :
					successor (z),
				x = compare (y.Left == z) ?
					y.Left :
					y.Right;

		if (compare (x != Nil))
			x.Parent = y.Parent;
		
		if (compare (y.Parent == Nil))
			Root = x;
		else if (compare (y == y.Parent.Left))
			y.Parent.Left = x;
		else
			y.Parent.Right = x;
		
		if (compare (y != z))
			z.Key = y.Key;
		
		informAll (1, "Delete (T,x), key[x]="+y.Key);
		
		AllComparisionsDeletion += Comparisions; 
		MaxComparisionsDeletion = Comparisions > MaxComparisionsDeletion ? Comparisions : MaxComparisionsDeletion;
		
		return y;
	}
	
	public int getHeight (Node x) {
		if (x == Nil)
			return 0;
		
		int LeftHeight = getHeight (x.Left),
			RightHeight = getHeight (x.Right);
		 
		return 1 + ((LeftHeight > RightHeight) ? LeftHeight : RightHeight);
	}
	
	public String getMessage () {
		return Message;
	}
	
	public int getViewPriority () {
		return ViewPriority;
	}
	
	public void informAll () {
		for (int i = 0; i < Observers.size (); i++)
			Observers.get (i).inform ();
	}
	
	public void informAll (int priority, String message) {
		ViewPriority = priority;
		Message = message;
		informAll ();
	}
	
	public String inorder () {
		return inorder (Root);
	}
	
	public String inorder (Node x) {
		String result = new String ("");
		
		if (x != Nil) {
			if (x.Left != Nil)
				result += inorder (x.Left);
			
			result += x.Key + " ";
			
			if (x.Right != Nil)
				result += inorder (x.Right);
		}
		
		return result;
	}
	
	public void insert (Node z) {
		Comparisions = 0;
		Insertions++;
		
		Node	y = Nil,
				x = Root;
		
		while (compare (x != Nil)) {
			y = x;
			if (z.Key < x.Key)
				x = x.Left;
			else
				x = x.Right;
		}
		
		z.Parent = y;
		
		if (compare (y == Nil))
			Root = z;
		else {
			if (compare (z.Key < y.Key))
				y.Left = z;
			else
				y.Right = z;
		}
		
		informAll (1, "Insert (T,x), key[x]="+z.Key);
		
		AllComparisionsInsertion += Comparisions; 
		MaxComparisionsInsertion = Comparisions > MaxComparisionsInsertion ? Comparisions : MaxComparisionsInsertion;
	}
	
	public synchronized Node maximum () {
		return this.maximum (Root);
	}
	
	public synchronized Node maximum (Node x) {
		while (x.Right != Nil)
			x = x.Right;
		return x;
	}
	
	public synchronized Node minimum () {
		return minimum (Root);
	}
	
	public synchronized Node minimum (Node x) {
		while (x.Left != Nil)
			x = x.Left;
		return x;
	}
	
	public synchronized Node predecessor (Node x) {
		if (x.Left != Nil)
			return maximum (x.Left);
		
		Node y = x.Parent;
		while (y != Nil && x == y.Left) {
			x = y;
			y = y.Parent;
		}
		return y;
	}
	
	public Node search (Node x, int key) {
		while (x != Nil && key != x.Key)
			x = (key < x.Key) ? x.Left : x.Right;
		return x;
	}
	
	public synchronized Node successor (Node x) {
		if (x.Right != Nil)
			return this.minimum (x.Right);
		
		Node y = x.Parent;
		while (y != Nil && x == y.Right) {
			x = y;
			y = y.Parent;
		}
		return y;
	}
}
