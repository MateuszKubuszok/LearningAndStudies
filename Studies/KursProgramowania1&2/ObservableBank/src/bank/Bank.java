package bank;
import java.util.Observable;

public class Bank extends Observable {
	public void newCommand (String command) {
		setChanged ();
		notifyObservers (command);
	}
}
