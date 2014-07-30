package test;

import java.util.Random;
import cache.Cache;

public class RequestThread extends Thread {
	private Cache<Integer, String> Cache;
	
	public RequestThread (Cache<Integer, String> cache) {
		Cache = cache;
	}
	
	public void run () {
		Random randy = new Random ();
		while (true) {
			Cache.getDataFor (randy.nextInt (10));
			try {
				sleep (1);
			} catch (InterruptedException e) {}
		}
	}
}
