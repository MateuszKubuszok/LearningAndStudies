package responses;


public final class ResponseLogin extends Response {
	private static final long serialVersionUID = -3237141025198437605L;
	
	public int SessionID;
	
	public int getSessionID () {
		return SessionID;
	}
	
	public ResponseType obtainResponseType () {
		return ResponseType.Login;
	}
	
	public void setSessionID (int sessionID) {
		SessionID = sessionID;
	}
}
