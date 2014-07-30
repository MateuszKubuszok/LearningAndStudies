package responses;

public final class ResponseLogout extends ResponseSetStatus {
	private static final long serialVersionUID = -3544269746735943369L;

	public ResponseType obtainResponseType() {
		return ResponseType.Logout;
	}
}
