package threads;

import b_tree.*;

public class DeleteThread extends Thread {
	public int Key;
	private BTree Tree;
	
	public DeleteThread (BTree tree, int key) {
		Tree = tree;
		Key = key;
	}
	
	public void run () {
		Tree.delete (Key);
	}
}
