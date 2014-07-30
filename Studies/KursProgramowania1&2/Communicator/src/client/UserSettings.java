package client;

import java.io.Serializable;

public class UserSettings implements Serializable {
	private static final long serialVersionUID = -2413221174734264851L;
	
	public String Username;
	public String Password;
	public String Resource;
	
	public UserSettings () {}
	
	public UserSettings (String username, String password, String resource) {
		Username = username;
		Password = password;
		Resource = resource;
	}
	
	public String getPassword () {
		return Password;
	}
	
	public String getResource () {
		return Resource;
	}
	
	public String getUsername () {
		return Username;
	}
	
	public void setPassword (String password) {
		Password = password;
	}
	
	public void setResource (String resource) {
		Resource = resource;
	}
	
	public void setUsername (String username) {
		Username = username;
	}
}
