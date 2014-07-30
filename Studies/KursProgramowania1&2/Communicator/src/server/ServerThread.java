package server;

import java.io.IOException;

public class ServerThread implements Runnable {
	private Server Server;
	
	public ServerThread (Server server) {
		Server = server;
		
		new Thread (this).start ();
	}
	
	public void run() {
		while (true)
			try {
				new ConnectionThread (Server, Server.getServerSocket ().accept ());
			} catch (IOException e) {}
	}
}
