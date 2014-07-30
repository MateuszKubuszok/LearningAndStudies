package responses;

public final class ResponseConnectionMaintenance extends Response {
	private static final long serialVersionUID = 7398125731998045057L;

	public ResponseType obtainResponseType () {
		return ResponseType.ConnectionMaintenance;
	}
}
