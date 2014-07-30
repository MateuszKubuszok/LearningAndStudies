package trie;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class TrieBuilder {
	private static Random random = new Random();
	
	public static Trie generate(int players) {
		return generate(players, 1);
	}
	
	public static Trie generate(int players, int leadersNumber) {
		Set<Integer> allPlayers = new HashSet<Integer>();
		for (int i = 1; i <= players; i++)
			allPlayers.add(i);
		TrieNode root = new TrieNode(allPlayers);
		generateChildren(root, leadersNumber);
		return new Trie(root);
	}
	
	private static void generateChildren(TrieNode node, int leadersNumber) {
		if (node.getPlayers().size() > leadersNumber) {
			Set<Integer> headPlayers = new HashSet<Integer>();
			Set<Integer> tailPlayers = new HashSet<Integer>();
			for (Integer player : node.getPlayers())
				if (flipCoin() == Coin.HEAD)
					headPlayers.add(player);
				else
					tailPlayers.add(player);
			TrieNode headNode = new TrieNode(headPlayers);
			TrieNode tailNode = new TrieNode(tailPlayers);
			node.setHeads(headNode);
			node.setTails(tailNode);
			generateChildren(headNode, leadersNumber);
			generateChildren(tailNode, leadersNumber);
		}
	}
	
	private static Coin flipCoin() {
		return random.nextBoolean() ? Coin.HEAD : Coin.TAIL;
	}
	
	private enum Coin {
		HEAD, TAIL;
	}
}
