package tree;

public class Node {
	public int Key;
	public Node Left;
	public Node Right;
	public Node Parent;
	
	public Node () {
		Left = null;
		Right = null;
		Parent = null;
	}
	
	public Node (Tree t) {
		Left = t.Nil;
		Right = t.Nil;
		Parent = t.Nil;
	}
}
