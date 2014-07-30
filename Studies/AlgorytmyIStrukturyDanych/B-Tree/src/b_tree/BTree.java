package b_tree;

import java.util.Vector;

import observer.Observable;
import observer.Observer;
import disk.Disk;

public class BTree implements Observable {
	protected int ViewPriority;
	protected String Message;
	protected Vector<Observer> Observers;
	
	public int Deletions = 0;
	public int Insertions = 0;
	public int Comparisions;
	public int AllInsertionComparisions;
	public int MaximumInsertionComparisions;
	public int AllDeletionComparisions;
	public int MaximumDeletionComparisions;
	
	public Disk Disk;
	
	public int Degree;
	
	public BNode Root;
	
	public class BNode {
		public int N;
		public int[] Keys;
		
		public boolean Leaf;
		
		public BNode[] Children;
		
		public BNode (int degree) {
			N = 0;
			Leaf = true;
			Keys = new int[2*degree-1+1];
			Children = new BNode[2*degree+1];
		}
	}
	
	public BTree (int degree) {
		create (degree, new Disk ());
	}
	
	public BTree (int degree, Disk disk) {
		create (degree, disk);
	}
	
	public void addObserver (Observer object) {
		Observers.add (object);
	}
	
	private void create (int degree, Disk disk) {
		Observers = new Vector<Observer> ();
		Message = "";
		
		Degree = degree;
		Disk = disk;
		
		BNode x = new BNode (degree);
		x.Leaf = true;
		x.N = 0;
		Disk.write (x);
		
		Root = x;
	}
	
	public boolean compare (boolean comparision) {
		Comparisions++;
		
		return comparision;
	}
	
	public void delete (int k) {
		Disk.DiskAccess = 0;
		Comparisions = 0;
		
		delete (Root, k);
		
		if (!Root.Leaf && Root.N == 0)
			Root = Root.Children [1];
		
		Deletions++;
		Disk.AllDeletionDiskAccess += Disk.DiskAccess;
		Disk.MaximalDeletionDiskAccess = Disk.MaximalDeletionDiskAccess >= Disk.DiskAccess ? Disk.MaximalDeletionDiskAccess : Disk.DiskAccess;
		AllDeletionComparisions += Comparisions;
		MaximumDeletionComparisions = MaximumDeletionComparisions >= Comparisions ? MaximumDeletionComparisions : Comparisions;
		
		informAll (0, "Deleted key: "+k);
	}
	
	public void delete (BNode x, int k) {
		if (x.Leaf) 
			deleteFromLeaf (x, k);
		else {
			int i;
			for (i = 1; i <= x.N && compare (x.Keys [i] < k); i++) ; 
			
			if (i <= x.N && compare (x.Keys [i] == k))
				deleteFromInternalNode (x, i);
			else {
				BNode y = ensureFullEnough (x, i);
				delete (y, k);
			}
		}
	}
	
	private void deleteFromLeaf (BNode x, int k) {
		int i;
		
		for (i = 1; i <= x.N && compare (x.Keys [i] < k); i++) ;
		
		if (i <= x.N && compare (x.Keys [i] == k)) {
			for (int j = i+1; j <= x.N; j++)
				x.Keys [j-1] = x.Keys [j];
			x.N--;
			
			Disk.write (x);
			
			informAll (1, "Key deleted from leaf: "+k);
		}
	}
	
	private void deleteFromInternalNode (BNode x, int i) {
		int k = x.Keys [i];
		BNode y = x.Children [i];
		Disk.read (y);
		
		if (y.N >= Degree) {
			int K = maximum (y);
			Disk.read (y);
			delete (y, K);
			Disk.read (x);
			x.Keys [i] = K;
		} else {
			BNode z = x.Children [i+1];
			Disk.read (z);
			
			if (z.N >= Degree) {
				int K = minimum (z);
				Disk.read (z);
				delete (z, K);
				Disk.read (x);
				x.Keys [i] = K;
			} else {
				y.Keys [y.N+1] = k;
				for (int j = 1; j <= z.N; j++)
					y.Keys [y.N+j+1] = z.Keys [j];
				
				if (!y.Leaf)
					for (int j = 1; j <= z.N+1; j++)
						y.Children [y.N+j+1] = z.Children [j];
				
				y.N += z.N + 1;
				
				for (int j = i+1; j <= x.N; j++) {
					x.Keys [j-1] = x.Keys [j];
					x.Children [j] = x.Children [j+1];
				}
				x.N--;
				
				Disk.write (x);
				Disk.write (y);
				Disk.free (z);
				
				delete (y, k);
			}
		}
		
		informAll (1, "Key deleted from internal node: "+k);
	}
	
	public BNode ensureFullEnough (BNode x, int i) {
		BNode Child = x.Children [i];
		Disk.read (Child);
		
		if (Child.N < Degree) {
			BNode	LeftSibling = null,
					RightSibling = null;
			int		LeftN = 0,
					RightN = 0;
			
			if (i > 1) {
				LeftSibling = x.Children [i-1];
				LeftN = LeftSibling.N;
				Disk.read (LeftSibling);
			}
			if (i < x.N+1) {
				RightSibling = x.Children [i+1];
				RightN = RightSibling.N;
				Disk.read (RightSibling);
			}
			
			if (LeftN >= Degree) {				
				if (!Child.Leaf) {
					for (int j = Child.N+1; j >= 1; j--)
						Child.Children [j+1] = Child.Children [j];
					Child.Children [1] = LeftSibling.Children [LeftN+1];					
				}
				
				for (int j = Child.N; j >= 1; j--)
					Child.Keys [j+1] = Child.Keys [j];
				Child.Keys [1] = x.Keys [i-1];
				x.Keys [i-1] = LeftSibling.Keys [LeftN];
				
				LeftSibling.N--;
				Child.N++;
				
				Disk.write (Child);
				Disk.write (LeftSibling);
				Disk.write (x);
			} else if (RightN >= Degree) {
				if (!Child.Leaf) {
					Child.Children [Child.N+2] = RightSibling.Children [1];
					for (int j = 1; j < RightN+1; j++)
						RightSibling.Children [j] = RightSibling.Children [j+1];
				}
				
				Child.Keys [Child.N+1] = x.Keys [i];
				x.Keys [i] = RightSibling.Keys [1];
				for (int j = 1; j < RightN; j++)
					RightSibling.Keys [j] = RightSibling.Keys [j+1];
				
				RightSibling.N--;
				Child.N++;
				
				Disk.write (Child);
				Disk.write (RightSibling);
				Disk.write (x);
			} else if (LeftN == Degree-1) {
				if (!Child.Leaf) {
					for (int j = 1; j <= Child.N+1; j++)
						LeftSibling.Children [j+Degree] = Child.Children [j];
				}
				
				LeftSibling.Keys [LeftN+1] = x.Keys [i-1];
				for (int j = 1; j <= Child.N; j++)
					LeftSibling.Keys [j+Degree] = Child.Keys [j];
				
				for (int j = i-1; j < x.N; j++)
					x.Keys [j] = x.Keys [j+1];
				for (int j = i; j <= x.N; j++)
					x.Children [j] = x.Children [j+1];
				x.Children [x.N+1] = null;
				
				x.N--;
				LeftSibling.N += Degree;
				
				if (!Root.Leaf && Root.N == 0)
					Root = Root.Children [1];
				
				Disk.free (Child);
				Disk.write (LeftSibling);
				Disk.write (x);
				
				return LeftSibling;
			} else if (RightN == Degree-1) {
				if (!Child.Leaf) {
					for (int j = 1; j <= RightN+1; j++)
						Child.Children [j+Degree] = RightSibling.Children [j];
				}
				
				Child.Keys [Child.N+1] = x.Keys [i];
				for (int j = 1; j <= RightN; j++)
					Child.Keys [j+Degree] = RightSibling.Keys [j];
				
				for (int j = i; j < x.N; j++)
					x.Keys [j] = x.Keys [j+1];
				for (int j = i+1; j <= x.N; j++)
					x.Children [j] = x.Children [j+1];
				x.Children [x.N+1] = null;
				
				x.N--;
				Child.N += Degree;
				
				if (!Root.Leaf && Root.N == 0)
					Root = Root.Children [1];
				
				Disk.write (Child);
				Disk.write (RightSibling);
				Disk.write (x);
			}

			informAll (1, "Ensured a node is prepared to deletion");	
		}
		
		return Child;
	}
	
	public int getHeight () {
		return getHeight (Root);
	}
	
	public int getHeight (BNode x) {
		if (x == null)
			return 0;
		else if (x.Leaf)
			return 1;
		else
			return 1 + getHeight (x.Children [1]);
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
	
	public int[] inorder () {
		return inorder (Root);
	}
	
	public int[] inorder (BNode x) {
		int ToReturn[];
		
		if (x.Leaf) {
			ToReturn = new int[x.N];
			for (int j = 1; j <= x.N; j++)
				ToReturn [j-1] = x.Keys [j];
		} else {
			int Results[][] = new int[x.N+1][];
			int ResultSize = x.N;
			
			for (int j = 1; j <= x.N+1; j++) {
				Results [j-1] = inorder (x.Children [j]);
				ResultSize += Results [j-1].length;
			}
			
			ToReturn = new int[ResultSize];
			
			int j = 0;
			for (int i = 0; i < x.N; i++) {
				for (int k = 0; k < Results [i].length; k++) {
					ToReturn [j] = Results [i][k];
					j++;
				}
				ToReturn [j] = x.Keys [i+1];
				j++;
			}
			for (int k = 0; k < Results [x.N].length; k++) {
				ToReturn [j] = Results [x.N][k];
				j++;
			}
		}
		
		return ToReturn;
	}
	
	public boolean[] inorderLeafsKeys () {
		return inorderLeafsKeys (Root);
	}
	
	public boolean[] inorderLeafsKeys (BNode x) {
		boolean ToReturn[];
		
		if (x.Leaf) {
			ToReturn = new boolean[x.N];
			for (int j = 1; j <= x.N; j++)
				ToReturn [j-1] = true;
		} else {
			boolean Results[][] = new boolean[x.N+1][];
			int ResultSize = x.N;
			
			for (int j = 1; j <= x.N+1; j++) {
				Results [j-1] = inorderLeafsKeys (x.Children [j]);
				ResultSize += Results [j-1].length;
			}
			
			ToReturn = new boolean[ResultSize];
			
			int j = 0;
			for (int i = 0; i < x.N; i++) {
				for (int k = 0; k < Results [i].length; k++) {
					ToReturn [j] = Results [i][k];
					j++;
				}
				ToReturn [j] = false;
				j++;
			}
			for (int k = 0; k < Results [x.N].length; k++) {
				ToReturn [j] = Results [x.N][k];
				j++;
			}
		}
		
		return ToReturn;
	}
	
	public void insert (int k) {
		Disk.DiskAccess = 0;
		Comparisions = 0;
		
		BNode r = Root;
		
		if (r.N == 2*Degree-1) {
			BNode s = new BNode (Degree);
			Root = s;
			s.Leaf = false;
			s.N = 0;
			s.Children [1] = r;
			splitChild (s, 1);
			insertNonFull (s, k);
		} else
			insertNonFull (r, k);
		
		Insertions++;
		Disk.AllInsertionDiskAccess += Disk.DiskAccess;
		Disk.MaximalInsertionDiskAccess = Disk.MaximalInsertionDiskAccess >= Disk.DiskAccess ? Disk.MaximalInsertionDiskAccess : Disk.DiskAccess;
		AllInsertionComparisions += Comparisions;
		MaximumInsertionComparisions = MaximumInsertionComparisions >= Comparisions ? MaximumInsertionComparisions : Comparisions;
		
		informAll (0, "Inserted: "+k);
	}
	
	public void insertNonFull (BNode x, int k) {
		int i = x.N;
		
		if (x.Leaf) {
			for (; i >= 1 && compare (k < x.Keys [i]); i--)
				x.Keys [i+1] = x.Keys [i];
			
			x.Keys [i+1] = k;
			x.N++;
			
			Disk.write (x);
		} else {
			for (; i >= 1 && compare (k < x.Keys [i]); i--) ;
			i++;
			
			Disk.read (x.Children [i]);
			
			if (x.Children [i].N == 2*Degree-1) {
				splitChild (x, i);
				if (compare (k > x.Keys [i]))
					i++;
			}
			 
			insertNonFull (x.Children [i], k);
		}
		
		informAll (1, "insertNonNull");
	}
	
	public int maximum () {
		return maximum (Root);
	}
	
	private int maximum (BNode x) {
		if (x.Leaf)
			return x.Keys [x.N];
		else {
			Disk.read (x.Children [x.N+1]);
			return maximum (x.Children [x.N+1]);
		}
	}
	
	public int minimum () {
		return minimum (Root);
	}
	
	private int minimum (BNode x) {
		if (x.Leaf)
			return x.Keys [1];
		else {
			Disk.read (x.Children [1]);
			return minimum (x.Children [1]);
		}
	}
	
	public BTreePair search (BNode x, int k) {
		int i;
		for (i = 1; i <= x.N && k > x.Keys [i]; i++) ;
		
		if (i <= x.N && k == x.Keys [i])
			return new BTreePair (x, i);
		else if (x.Leaf)
			return null;
		else {
			Disk.read (x.Children [i]);
			return search (x.Children [i], k);
		}
	}
	
	private void splitChild (BNode x, int i) {
		BNode	z = new BNode (Degree),
				y = x.Children [i];
		int 	j;
		
		z.Leaf = y.Leaf;
		z.N = Degree-1;
		
		for (j = 1; j <= Degree-1; j++)
			z.Keys [j] = y.Keys [j+Degree];
		
		if (!y.Leaf)
			for (j = 1; j <= Degree; j++)
				z.Children [j] = y.Children [j+Degree];
		
		y.N = Degree-1;
		
		for (j = x.N+1; j >= i+1; j--)
			x.Children [j+1] = x.Children [j];
		x.Children [i+1] = z;
		
		for (j = x.N; j >= i; j--)
			x.Keys [j+1] = x.Keys [j];
		x.Keys [i] = y.Keys [Degree];
		
		x.N++;
		
		Disk.write (y);
		Disk.write (z);
		Disk.write (x);
		
		informAll (1, "Split node");
	}
	
	public String walk () {
		return walk (Root);
	}
	
	public String walk (BNode x) {
		if (x == null)
			return "_";
		
		String ToReturn = "";
		
		if (x.Leaf) {
			ToReturn = " ";
			for (int i = 1; i <= x.N; i++)
				ToReturn += x.Keys [i] + " ";
		} else {
			for (int i = 1; i <= x.N; i++)
				ToReturn += "["+walk (x.Children [i])+"] ("+x.Keys [i]+") ";
			ToReturn += "["+walk (x.Children [x.N+1])+"]";
		}
		
		return ToReturn;
	}
	
	public class BTreePair {
		public BNode Node;
		public int Index;
		
		public BTreePair (BNode x, int i) {
			Node = x;
			Index = i;
		}
	}
}