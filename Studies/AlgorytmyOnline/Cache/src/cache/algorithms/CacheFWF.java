package cache.algorithms;

import java.util.HashMap;
import java.util.Map;

public class CacheFWF<Key, Value> extends Cache<Key, Value> {
	private final Map<Key,Value> keyValueMap;
	private final int maxSize;
	
	public CacheFWF(ValueSource<Key, Value> source) {
		this(source, 100);
	}
	
	public CacheFWF(ValueSource<Key, Value> source, int maxSize) {
		super(source);
		this.keyValueMap = new HashMap<Key,Value>();
		this.maxSize = maxSize;
	}

	@Override
	protected Value getForCache(Key key) {
		Value value = getValueFromSource(key);
		keyValueMap.put(key, value);
		return value;
	}

	@Override
	protected Value getFromCache(Key key) {
		return keyValueMap.get(key);
	}

	@Override
	protected boolean isEnoughPlaceInCache() {
		return keyValueMap.size() < maxSize;
	}

	@Override
	protected boolean isValueInCache(Key key) {
		return keyValueMap.containsKey(key);
	}

	@Override
	protected void freeSpace() {
		keyValueMap.clear();
	}
}
