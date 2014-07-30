package binomial.heap;

public class BinomialNode<Key extends Comparable<Key>, Value> implements
		Cloneable, Comparable<BinomialNode<Key, Value>> {
	private BinomialNode<Key, Value> parent = null;
	private BinomialNode<Key, Value> sibling = null;
	private BinomialNode<Key, Value> child = null;
	private int degree = 0;

	private Key key;
	private Value value;

	public BinomialNode(Key key, Value value) {
		this.key = key;
		this.value = value;
	}

	public Key key() {
		return key;
	}

	Key key(Key key) {
		return this.key = key;
	}

	public Value value() {
		return value;
	}

	int degree() {
		return degree;
	}

	void degree(int degree) {
		this.degree = degree;
	}

	protected BinomialNode<Key, Value> parent() {
		return parent;
	}

	void parent(BinomialNode<Key, Value> parent) {
		this.parent = parent;
	}

	protected BinomialNode<Key, Value> sibling() {
		return sibling;
	}

	void sibling(BinomialNode<Key, Value> sibling) {
		this.sibling = sibling;
	}

	protected BinomialNode<Key, Value> child() {
		return child;
	}

	void child(BinomialNode<Key, Value> son) {
		this.child = son;
	}

	@Override
	public int compareTo(BinomialNode<Key, Value> node) {
		if (key == null)
			return -1;
		return (node == null || node.key() == null) ? 1 : key.compareTo(node
				.key());
	}

	@Override
	public String toString() {
		return "Key:" + key + "|Value:" + value + "|Degree:" + degree;
	}

	protected void swap(BinomialNode<Key, Value> x) {
		Key tmpKey = key;
		key = x.key;
		x.key = tmpKey;

		Value tmpValue = value;
		value = x.value;
		x.value = tmpValue;
	}
}
