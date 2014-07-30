package server;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class SessionCleaner implements Runnable {
	Server Server;
	
	public SessionCleaner (Server server) {
		Server = server;
		
		new Thread (this).start ();
	}
	
	@SuppressWarnings("rawtypes")
	public void run () {
		while (true) {
			Map<Integer,Session> Sessions = Server.getSessions ();		
			ArrayList<Integer> ToRemove = new ArrayList<Integer> ();
			Date Date = new Date ();
			
			synchronized (Sessions) {
				Iterator Iterator = Sessions.entrySet ().iterator ();
				
				while (Iterator.hasNext ()) {
			        Map.Entry Pairs = (Map.Entry) Iterator.next ();
			        Session Session = (Session) Pairs.getValue ();
			        if ((Date.getTime () - Session.Date.getTime ()) > 60000)
						ToRemove.add (Integer.valueOf ((Integer) Pairs.getKey ()));
			    }
				
				for (int SessionKey : ToRemove) {
					Session Session = Sessions.get (SessionKey);
					Server.logoutSession (Session);
					System.out.println ("Session outdated: " + SessionKey);
				}
			}
			
			try {
				Thread.sleep (60000);
			} catch (InterruptedException e) {}
		}
	}

}
