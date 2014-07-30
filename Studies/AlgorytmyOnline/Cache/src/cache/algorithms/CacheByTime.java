package cache.algorithms;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

abstract class CacheByTime<Key, Value> extends Cache<Key, Value> {
	private final Map<Key,ValueStorage> valueStorage;
	protected final SortedSet<KeyStorage> keyStorage;
	private final int maxSize;
	
	public CacheByTime(ValueSource<Key,Value> source) {
		this(source, 100);
	}
	
	public CacheByTime(ValueSource<Key,Value> source, int maxSize) {
		super(source);
		this.keyStorage = new TreeSet<KeyStorage>();
		this.valueStorage = new HashMap<Key,ValueStorage>();
		this.maxSize = maxSize;
	}
	
	@Override
	protected Value getForCache(Key key) {
		return new ValueStorage(key).getValue();
	}
	
	@Override
	protected Value getFromCache(Key key) {
		return valueStorage.get(key).getValue();
	}
	
	@Override
	protected boolean isEnoughPlaceInCache() {
		return valueStorage.size() < maxSize;
	}
	
	@Override
	protected boolean isValueInCache(Key key) {
		return valueStorage.containsKey(key);
	}
	
	protected class ValueStorage {
		private final Value value;
		private final KeyStorage storage;
		
		public ValueStorage(Key key) {
			this.value = getValueFromSource(key);
			this.storage = new KeyStorage(key);
			valueStorage.put(key, this);
		}
		
		public Value getValue() {
			storage.update();
			return value;
		}
	}
	
	protected class KeyStorage implements Comparable<KeyStorage> {
		private final Key key;
		private long time;
		
		public KeyStorage(Key key) {
			this.key = key;
			this.time = System.currentTimeMillis();
			keyStorage.add(this);
		}
		
		public void remove() {
			keyStorage.remove(this);
			valueStorage.remove(key);
		}
		
		public void update() {
			keyStorage.remove(this);
			time = System.currentTimeMillis();
			keyStorage.add(this);
		}
		
		public int compareTo(KeyStorage valueStorage) {
			return Long.signum(time - valueStorage.time);
		}
	}
}
