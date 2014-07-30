package server;

@SuppressWarnings("serial")
public class ServerError extends Exception {
	private ErrorType Type;
	
	public ServerError (ErrorType type, String message) {
		super (message);
		Type = type;
	}
	
	public ErrorType getErrorType () {
		return Type;
	}
	
	public enum ErrorType {
		CannotOpenServer,
		CannotConnetClient,
		CannotHandleConnection,
		UknownHost,
		InvalidCommand
	}
}
