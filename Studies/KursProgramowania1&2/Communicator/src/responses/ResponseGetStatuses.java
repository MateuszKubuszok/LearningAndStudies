package responses;

import java.util.ArrayList;

import data.ClientUser;

public final class ResponseGetStatuses extends Response {
	private static final long serialVersionUID = -9185897987176328549L;
	
	public ArrayList<ClientUser>	Users;
	
	public ResponseGetStatuses () {}
	
	public ResponseGetStatuses (ArrayList<ClientUser> users) {
		Users = users;
	}
	
	public ArrayList<ClientUser> getUsers () {
		return Users;
	}
	
	public ResponseType obtainResponseType() {
		return ResponseType.GetStatuses;
	}

	public void setUsers (ArrayList<ClientUser> users) {
		Users = users;
	}
}
