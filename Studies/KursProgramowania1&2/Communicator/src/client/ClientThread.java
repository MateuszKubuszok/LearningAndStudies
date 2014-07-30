package client;

public class ClientThread implements Runnable {
	private Client Client;
	
	public ClientThread (Client client) {
		Client = client;
	}
	
	public void run () {
		int Cycles = 0;
		
		while (true) {
			if (Client.ServerStatus == ServerStatus.Alive || Client.areUnloggedRequestsQueued ()) {
				if (Client.ServerStatus == ServerStatus.Alive) {
					Client.makeGetMessagesRequest ();
					
					if (Client.AllContacts.size () > 0 && Cycles % 2 == 0)
						Client.makeGetStatusesRequest ();
					
					if ((Client.AllContacts.size () == 0 || Client.CheckStatus) && Cycles % 10 == 0)
						Client.maintainConnection ();
					
					if (Cycles >= 10)
						Cycles = 0;
				}
				
				Client.executeRequests ();
			}
			
			Cycles++;
			
			try {
				Thread.sleep (1000);
			} catch (InterruptedException e) {}
		}
	}
}
