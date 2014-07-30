package console;

import coordinate.Coordinator;
import observer.Observer;

public class TreePreview implements Observer {
	public Coordinator Controller;
	
	public TreePreview (Coordinator controller) {
		Controller = controller;
	}
	
	public void inform () {
		System.out.println (Controller.Tree.getMessage ());
		System.out.println (Controller.Tree.inorder ());
		System.out.println ();
	}
}
