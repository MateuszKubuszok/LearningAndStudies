package cache.main;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cache.algorithms.Cache;
import cache.algorithms.CacheFIFO;
import cache.algorithms.CacheFWF;
import cache.algorithms.CacheLFD;
import cache.algorithms.CacheLFU;
import cache.algorithms.CacheLRU;
import cache.algorithms.ValueSource;
import cache.dfs.DfsGenerator;
import cache.dfs.DfsNode;

public class Main {
	private static final Map<CacheType, Integer> costs = new EnumMap<CacheType, Integer>(CacheType.class);
	
	public static void main(String[] args) {
		int min = 3;
		int max = 5;
		int degree = 5;
		int cacheSize = 20;
		DfsNode root = DfsGenerator.generateTree(degree, min, max);
		List<DfsNode> requests = root.dfsOrder();
		System.out.println(requests);
		
		List<Cache<DfsNode, String>> caches = new LinkedList<Cache<DfsNode, String>>();
		caches.add(new CacheLRU<DfsNode, String>(getSourceFor(CacheType.LRU), cacheSize));
		caches.add(new CacheFIFO<DfsNode, String>(getSourceFor(CacheType.FIFO), cacheSize));
		caches.add(new CacheLFU<DfsNode, String>(getSourceFor(CacheType.LFU), cacheSize));
		caches.add(new CacheFWF<DfsNode, String>(getSourceFor(CacheType.FWF), cacheSize));
		caches.add(new CacheLFD<DfsNode, String>(getSourceFor(CacheType.LFD), requests, cacheSize));
		
		for (Cache<DfsNode, String> cache : caches)
			for (DfsNode request : requests)
				cache.get(request);
		
		System.out.println(costs);
	}
	
	private static ValueSource<DfsNode, String> getSourceFor(final CacheType type) {
		return new ValueSource<DfsNode, String>() {
			@Override
			public String get(DfsNode key) {
				int cost = costs.containsKey(type) ? costs.get(type) : 0;
				costs.put(type, cost+1);
				return key.getName();
			}
		};
	}
	
	private enum CacheType {
		LRU("Least Recent Use"),
		FIFO("First-In First-Out"),
		LFU("Least Frequently Used"),
		FWF("Flush When Full"),
		LFD("Longest Forward Distance");
		
		private final String name;
		
		private CacheType(String name) {
			this.name = name;
		}
		
		public String toString() {
			return name;
		}
	}
}
