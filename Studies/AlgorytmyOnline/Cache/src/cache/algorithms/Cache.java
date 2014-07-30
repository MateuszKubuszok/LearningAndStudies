package cache.algorithms;

public abstract class Cache<Key, Value> {
	private ValueSource<Key, Value> source;
	
	Cache(ValueSource<Key, Value> source) {
		this.source = source;
	}
	
	public Value get(Key key) {
		if (isValueInCache(key))
			return getFromCache(key);
		if (!isEnoughPlaceInCache())
			freeSpace();
		return getForCache(key);
	}
	
	protected abstract Value getForCache(Key key);
	protected abstract Value getFromCache(Key key);
	protected abstract boolean isEnoughPlaceInCache();
	protected abstract boolean isValueInCache(Key key);
	protected abstract void freeSpace();
	
	protected Value getValueFromSource(Key key) {
		return source.get(key);
	}
}
