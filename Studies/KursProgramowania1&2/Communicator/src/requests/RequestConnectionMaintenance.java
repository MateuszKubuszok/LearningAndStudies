package requests;

public final class RequestConnectionMaintenance extends Request {
	private static final long serialVersionUID = -7480617763082462408L;

	public RequestConnectionMaintenance () {}
	
	public RequestConnectionMaintenance (String username, String resource) {
		super (username, resource);
	}
	
	public RequestType obtainRequestType () {
		return RequestType.ConnectionMaintenance;
	}
}
