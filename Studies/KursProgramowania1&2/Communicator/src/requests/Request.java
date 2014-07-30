package requests;

import java.io.Serializable;

public abstract class Request implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public String	Username;
	public String	Resource;
	public int		SessionID = 0;
	
	public Request () {}
	
	public Request (String username, String resource) {
		Username = username;
		Resource = resource;
	}
	
	public String getResource() {
		return Resource;
	}
	
	public String getUsername () {
		return Username;
	}
	
	public int getSessionID () {
		return SessionID;
	}
	
	public abstract RequestType obtainRequestType ();
	
	public void setUsername (String username) {
		Username = username;
	}

	public void setResource (String resource) {
		Resource = resource;
	}
	
	public void setSessionID (int sessionID) {
		SessionID = sessionID;
	}
}
