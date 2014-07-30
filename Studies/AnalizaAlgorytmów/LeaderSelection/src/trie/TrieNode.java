package trie;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class TrieNode {
	private Set<Integer> players;
	private TrieNode headNode;
	private TrieNode tailNode;
	
	TrieNode(Collection<Integer> players) {
		this.players = new HashSet<Integer>(players);
	}
	
	public boolean isLeaf() {
		return (tailNode == null || tailNode.players.isEmpty())
				&& (headNode == null || headNode.players.isEmpty());
	}
	
	public Set<Integer> getPlayers() {
		return players;
	}
	
	public TrieNode getHeads() {
		return headNode;
	}
	
	public void setHeads(TrieNode headNode) {
		this.headNode = headNode;
	}
	
	public TrieNode getTails() {
		return tailNode;
	}
	
	public void setTails(TrieNode tailNode) {
		this.tailNode = tailNode;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		toStringHelper(builder, "");
		return builder.toString(); 
	}
	
	private void toStringHelper(StringBuilder builder, String tab) {
		builder.append(players);
		if (tailNode != null && !tailNode.players.isEmpty()) {
			builder.append("\n" + tab + "\t").append("tail -> ");
			tailNode.toStringHelper(builder, tab + "\t");
		}
		if (headNode != null && !headNode.players.isEmpty()) {
			builder.append("\n" + tab + "\t").append("head -> ");
			headNode.toStringHelper(builder, tab + "\t");
		}
	}
}
