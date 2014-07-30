package main;

import cache.Cache;
import test.DebugThread;
import test.RequestThread;
import test.StringSource;

public class Main {
	public static void main (String[] args) {
		Cache <Integer, String> cache = new Cache <Integer, String>
				(new StringSource ()).	// source of cache content
				setMaxKeep (5).			// after cleanup keep max 5 most requested
				setGCDelay (10).		// cleanup every 10 ms
				setResetCounter (5);	// reset counter every 5 requests
		
		// testing for deadlocks - none so far
		new RequestThread (cache).start ();
		new DebugThread (cache).start ();
	}
}
