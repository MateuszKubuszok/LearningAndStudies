package cache.algorithms;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CacheLFD<Key, Value> extends Cache<Key, Value> {
	private final LinkedList<Key> futureRequests;
	private final Map<Key,Distance> distancesByKey;
	private final int maxSize;
	
	public CacheLFD(ValueSource<Key, Value> source, List<Key> requests) {
		this(source, requests, 100);
	}
	
	public CacheLFD(ValueSource<Key, Value> source, List<Key> requests, int maxSize) {
		super(source);
		this.futureRequests = new LinkedList<Key>(requests);
		this.distancesByKey = new HashMap<Key, Distance>();
		this.maxSize = maxSize;
	}

	@Override
	protected Value getForCache(Key key) {
		return new Distance(key).value;
	}

	@Override
	protected Value getFromCache(Key key) {
		return distancesByKey.get(key).value;
	}

	@Override
	protected boolean isEnoughPlaceInCache() {
		return distancesByKey.size() < maxSize;
	}

	@Override
	protected boolean isValueInCache(Key key) {
		if (futureRequests.isEmpty() || !futureRequests.pollFirst().equals(key))
			throw new IllegalStateException("Unexpected key in request list");
		return distancesByKey.containsKey(key);
	}

	@Override
	protected void freeSpace() {
		Distance maxDistance = null;
		for (Distance distance : distancesByKey.values()) {
			distance.update();
			if (distance.distance == -1) {
				distance.remove();
				return;
			} else if (maxDistance == null || maxDistance.distance < distance.distance)
				maxDistance = distance;
		}
		if (maxDistance != null)
			maxDistance.remove();
	}
	
	private class Distance {
		private final Key key;
		private final Value value;
		private int distance;
		
		Distance(Key key) {
			this.key = key;
			this.value = getValueFromSource(key);
			distancesByKey.put(key, this);
		}
		
		void update() {
			this.distance = futureRequests.indexOf(key);
		}
		
		void remove() {
			distancesByKey.remove(key);
		}
	}
}
