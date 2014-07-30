package responses;

public final class ResponseSendMessage extends Response {
	private static final long serialVersionUID = 7656128721326989295L;

	public String MessageHash;
	
	public String getMessageHash () {
		return MessageHash;
	}
	
	public ResponseType obtainResponseType () {
		return ResponseType.SendMessage;
	}
	
	public void setMessageHash (String messageHash) {
		MessageHash = messageHash;
	}
}
