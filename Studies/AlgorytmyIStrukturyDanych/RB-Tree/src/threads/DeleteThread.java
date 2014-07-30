package threads;

import rb_tree.*;
import tree.Node;

public class DeleteThread extends Thread {
	private RBNode Leaf;
	private RBTree Tree;
	
	public Node Deleted;
	
	public DeleteThread (RBTree tree, RBNode x) {
		Leaf = x;
		Tree = tree;
	}
	
	public void run () {
		Deleted = (Node) Tree.delete (Leaf);
	}
}
