package responses;

import data.UserStatus;

public class ResponseSetStatus extends Response {
	private static final long serialVersionUID = 6439422484000235603L;

	public UserStatus Status;
	
	public UserStatus getStatus () {
		return Status;
	}
	
	public ResponseType obtainResponseType() {
		return ResponseType.SetStatus;
	}
	
	public void setStatus (UserStatus status) {
		Status = status;
	}
}
