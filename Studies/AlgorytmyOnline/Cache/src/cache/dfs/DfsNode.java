package cache.dfs;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DfsNode {
	private final String name;
	private final List<DfsNode> children;
	
	private DfsNode(String name) {
		this.name = name;
		this.children = new ArrayList<DfsNode>();
	}
	
	public String getName() {
		return name;
	}
	
	public DfsNode addNode(String name) {
		DfsNode file = new DfsNode(name);
		children.add(file);
		return file;
	}
	
	public List<DfsNode> getChildren() {
		return children;
	}
	
	public List<DfsNode> dfsOrder() {
		List<DfsNode> order = new LinkedList<DfsNode>();
		generateDfsOrder(this, order);
		return order;
	}
	
	public String toString() {
		return name;
	}
	
	public static DfsNode createRoot(String name) {
		return new DfsNode(name);
	}
	
	private static void generateDfsOrder(DfsNode node, List<DfsNode> order) {
		order.add(node);
		for (DfsNode child : node.children) {
			generateDfsOrder(child, order);
			order.add(node);
		}
	}
}
