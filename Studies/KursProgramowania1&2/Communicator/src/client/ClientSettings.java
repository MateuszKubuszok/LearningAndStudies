package client;

import java.io.Serializable;

public class ClientSettings implements Serializable {
	private static final long serialVersionUID = -8729814740371775188L;
	
	public String	Host;
	public int		Port;
	
	public ClientSettings () {}
	
	public ClientSettings (String host, int port) {
		Host = host;
		Port = port;
	}
	
	public String getHost () {
		return Host;
	}
	
	public int getPort () {
		return Port;
	}
	
	public void setHost (String host) {
		Host = host;
	}
	
	public void setPort (int port) {
		Port = port;
	}
}
