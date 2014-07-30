package disk;

import b_tree.*;

public class Disk {
	public int DiskAccess;
	public int AllInsertionDiskAccess;
	public int MaximalInsertionDiskAccess;
	public int AllDeletionDiskAccess;
	public int MaximalDeletionDiskAccess;
	
	public void free (BTree.BNode x) {}
	
	public void read (BTree.BNode x) {
		DiskAccess++;
	}
	
	public void write (BTree.BNode x) {		
		DiskAccess++;
	}
}
