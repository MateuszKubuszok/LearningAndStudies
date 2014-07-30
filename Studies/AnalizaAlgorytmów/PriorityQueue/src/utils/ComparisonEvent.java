package utils;

public interface ComparisonEvent {
	public <Key extends Comparable<Key>> void onCompare(Key a, Key b);
}
