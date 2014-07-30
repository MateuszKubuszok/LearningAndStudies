package requests;

public final class RequestChangePassword extends Request {
	private static final long serialVersionUID = -395037667724280199L;

	public String NewPassword;
	public String Password;
	
	public RequestChangePassword () {}
	
	public RequestChangePassword (String username, String password, String newPassword) {
		Username = username;
		Password = password;
		NewPassword = newPassword;
	}
	
	public String getNewPassword () {
		return NewPassword;
	}
	
	public String getPassword () {
		return Password;
	}
	
	public RequestType obtainRequestType () {
		return RequestType.ChangePassword;
	}

	public void setNewPassword (String newPassword) {
		NewPassword = newPassword;
	}
	
	public void setPassword (String password) {
		Password = password;
	}
}
