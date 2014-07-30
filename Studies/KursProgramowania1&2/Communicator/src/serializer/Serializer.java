package serializer;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class Serializer {
	private ObjectInputStream	Input;
	private ObjectOutputStream	Output;
	
	public Serializer (Socket socket)
	throws IOException {
		Output = new ObjectOutputStream (socket.getOutputStream ());
		Input = new ObjectInputStream (socket.getInputStream ());
	}
	
	public Object read ()
	throws IOException, ClassNotFoundException, SocketException, EOFException {
		return Input != null ? Input.readObject () : null;
	}
	
	public void write (Object object)
	throws IOException {
		if (Output != null)
			Output.writeObject (object);
	}
}
