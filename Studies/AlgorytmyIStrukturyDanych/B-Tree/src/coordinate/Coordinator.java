package coordinate;

import java.util.Random;
import java.util.Vector;

import console.TreePreview;
import graph.GraphWindow;
import menu.MenuWindow;
import observer.*;
import b_tree.*;
import threads.*;

public class Coordinator implements Observable {
	public int Degree;
	public int Delay;
	public int GenerateMethod;
	public int Seed;
	public int Size;
	public int Step;
	
	public GraphWindow Graph;
	public MenuWindow Menu;
	
	public GraphTest	Execution;
	public Thread		Display;
	public BTree		Tree;
	
	private Vector<Observer> Observers;
	private Random Rand;
	
	public Coordinator () { 
		Graph = new GraphWindow (this);
		Menu = new MenuWindow (this);
	}
	
	public void addObserver (Observer object) {
		this.Observers.add (object);
	}
	
	public void informAll () {
		for (int i = 0; i < this.Observers.size (); i++)
			this.Observers.get (i).inform ();
	}
	
	public void startup () {
		Thread MenuThread = new Thread (Menu);
		MenuThread.setDaemon (true);
		MenuThread.start ();
	}
	
	public void startAnimation () {
		Tree = new BTree (Degree);
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
			Menu.AverageDeletionComparisions.setText	("" + ((double) (Tree.AllDeletionComparisions))/((double) (Tree.Deletions)));
			Menu.MaxDeletionComparisions.setText		("" + Tree.MaximumDeletionComparisions);
			Menu.AverageInsertionComparisions.setText	("" + ((double) (Tree.AllInsertionComparisions))/((double) (Tree.Insertions)));
			Menu.MaxInsertionComparisions.setText		("" + Tree.MaximumInsertionComparisions);
			Menu.AverageDeletionDiskAccess.setText		("" + ((double) (Tree.Disk.AllDeletionDiskAccess))/((double) (Tree.Deletions)));
			Menu.MaxDeletionDiskAccess.setText			("" + Tree.Disk.MaximalDeletionDiskAccess);
			Menu.AverageInsertionDiskAccess.setText		("" + ((double) (Tree.Disk.AllInsertionDiskAccess))/((double) (Tree.Insertions)));
			Menu.MaxInsertionDiskAccess.setText			("" + Tree.Disk.MaximalInsertionDiskAccess);
		} else {
			Menu.AverageDeletionComparisions.setText	("unavailable");
			Menu.MaxDeletionComparisions.setText		("unavailable");
			Menu.AverageInsertionComparisions.setText	("unavailable");
			Menu.MaxInsertionComparisions.setText		("unavailable");
			Menu.AverageDeletionDiskAccess.setText		("unavailable");
			Menu.MaxDeletionDiskAccess.setText			("unavailable");
			Menu.AverageInsertionDiskAccess.setText		("unavailable");
			Menu.MaxInsertionDiskAccess.setText			("unavailable");
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
				Action = (Thread) new InsertThread (Tree, Input [i]);
				Action.start ();
				try {
					Action.join ();
				} catch (Exception ex) {
					System.out.println (ex.getMessage ());
				}
			}
			
			do {
				Empty = true;
				for (i = 0; i < Size; i++)
					if (Input [i] >= 0) {
						Empty = false;
						break;
					}
				
				if (!Empty) {
					for (i = Rand.nextInt (Size); Input [i] < 0; i = (i+1)%Size) ;
					
					Action = (Thread) new DeleteThread (Tree, Input [i]);
					Action.start ();
					try {
						Action.join ();
					} catch (Exception ex) {
						System.out.println (ex.getMessage ());
					}
					for (i = 0; i < Size; i++)
						if (Input [i] >= 0 && Input [i] == ((DeleteThread) Action).Key) {							
							Input [i] = -1;
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
