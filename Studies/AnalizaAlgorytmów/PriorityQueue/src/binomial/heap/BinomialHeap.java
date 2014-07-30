package binomial.heap;

import static utils.Comparison.GREATER_THAN;
import static utils.Comparison.LESSER_OR_EQUAL_TO;
import static utils.Comparison.LESSER_THAN;
import static utils.Comparison.is;

public class BinomialHeap<Key extends Comparable<Key>, Value> {
	private BinomialNode<Key, Value> head = null;
	private BinomialNode<Key, Value> min = null;

	public BinomialNode<Key, Value> delete(BinomialNode<Key, Value> x) {
		decreaseKey(x, null);
		setUpMin();
		return extractMin();
	}

	public BinomialNode<Key, Value> extractMin() {
		BinomialNode<Key, Value> min = findMin();
		head = deleteMin(head, min);
		setUpMin();
		return min;
	}

	public BinomialNode<Key, Value> findMin() {
		return min;
	}

	public BinomialNode<Key, Value> insert(Key key, Value value) {
		if (key == null)
			throw new NullPointerException();
		BinomialNode<Key, Value> node = createNode(key, value);
		head = union(head, node);
		setUpMin();
		return node;
	}

	public boolean isEmpty() {
		return head == null;
	}

	public void union(BinomialHeap<Key, Value> merged) {
		head = union(head, merged.head);
		setUpMin();
	}

	protected BinomialNode<Key, Value> createNode(Key key, Value value) {
		return new BinomialNode<Key, Value>(key, value);
	}

	protected BinomialNode<Key, Value> head() {
		return head;
	}

	private void setUpMin() {
		min = null;
		for (BinomialNode<Key, Value> x = head; x != null; x = x.sibling())
			if (min == null || is(min, GREATER_THAN, x))
				min = x;
	}

	public static <Key extends Comparable<Key>, Value> BinomialNode<Key, Value> decreaseKey(
			BinomialNode<Key, Value> x, Key key) {
		if (key != null && is(key, GREATER_THAN, x.key()))
			throw new IllegalArgumentException();
		x.key(key);
		BinomialNode<Key, Value> y = x, z = y.parent();
		while (z != null && is(y, LESSER_THAN, z)) {
			y.swap(z);
			y = z;
			z = y.parent();
		}
		return y;
	}

	private static <Key extends Comparable<Key>, Value> BinomialNode<Key, Value> deleteMin(
			BinomialNode<Key, Value> head, BinomialNode<Key, Value> x) {
		BinomialNode<Key, Value> prevX, nextX;

		if (x == null)
			return head;

		if (x == head)
			head = x.sibling();
		else
			for (prevX = head; prevX != null; prevX = prevX.sibling())
				if (prevX.sibling() == x) {
					prevX.sibling(x.sibling());
					break;
				}

		x = x.child();
		if (x != null) {
			x.parent(null);
			prevX = null;
			while (x.sibling() != null) {
				nextX = x.sibling();
				x.sibling(prevX);
				prevX = x;
				x = nextX;
				x.parent(null);
			}
			x.sibling(prevX);
		}

		return union(head, x);
	}

	private static <Key extends Comparable<Key>, Value> void link(
			BinomialNode<Key, Value> y, BinomialNode<Key, Value> z) {
		y.parent(z);
		y.sibling(z.child());
		z.child(y);
		z.degree(z.degree() + 1);
	}

	private static <Key extends Comparable<Key>, Value> BinomialNode<Key, Value> merge(
			BinomialNode<Key, Value> h1, BinomialNode<Key, Value> h2) {
		BinomialNode<Key, Value> a = h1, b = h2, c;
		h1 = minDegree(a, b);

		if (h1 == null)
			return null;
		if (h1 == b)
			b = a;
		a = h1;
		while (b != null) {
			if (a.sibling() == null) {
				a.sibling(b);
				return h1;
			}
			if (a.sibling().degree() < b.degree())
				a = a.sibling();
			else {
				c = b.sibling();
				b.sibling(a.sibling());
				a.sibling(b);
				a = a.sibling();
				b = c;
			}
		}
		return h1;
	}

	private static <Key extends Comparable<Key>, Value> BinomialNode<Key, Value> minDegree(
			BinomialNode<Key, Value> a, BinomialNode<Key, Value> b) {
		if (a == null) {
			if (b == null)
				return null;
			return b;
		}
		if (b == null)
			return a;
		return (a.degree() <= b.degree() ? a : b);
	}

	private static <Key extends Comparable<Key>, Value> BinomialNode<Key, Value> union(
			BinomialNode<Key, Value> h1, BinomialNode<Key, Value> h2) {
		BinomialNode<Key, Value> head = merge(h1, h2);
		if (head == null)
			return null;

		BinomialNode<Key, Value> prevX = null, x = head, nextX = x.sibling();

		while (nextX != null) {
			if (x.degree() != nextX.degree()
					|| (nextX.sibling() != null && nextX.sibling().degree() == x
							.degree())) {
				prevX = x;
				x = nextX;
			} else if (is(x, LESSER_OR_EQUAL_TO, nextX)) {
				x.sibling(nextX.sibling());
				link(nextX, x);
			} else {
				if (prevX == null)
					head = nextX;
				else
					prevX.sibling(nextX);
				link(x, nextX);
				x = nextX;
			}
			nextX = x.sibling();
		}
		return head;
	}
}
