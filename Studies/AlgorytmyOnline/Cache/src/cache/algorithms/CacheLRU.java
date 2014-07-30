package cache.algorithms;

public class CacheLRU<Key, Value> extends CacheByTime<Key, Value> {
	public CacheLRU(ValueSource<Key, Value> source) {
		super(source);
	}
	
	public CacheLRU(ValueSource<Key, Value> source, int maxSize) {
		super(source, maxSize);
	}

	@Override
	protected void freeSpace() {
		keyStorage.first().remove();
	}
}
