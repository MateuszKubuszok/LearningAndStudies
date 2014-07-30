package controller;

import java.util.Vector;

import server.Client;

import commands.Command;

public class Synchronizer implements Runnable {
	private Controller Controller;
	private Vector<Command> ToSend;
	
	public Synchronizer (Controller controller) {
		Controller	= controller;
		ToSend		= new Vector<Command> ();		
	}
	
	public void addCommand (Command command) {
		synchronized (ToSend) {
			ToSend.add (command);
		}
	}
	
	public void run () {
		Client Client;
		
		while (true) {
			Client = Controller.getClient ();
			
			if (Client != null) {
				Controller.update (Client.getCommands ());
				
				synchronized (ToSend) {
					for (Command Command : ToSend)
						Client.addCommandToSend (Command);					
					ToSend.clear ();
				}
			}
			
			try {
				Thread.yield ();
				Thread.sleep (250);
			} catch (InterruptedException e) {
				return;
			}
		}
	}
}
