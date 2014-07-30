package observer;

import java.util.ArrayList;

public class Observable {
	private ArrayList<Observer> Observers;
	
	public Observable () {
		Observers = new ArrayList<Observer> ();
	}
	
	public void addObserver (Observer observer) {
		synchronized (Observers) {
			Observers.add (observer);
		}
	}
	
	public void removeObserver (Observer observer) {
		synchronized (Observers) {
			Observers.remove (observer);
		}
	}
	
	public void notifyObservers () {
		notifyObservers (null);
	}
	
	public void notifyObservers (final Object changedProperty) {
		synchronized (Observers) {
			final Observable Parent = this;
			
			for (final Observer Observer : Observers) {
				new Thread (new Runnable () {
					public void run () {
						Observer.update (Parent, changedProperty);
					}
				}).start ();
			}
		}
	}
}
