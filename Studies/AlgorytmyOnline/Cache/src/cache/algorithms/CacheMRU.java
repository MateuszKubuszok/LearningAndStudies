package cache.algorithms;

public class CacheMRU<Key, Value> extends CacheByTime<Key, Value> {
	public CacheMRU(ValueSource<Key, Value> source) {
		super(source);
	}
	
	public CacheMRU(ValueSource<Key, Value> source, int maxSize) {
		super(source, maxSize);
	}

	@Override
	protected void freeSpace() {
		keyStorage.last().remove();
	}
}
