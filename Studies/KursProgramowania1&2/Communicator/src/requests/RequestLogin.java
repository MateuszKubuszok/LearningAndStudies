package requests;

public final class RequestLogin extends Request {
	private static final long serialVersionUID = 6406506149411170503L;
	
	public String Password;
	
	public RequestLogin () {}
	
	public RequestLogin (String username, String password, String resource) {
		Username = username;
		Password = password;
		Resource = resource;
	}
	
	public String getPassword () {
		return Password;
	}
	
	public RequestType obtainRequestType () {
		return RequestType.Login;
	}
	
	public void setPassword (String password) {
		Password = password;
	}
}
