package requests;

import java.io.Serializable;
import java.util.Vector;

import client.Client;

public final class RequestPackage implements Serializable {
	private static final long serialVersionUID = 2697298247981336466L;
	
	private transient Client Client;
	public Vector<Request>	Requests;
	
	public RequestPackage (Client client) {
		Client = client;
		Requests = new Vector<Request> ();
	}
	
	public RequestPackage (Vector<Request> requests) {
		Requests = requests;
	}
	
	public void add (Request request) {
		request.Resource = Client.Resource;
		request.SessionID = Client.SessionID;
		Requests.add (request);
	}
	
	public void addAll (RequestPackage requests) {
		for (Request Request : requests.Requests) {
			Request.Username = Client.Username;
			Request.Resource = Client.Resource;
			Request.SessionID = Client.SessionID;
		}
		Requests.addAll (requests.Requests);
	}
	
	public Vector<Request> getRequests () {
		return Requests;
	}
	
	public void setRequests (Vector<Request> requests) {
		Requests = requests;
	}
}
