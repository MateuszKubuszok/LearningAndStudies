package requests;

import data.Message;

public final class RequestSendMessage extends Request {
	private static final long serialVersionUID = 3256992716628054783L;

	public Message	Message;
	public String	MessageHash;
	
	public RequestSendMessage () {}
	
	public RequestSendMessage (Message message, String messageHash) {
		Message = message;
		MessageHash = messageHash;
	}
	
	public Message getMessage () {
		return Message;
	}
	
	public String getMessageHash () {
		return MessageHash;
	}
	
	public RequestType obtainRequestType() {
		return RequestType.SendMessage;
	}

	public void setMessage (Message message) {
		Message = message;
	}
	
	public void setMessageHash (String messageHash) {
		MessageHash = messageHash;
	}
}
