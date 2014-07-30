package requests;

import data.UserStatus;

public final class RequestSetStatus extends Request {
	private static final long serialVersionUID = 4191249845917746747L;

	public UserStatus Status;
	
	public RequestSetStatus () {
		Status = UserStatus.Online;
	}
	
	public RequestSetStatus (UserStatus status) {
		Status = status;
	}
	
	public UserStatus getStatus () {
		return Status;
	}
	
	public RequestType obtainRequestType() {
		return RequestType.SetStatus;
	}
	
	public void setStatus (UserStatus status) {
		Status = status;
	} 
}
