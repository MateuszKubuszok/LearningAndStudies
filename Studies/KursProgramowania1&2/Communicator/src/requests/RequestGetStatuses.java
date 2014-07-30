package requests;

import java.util.ArrayList;

public final class RequestGetStatuses extends Request {
	private static final long serialVersionUID = 7857358128821832254L;
	
	public ArrayList<String> Users;
	
	public RequestGetStatuses () {}
	
	public RequestGetStatuses (ArrayList<String> users) {
		Users = users;
	}
	
	public ArrayList<String> getUsers () {
		return Users;
	}
	
	public RequestType obtainRequestType() {
		return RequestType.GetStatuses;
	}

	public void setUsers (ArrayList<String> users) {
		Users = users;
	}
}
