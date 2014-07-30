package threads;

import b_tree.*;

public class InsertThread extends Thread {
	private int Key;
	private BTree Tree;
	
	public InsertThread (BTree tree, int k) {
		Key = k;
		Tree = tree;
	}
	
	public void run () {
		Tree.insert (Key);
	}
}
