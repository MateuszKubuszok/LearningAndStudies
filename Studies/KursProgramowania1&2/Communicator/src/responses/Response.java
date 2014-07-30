package responses;

import java.io.Serializable;

public abstract class Response implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public String ErrorMessage;
	
	public String getErrorMessage () {
		return ErrorMessage;
	}
	
	public abstract ResponseType obtainResponseType ();
	
	public void setErrorMessage (String errorMessage) {
		ErrorMessage = errorMessage;
	}
}
