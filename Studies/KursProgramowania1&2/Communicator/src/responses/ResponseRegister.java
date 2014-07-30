package responses;


public final class ResponseRegister extends Response {
	private static final long serialVersionUID = 1882511576113720408L;

	public String Username;
	
	public ResponseRegister () {}
	
	public ResponseRegister (String username) {
		Username = username;
	}
	
	public String getUsername () {
		return Username;
	}
	
	public ResponseType obtainResponseType() {
		return ResponseType.Register;
	}
	
	public void setUsername (String username) {
		Username = username;
	}
}
