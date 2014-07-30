package requests;

public final class RequestGetMessages extends Request {
	private static final long serialVersionUID = -7606031836361111740L;
	
	public RequestGetMessages () {}
	
	public RequestGetMessages (String username, String resource, int sessionID) {
		super (username, resource);
	}
	
	public RequestType obtainRequestType () {
		return RequestType.GetMessages;
	}
}
