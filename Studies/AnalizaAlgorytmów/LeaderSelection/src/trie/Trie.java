package trie;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

public class Trie {
	private final TrieNode root;
	
	Trie(TrieNode root) {
		this.root = root;
	}
	
	public int getFlips() {
		int flips = 0;
		TrieNode current = root;
		while (!current.isLeaf()) {
			flips += current.getPlayers().size();
			current = current.getTails().getPlayers().isEmpty() ?
					current.getHeads() :
					current.getTails();
		}
		return flips;
	}
	
	public int getRounds() {
		int rounds = 0;
		TrieNode current = root;
		while (!current.isLeaf()) {
			rounds++;
			current = current.getTails().getPlayers().isEmpty() ?
					current.getHeads() :
					current.getTails();
		}
		return rounds;
	}
	
	public int getMinLength() {
		return getLength(root, 0).first();
	}
	
	public int getMaxLength() {
		return getLength(root, 0).last();
	}
	
	public String toString() {
		return root.toString();
	}
	
	private SortedSet<Integer> getLength(TrieNode node, int currentDepth) {
		if (node.getPlayers().isEmpty())
			return new TreeSet<Integer>();
		else if (node.isLeaf())
			return new TreeSet<Integer>(Arrays.asList(currentDepth));
		SortedSet<Integer> lengths = new TreeSet<Integer>();
		if (node.getTails() != null)
			lengths.addAll(getLength(node.getTails(), currentDepth+1));
		if (node.getHeads() != null)
			lengths.addAll(getLength(node.getHeads(), currentDepth+1));
		return lengths;
	}
}
