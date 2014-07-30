package coordinate;

import java.util.Random;
import java.util.Vector;

import console.TreePreview;
import graph.GraphWindow;
import menu.MenuWindow;
import observer.*;
import rb_tree.*;
import threads.*;
import tree.*;

public class Coordinator implements Observable {
	public int Delay;
	public int GenerateMethod;
	public int Seed;
	public int Size;
	public int Step;
	
	public GraphWindow Graph;
	public MenuWindow Menu;
	
	public GraphTest	Execution;
	public Thread		Display;
	public RBTree		Tree;
	
	private Vector<Observer> Observers;
	private Random Rand;
	
	public Coordinator () { 
		Graph = new GraphWindow (this);
		Menu = new MenuWindow (this);
	}
	
	public void addObserver (Observer object) {
		Observers.add (object);
	}
	
	public void informAll () {
		for (int i = 0; i < Observers.size (); i++)
			Observers.get (i).inform ();
	}
	
	public void startup () {
		Thread MenuThread = new Thread (Menu);
		MenuThread.setDaemon (true);
		MenuThread.start ();
	}
	
	public void startAnimation () {
		Tree = new RBTree ();
		Tree.addObserver (Graph);
		Tree.addObserver (new TreePreview (this));
		
		Graph.setVisible (true);
		
		Execution = new GraphTest ();
		Execution.setPriority (Thread.MIN_PRIORITY);
		Execution.start ();
	}
	
	public void finishAnimation () {
		if (Execution != null && Execution.isAlive ())
			Execution.interrupt ();
		
		if (Size > 0 && Menu != null && Tree != null && Tree.Insertions > 0 && Tree.Deletions > 0) {
			Menu.AverageDeletionComparisions.setText	("" + ((double) (Tree.AllComparisionsDeletion))/((double) (Tree.Deletions)));
			Menu.MaxDeletionComparisions.setText		("" + Tree.MaxComparisionsDeletion);
			Menu.AverageInsertionComparisions.setText	("" + ((double) (Tree.AllComparisionsInsertion))/((double) (Tree.Insertions)));
			Menu.MaxInsertionComparisions.setText		("" + Tree.MaxComparisionsDeletion);
			Menu.AverageDeletionRotations.setText		("" + ((double) (Tree.AllRotationsDeletion))/((double) (Tree.Deletions)));
			Menu.MaxDeletionRotations.setText			("" + Tree.MaxRotationsDeletion);
			Menu.AverageInsertionRotations.setText		("" + ((double) (Tree.AllRotationsInsertion))/((double) (Tree.Insertions)));
			Menu.MaxInsertionRotations.setText			("" + Tree.MaxRotationsDeletion);
		} else {
			Menu.AverageDeletionComparisions.setText	("unavailable");
			Menu.MaxDeletionComparisions.setText		("unavailable");
			Menu.AverageInsertionComparisions.setText	("unavailable");
			Menu.MaxInsertionComparisions.setText		("unavailable");
			Menu.AverageDeletionRotations.setText		("unavailable");
			Menu.MaxDeletionRotations.setText			("unavailable");
			Menu.AverageInsertionRotations.setText		("unavailable");
			Menu.MaxInsertionRotations.setText			("unavailable");
		}
		
		Menu.lockFields (false);		
	}
	
	public void freeze () {
		try {
			Thread.sleep (Delay);
		} catch (InterruptedException ex) {}
	}
	
	public class GraphTest extends Thread {
		public Thread Action;
		
		public void run () {
			boolean		Empty;
			int[]		Input;
			int			i = 0;
			Node		x;
			Node[]		Leafs = new RBNode[Size];
			
			Rand = Seed != 0 ? new Random (Seed) : new Random ();
			
			switch (GenerateMethod) {
				case 2:
					Input = this.generateDescending (Size);
				break;
				case 1:
					Input = this.generateAscending (Size);
				break;
				default:
					Input = this.generateRandom (Size);
				break;
			}
			
			for (i = 0; i < Size; i++) {
				x = new RBNode (Tree);
				x.Key = Input [i];
				
				Action = (Thread) new InsertThread (Tree, (RBNode) x);
				Action.start ();
				try {
					Action.join ();
				} catch (Exception ex) {
					System.out.println (ex.getMessage ());
				}
				Leafs [i] = x;
			}
			
			do {
				Empty = true;
				for (i = 0; i < Size; i++)
					if (Leafs [i] != null) {
						Empty = false;
						break;
					}
				
				if (!Empty) {
					for (i = Rand.nextInt (Size); Leafs [i] == null; i = (i+1)%Size) ;
					
					Action = (Thread) new DeleteThread (Tree, (RBNode) Leafs [i]);
					Action.start ();
					try {
						Action.join ();
					} catch (Exception ex) {
						System.out.println (ex.getMessage ());
					}
					for (i = 0; i < Size; i++)
						if (Leafs [i] == ((DeleteThread) Action).Deleted) {							
							Leafs [i] = null;
							break;
						}
				}		
			} while (!Empty);
		}
		
		private int[] generateAscending (int n) {
			int[] Input = new int[n];
			
			for (int i = 0; i < Size; i++)
				Input [i] = i;
			
			return Input;
		}
		
		private int[] generateDescending (int n) {
			int[] Input = new int[n];
			
			for (int i = 0; i < Size; i++)
				Input [i] = n-1-i;
			
			return Input;
		}
		
		private int [] generateRandom (int n) {
			int[] Input = new int[n];
			boolean Repeat;
			
			for (int i = 0; i < n; i++)
				do {
					Input [i] = Rand.nextInt (n);
					Repeat = false;
					for (int j = 0; j < i; j++)
						if (Input [i] == Input [j]) {
							Repeat = true;
							break;
						}
				} while (Repeat);
			
			return Input;
		}
	}
}
