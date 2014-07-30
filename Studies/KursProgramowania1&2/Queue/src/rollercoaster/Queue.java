package rollercoaster;

import java.util.Iterator;
import java.util.LinkedList;

public class Queue {
	public LinkedList<Element> List;
	
	private int MaxSize;
	
	public class Element {
		int Priority;
		int Value;
		
		public Element (int value, int priority) {
			Value = value;
			Priority = priority;
		}
	}
	
	public Queue () {
		MaxSize = 0;
		List = new LinkedList<Element> ();
	}
	
	public Queue (int size) {
		MaxSize = size;
		List = new LinkedList<Element> ();
	}
	
	public int dequeue () {
		int ToReturn = List.getFirst ().Value;
		List.removeFirst ();
		return ToReturn;
	}
	
	public void enqueue (int value, int priority)
	throws Exception {
		if (isFull ())
			throw new Exception ("List is full!");
		
		for (Iterator<Element> I = List.iterator (); I.hasNext ();) {
			Element Checked = I.next ();
			
			if (Checked.Priority < priority) {
				List.add(List.indexOf (Checked), new Element (value, priority));
				return;
			}
		}
		
		List.add (new Element (value, priority));
	}
	
	public int first () {
		return List.getFirst ().Value;
	}
	
	public boolean isEmpty () {
		return !(List.size () > 0);
	}
	
	public boolean isFull () {
		return (MaxSize != 0 && List.size () >= MaxSize);
	}

	public int size () {
		return List.size ();
	}
	
	public String toString () {
		String ToReturn = "";
		
		for (Iterator<Element> I = List.iterator (); I.hasNext ();) {
			Element Checked = I.next ();
			
			ToReturn += "[v:" + Checked.Value + "; p:" + Checked.Priority + "]";
		}
		
		return ToReturn;
	}
	
	public static void main (String[] args)
	throws Exception {
		Queue Test = new Queue ();
		
		Test.enqueue (100,	1);
		Test.enqueue (80,	2);
		Test.enqueue (60,	3);
		Test.enqueue (40,	7);
		Test.enqueue (45,	7);
		Test.enqueue (20,	5);
		Test.enqueue (0,	6);
		
		System.out.println ("Kolejka: " + Test);
		
		while (Test.size () > 0)
			System.out.println ("Najwyzej stal: " + Test.dequeue ());
	}
}
