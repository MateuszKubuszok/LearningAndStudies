package cache.dfs;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class DfsGenerator {
	private static final Random random = new Random();
	
	public static DfsNode generateTree(int degree, int min, int max) {
		int number = 0;
		DfsNode root = DfsNode.createRoot(String.valueOf(number++));
		Set<DfsNode> currentLevel = new HashSet<DfsNode>();
		Set<DfsNode> nextLevel = new HashSet<DfsNode>();
		currentLevel.add(root);
		for (int currentDegree = 1; currentDegree <= degree; currentDegree++) {
			for (DfsNode node : currentLevel) {
				for (int i = nodesNumber(min, max); i > 0; i--) {
					nextLevel.add(node.addNode(String.valueOf(number++)));
				}
			}
			Set<DfsNode> tmp = currentLevel;
			currentLevel = nextLevel;
			nextLevel = tmp;
			nextLevel.clear();
		}
		return root;
	}
	
	private static int nodesNumber(int min, int max) {
		return random.nextInt(max-min) + min;
	}
}
