package data;

import java.io.Serializable;

public final class ClientUser implements Comparable<ClientUser>, Serializable {
	private static final long serialVersionUID = -5774301831249159871L;

	public String		Username;
	public UserStatus	State = UserStatus.Offline;
	
	public ClientUser () {
		State = UserStatus.Offline;
	}
	
	public ClientUser (String username) {
		Username = username;
		State = UserStatus.Offline;
	}
	
	public ClientUser (String username, UserStatus state) {
		Username = username;
		State = state;
	}
	
	public int compareTo (ClientUser user) {
		int v1 = statusValue (),
			v2 = user.statusValue ();
		if (v1 != v2)
			return v2 - v1;
		else
			return Username.compareTo (user.Username);
	}
	
	public String getUsername () {
		return Username;
	} 
	
	public UserStatus getState () {
		return State;
	}
	
	public void setUsername (String username) {
		Username = username;
	}
	
	public void setState (UserStatus state) {
		State = state;
	}
	
	public int statusValue () {
		if (State == UserStatus.Online)
			return 2;
		else if (State == UserStatus.Away)
			return 1;
		else
			return 0;
	}
}
