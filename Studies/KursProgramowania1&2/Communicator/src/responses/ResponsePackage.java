package responses;

import java.io.Serializable;
import java.util.Vector;

public final class ResponsePackage implements Serializable {
	private static final long serialVersionUID = 7766087444517184386L;
	
	public Vector<Response>	Responses;
	
	public ResponsePackage () {
		Responses = new Vector<Response> ();
	}
	
	public ResponsePackage (Vector<Response> responses) {
		Responses = responses;
	}
	
	public void add (Response Response) {
		Responses.add (Response);
	}
	
	public Vector<Response> getResponses () {
		return Responses;
	}
	
	public void setResponses (Vector<Response> responses) {
		Responses = responses;
	}
	
	public String toString () {
		return super.toString() + "(" + Responses + ")";
	}
}
