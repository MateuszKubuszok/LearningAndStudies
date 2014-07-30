package server;

import java.beans.PersistenceDelegate;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.beans.Encoder;
import java.beans.Expression;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import commands.*;

import commands.Command;

public class Client implements Runnable {
	protected Socket		Client;
	protected XMLDecoder	Decoder;
	protected XMLEncoder	Encoder;
	
	protected Thread		Thread;
	
	private	int		Port;
	private	String	Server;
	
	protected Vector<Command>	CommandsRecieved;
	protected Vector<Command>	CommandsToSend;
	
	public Client () {
		CommandsRecieved = new Vector<Command> ();
		CommandsToSend = new Vector<Command> ();
	}
	
	public Client (String server, int port)
	throws ServerError {
		CommandsRecieved = new Vector<Command> ();
		CommandsToSend = new Vector<Command> ();
		
		Port = port;
		Server = server;
		
		try {
			Client = new Socket (Server, Port);
		} catch (UnknownHostException Exception) {
			throw new ServerError (ServerError.ErrorType.UknownHost, "Couldn't connect to server "+Server+":"+Port+" ("+Exception.getMessage ()+").");
		} catch (IOException Exception) {
			throw new ServerError (ServerError.ErrorType.CannotConnetClient, "Couldn't open server port at "+Port+" ("+Exception.getMessage ()+").");
		}
		
		Thread = new Thread (this);
		Thread.setDaemon (true);
		Thread.start ();
		
		System.out.println ("Client connected");
	}
	
	public void addCommandToSend (Command command) {
		synchronized (CommandsToSend) {
			CommandsToSend.add (command);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Vector<Command> getCommands () {
		Vector<Command> Commands;
		
		synchronized (CommandsRecieved) {
			Commands = (Vector<Command>) CommandsRecieved.clone ();
		}
		
		return Commands;
	}
	
	@SuppressWarnings("unchecked")
	public void readCommands ()
	throws ServerError {
		Vector<Command> Commands;
		
		try {
			Decoder = new XMLDecoder (Client.getInputStream ());
			Commands = (Vector<Command>) Decoder.readObject ();
			synchronized (CommandsRecieved) {
				CommandsRecieved.addAll (Commands);
			}
		} catch (Exception Exception) {
			throw new ServerError (ServerError.ErrorType.CannotHandleConnection, "Error during connection ("+Exception.getMessage ()+").");
		}
	}
	
	public void sendCommands ()
	throws ServerError {
		synchronized (CommandsToSend) {
			try {
				Encoder = new XMLEncoder (Client.getOutputStream ());
				Encoder.setPersistenceDelegate (CommandType.class, new EnumPersistenceDelegate ());
				Encoder.writeObject (CommandsToSend);
				Encoder.close ();
				CommandsToSend.clear ();
			} catch (IOException Exception) {
				throw new ServerError (ServerError.ErrorType.CannotHandleConnection, "Error during connection ("+Exception.getMessage ()+").");
			}
		}
	}
	
	public void run () {
		try {
			while (true) {
				if (Client != null) {
					System.out.println ("Client : Send [start]");
					sendCommands ();
					System.out.println ("Client : Send [end]");
				}
				
				try {
					Client = new Socket (Server, Port);
				} catch (UnknownHostException Exception) {
					throw new ServerError (ServerError.ErrorType.UknownHost, "Couldn't connect to server "+Server+":"+Port+" ("+Exception.getMessage ()+").");
				} catch (IOException Exception) {
					throw new ServerError (ServerError.ErrorType.CannotConnetClient, "Couldn't open server port at "+Port+" ("+Exception.getMessage ()+").");
				}
				
				if (Client != null) {
					System.out.println ("Client : Read [start]");
					readCommands ();
					System.out.println ("Client : Read [end]");
				}
					
				try {
					Client = new Socket (Server, Port);
				} catch (UnknownHostException Exception) {
					throw new ServerError (ServerError.ErrorType.UknownHost, "Couldn't connect to server "+Server+":"+Port+" ("+Exception.getMessage ()+").");
				} catch (IOException Exception) {
					throw new ServerError (ServerError.ErrorType.CannotConnetClient, "Couldn't open server port at "+Port+" ("+Exception.getMessage ()+").");
				}
				
				try {
					java.lang.Thread.sleep (250);
				} catch (InterruptedException Exception) {
					System.out.println (Exception.getMessage ());
					return;
				}
			}
		} catch (ServerError Exception) {
			// TODO: informuj o zamknieciu polaczenia
			return;
		}
	}
	
	class EnumPersistenceDelegate extends PersistenceDelegate {
		@SuppressWarnings("rawtypes")
		protected Expression instantiate (Object oldInstance, Encoder out) {
			Enum e = (Enum) oldInstance;
			return new Expression (e, e.getClass(), "valueOf", new Object[] {e.name()});
		}
		
	    protected boolean mutatesTo (Object oldInstance, Object newInstance) {
	        return oldInstance == newInstance;
	    }
	}
}
