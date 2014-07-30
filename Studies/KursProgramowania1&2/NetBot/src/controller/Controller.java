package controller;

import searcher.Wanderer;
import gui.BotWindow;
import gui.Printer;

public class Controller {
	private BotWindow Window;
	private Printer Printer;
	
	private Thread Current;
	
	public Controller () {
		Window = new BotWindow (this);
		
		Window.getJFrame ().setVisible (true);
		
		Printer = Window.getPrinter ();
	}
	
	public void search (String address, String pattern, String depth) {
		Window.locked (true);
		Printer.clean ();
		
		try {			
			final Wanderer Wanderer = new Wanderer (
				address.startsWith ("http://") || address.startsWith ("https://") ? address : "http://" + address,
				pattern,
				Integer.parseInt (depth)
			);
			Wanderer.setPrinter (Printer);
			
			if (Current != null && Current.isAlive ())
				Current.interrupt ();
			Current = new Thread (
				new Runnable () {
					public void run () {
						try {
							Wanderer.parse ();
						} catch (Exception e) {
							Printer.print ("Error occured: " + e.getMessage ());
							e.printStackTrace ();
						} finally {
							Window.locked (false);
						}
					}
				});
			Current.start ();
		} catch (Exception e) {
			Printer.print (depth + " is not a valid integer number.");
			Window.locked (false);
		}
	}
	
	public void stop () {
		if (Current != null && Current.isAlive ()) {
			Current.interrupt ();
			Window.locked (false);
		}
	}
	
	public static void main (String[] args) {
		new Controller ();
	}
}
