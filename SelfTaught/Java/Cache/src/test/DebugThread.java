package test;

import cache.Cache;

public class DebugThread extends Thread {
	private Cache<Integer, String> Cache;
	
	public DebugThread (Cache<Integer, String> cache) {
		Cache = cache;
	}
	
	public void run () {
		while (true) {
			Cache.debugContent ();
			try {
				sleep (2000);
			} catch (InterruptedException e) {}
		}
	}
}
