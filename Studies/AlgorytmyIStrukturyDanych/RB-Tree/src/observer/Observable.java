package observer;

public interface Observable {
	public void addObserver (Observer object);
	
	public void informAll ();
}
