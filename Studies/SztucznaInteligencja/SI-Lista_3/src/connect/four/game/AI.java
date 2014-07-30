package connect.four.game;

import static connect.four.game.EPlayer.COMPUTER;
import static connect.four.game.EPlayer.HUMAN;
import static java.lang.Math.max;
import static java.lang.Math.min;

class AI {
	private final Board board;
	private final int maxScore = 100;
	private final int invalidScore = 1000000;
	private final int maxDepth = 2;

	public AI(Board board) {
		this.board = board;
	}

	public int computeNextMove() {
		int bestMove = -1;
		int bestScore = -invalidScore;
		int score = -1;

		for (int column = 0; column < board.getWidth(); column++) {
			if (board.isColumnFree(column)) {
				score = calculateScoreForColumn(column);

				if (score > bestScore) {
					bestScore = score;
					bestMove = column;
				}
			}
		}

		return bestMove;
	}
	
	private int calculateScoreForColumn(int column) {
		board.occupy(column, COMPUTER);
		int score = calculateScore();
		board.clearField(column);
		return score;
	}

	private int calculateScoreForColumn(EPlayer player, int alpha, int beta, int depth, int column) {
		board.occupy(column, COMPUTER);
		int score = calculateScore(HUMAN, alpha, beta, depth + 1);
		board.clearField(column);
		return score;
	}
	
	private int calculateScore() {
		return calculateScore(HUMAN, -invalidScore, invalidScore, 0);
	}
	
	private int calculateScore(EPlayer player, int alpha, int beta, int depth) {
		if (board.isGameEnded(COMPUTER))
			return maxScore;
		else if (board.isGameEnded(HUMAN))
			return -maxScore;
		
		if (depth >= maxDepth)
			return board.getScore();
		
		if (player.equals(COMPUTER)) {
			for (int column = 0; column < board.getWidth(); column++) {
				if (board.isColumnFree(column)) {
					alpha = max(alpha, calculateScoreForColumn(HUMAN, alpha, beta, depth + 1, column));
					
					if (alpha >= beta) {
						return alpha;
					}
				}
			}
			
			return alpha;
		} else if (player.equals(HUMAN)) {
			for (int column = 0; column < board.getWidth(); column++) {
				if (board.isColumnFree(column)) {
					beta = min(beta, calculateScoreForColumn(COMPUTER, alpha, beta, depth + 1, column));
					
					if (alpha >= beta)
						return beta;
				}
			}
			
			return beta;
		} else {
			return 0;
		}
	}
}