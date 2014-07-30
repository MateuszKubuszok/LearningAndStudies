package requests;

public final class RequestLogout extends Request {
	private static final long serialVersionUID = -5151195442826101715L;

	public RequestType obtainRequestType() {
		return RequestType.Logout;
	}
}
