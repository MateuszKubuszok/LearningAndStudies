package data;

import java.io.Serializable;
import java.util.ArrayList;

public final class ServerUser implements Serializable {
	private static final long serialVersionUID = 1366711631783436402L;
	
	public String				Username;
	public String				Password;
	public ArrayList<Message>	Messages;
	
	public ServerUser () {
		Messages = new ArrayList<Message> ();
	}
	
	public ServerUser (String username, String password) {
		Username = username;
		Password = password;
		Messages = new ArrayList<Message> ();
	}
	
	public ArrayList<Message> getMessages () {
		return Messages;
	}
	
	public String getPassword () {
		return Password;
	}
	
	public String getUsername () {
		return Username;
	}
	
	public void setMessages (ArrayList<Message> messages) {
		Messages = messages;
	}
	
	public void setPassword (String password) {
		Password = password;
	}
	
	public void setUsername (String username) {
		Username = username;
	}
}
