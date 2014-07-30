package binomial.heap.with.statistics;

import binomial.heap.BinomialNode;

public class BinomialNodeWithStatistics<Key extends Comparable<Key>, Value>
		extends BinomialNode<Key, Value> {
	private final BinomialHeapWithStatistics<Key, Value> owner;

	public BinomialNodeWithStatistics(Key key, Value value) {
		this(null, key, value);
	}

	public BinomialNodeWithStatistics(
			BinomialHeapWithStatistics<Key, Value> owner, Key key, Value value) {
		super(key, value);
		this.owner = owner;
	}

	BinomialHeapWithStatistics<Key, Value> owner() {
		return owner;
	}
}
