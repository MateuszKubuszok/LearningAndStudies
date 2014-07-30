package binomial.heap.with.statistics;

import utils.Comparison;
import utils.ComparisonEvent;
import binomial.heap.BinomialHeap;
import binomial.heap.BinomialNode;

public class BinomialHeapWithStatistics<Key extends Comparable<Key>, Value>
		extends BinomialHeap<Key, Value> {
	private int comparisons;

	public BinomialHeapWithStatistics() {
		super();
		comparisons = 0;
	}

	@Override
	public BinomialNode<Key, Value> head() {
		return super.head();
	}

	public int getComparisons() {
		return comparisons;
	}

	public void resetComparisons() {
		comparisons = 0;
	}

	void incrementComparison() {
		comparisons++;
	}

	@Override
	protected BinomialNode<Key, Value> createNode(Key key, Value value) {
		return new BinomialNodeWithStatistics<Key, Value>(this, key, value);
	}

	static {
		Comparison.registerHandler(new ComparisonEvent() {
			@Override
			public <Key extends Comparable<Key>> void onCompare(Key a, Key b) {
				BinomialNodeWithStatistics<?, ?> aws = null;
				BinomialNodeWithStatistics<?, ?> bws = null;

				if (a != null && a instanceof BinomialNodeWithStatistics)
					aws = ((BinomialNodeWithStatistics<?, ?>) a);
				if (b != null && b instanceof BinomialNodeWithStatistics)
					bws = ((BinomialNodeWithStatistics<?, ?>) b);

				if (aws != null && bws != null) {
					aws.owner().incrementComparison();
					if (aws.owner() != bws.owner())
						bws.owner().incrementComparison();
				} else if (aws != null) {
					aws.owner().incrementComparison();
				} else if (bws != null) {
					bws.owner().incrementComparison();
				}
			}
		});
	}
}
