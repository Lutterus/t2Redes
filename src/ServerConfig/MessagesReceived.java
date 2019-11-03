package ServerConfig;

public class MessagesReceived {
	private String message;
	private long time;
	private long tokenAntes;

	public MessagesReceived(String message, long time, long l) {
		setMessage(message);
		setTime(time);
		setTokenAntes(l);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getTokenAntes() {
		return tokenAntes;
	}

	public void setTokenAntes(long tokenAntes) {
		this.tokenAntes = tokenAntes;
	}
}
