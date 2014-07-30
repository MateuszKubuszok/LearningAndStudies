package requests;

public final class RequestRegister extends Request {
	private static final long serialVersionUID = 6406506149411170503L;
	
	public String Password;
	
	public RequestRegister () {}
	
	public RequestRegister (String username, String password) {
		Username = username;
		Password = password;
	}
	
	public String getPassword () {
		return Password;
	}
	
	public RequestType obtainRequestType () {
		return RequestType.Register;
	}
	
	public void setPassword (String password) {
		Password = password;
	}
}
