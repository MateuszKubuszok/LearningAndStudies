import java.util.Vector;

public class Observable {
	private Vector<Observer> Observers;
	
	public Observable () {
		this.Observers = new Vector<Observer> ();
	}
	
	public void addObserver (Observer newObserver) {
		this.Observers.add (newObserver);
	}
	
	public void informAll () {
		int i;
		
		for (i = 0; i < this.Observers.size (); i++)
			this.Observers.get (i).refresh ();
	}
}
