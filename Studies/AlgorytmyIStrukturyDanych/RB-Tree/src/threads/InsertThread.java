package threads;

import rb_tree.*;

public class InsertThread extends Thread {
	private RBNode Leaf;
	private RBTree Tree;
	
	public InsertThread (RBTree tree, RBNode x) {
		Leaf = x;
		Tree = tree;
	}
	
	public void run () {
		Tree.insert (Leaf);
	}
}
