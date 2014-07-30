package responses;

import java.util.Vector;

import data.Message;

public final class ResponseGetMessages extends Response {
	private static final long serialVersionUID = 4573307775520835377L;

	public Vector<Message> Messages;
	
	public ResponseGetMessages () {
		Messages = new Vector<Message> ();
	}
	
	public Vector<Message> getMessages () {
		return Messages;
	}
	
	public ResponseType obtainResponseType() {
		return ResponseType.GetMessages;
	}
	
	public void setMessages (Vector<Message> messages) {
		Messages = messages;
	}
}
