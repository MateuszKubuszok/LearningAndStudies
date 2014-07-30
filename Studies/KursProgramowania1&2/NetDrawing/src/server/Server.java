package server;

import java.io.IOException;
import java.net.ServerSocket;

public class Server extends Client implements Runnable {
	private ServerSocket	Server;
	private Thread			Thread;
	
	public Server () {}
	
	public Server (int port)
	throws ServerError {
		openServerAtPort (port);
	}
	
	public void closePort () {
		if (Server != null)
			try {
				Server.close ();
			} catch (IOException e) {}
	}
	
	public void finalize () {
		closePort ();
	}
	
	public void openServerAtPort (int port)
	throws ServerError {
		try {
			Server = new ServerSocket (port);
		} catch (IllegalArgumentException Exception) {
			throw new ServerError (ServerError.ErrorType.CannotOpenServer, "Incorrect port value ("+Exception.getMessage ()+").");
		} catch (IOException Exception) {
			throw new ServerError (ServerError.ErrorType.CannotOpenServer, "Couldn't open server port at "+port+" ("+Exception.getMessage ()+").");
		}
		
		Thread = new Thread (this);
		Thread.setDaemon (true);
		Thread.start ();
		
		System.out.println ("Server opened");
	}
	
	public void run () {
		try {			
			while (true) {
				Client = Server.accept ();
				System.out.println ("Server connected to client");
			
				System.out.println ("Server : Read [start]");
				readCommands ();
				System.out.println ("Server : Read [end]");
				
				Client = Server.accept ();
				System.out.println ("Server connected to client");
				
				System.out.println ("Server : Send [start]");
				sendCommands ();
				System.out.println ("Server : Send [end]");
				
				try {
					java.lang.Thread.sleep (250);
				} catch (InterruptedException Exception) {
					System.out.println (Exception.getMessage ());
					return;
				}
			}
		} catch (ServerError Exception) {
			System.out.println (Exception.getMessage ());
			// TODO: informuj o zamknieciu polaczenia
			return;
		} catch (IOException Exception) {
			System.out.println (Exception.getMessage ());
			// TODO: informuj o zamknieciu polaczenia
			return;
		}				
	}
}
