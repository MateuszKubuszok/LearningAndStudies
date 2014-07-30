package main;

import generator_observer.GeneratorObserver;
import number_generator.NumberGenerator;

public class Main {
	public static void main(String[] args) {
		NumberGenerator Observable = new NumberGenerator ();
		
		Observable.addObserver (new GeneratorObserver ("Observer #1", 0));
		Observable.addObserver (new GeneratorObserver ("Observer #2", 1500));
		Observable.addObserver (new GeneratorObserver ("Observer #3", 2250));
	}
}
