package server_main;

import server.Server;

public class ServerMain {
	public static Server Server;
	
	public static void main(String[] args) {
		int Port = 7777;
		
		server.Server.UsersDatafile = System.getProperty ("user.dir")+"\\server_users.dat";
		
		if (args.length >= 1) {
			try {
				Port = Integer.parseInt (args [0]);
			} catch (NumberFormatException e) {}
		}
		
		Server = new Server (Port);
		
		new Thread (new Runnable () {
			public void run () {
				while (true) {
					Server.writeUsers ();
					
					try {
						Thread.sleep (10000);
					} catch (InterruptedException e) {}
				}
			}
		}).start ();
	}
}
