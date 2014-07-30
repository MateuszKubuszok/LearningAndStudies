package generator_observer;

import observer.*;

public class GeneratorObserver implements Observer {
	private int		Delay;
	private String	Name;
	
	public GeneratorObserver (String name, int delay) {
		Delay = delay > 0 ? delay : 0;
		Name = name;
	}
	
	public void update (Observable observable, Object changedProperty) {
		try {
			Thread.sleep (Delay);
		} catch (InterruptedException e) {}

		try {
			System.out.println ("Observer '"+Name+"': "+Integer.valueOf ((Integer) changedProperty));
		} catch (Exception e) {
			System.out.println (changedProperty+" is not a valid integer.");
		}
	}
}
