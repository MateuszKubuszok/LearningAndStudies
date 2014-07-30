package responses;

public class ResponseChangePassword extends Response {
	private static final long serialVersionUID = -8575854238816438587L;

	public ResponseType obtainResponseType () {
		return ResponseType.ChangePassword;
	}
}
