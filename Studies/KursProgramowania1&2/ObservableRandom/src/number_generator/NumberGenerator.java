package number_generator;

public class NumberGenerator extends observer.Observable {	
	public NumberGenerator () {
		new Thread (new Runnable () {
			public void run () {
				java.util.Random Random = new java.util.Random ();
				
				while (true) {
					notifyObservers (Random.nextInt ());
					
					try {
						Thread.sleep (1000);
					} catch (InterruptedException e) {
						return;
					}
				}
			}
		}).start ();
	}
}
