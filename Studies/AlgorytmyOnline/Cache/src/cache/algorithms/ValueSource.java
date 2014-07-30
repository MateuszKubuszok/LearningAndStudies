package cache.algorithms;

public interface ValueSource<Key,Value> {
	public Value get(Key key);
}