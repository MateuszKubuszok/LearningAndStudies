package connect.four.game;

import static java.lang.Math.abs;

public class Board {
	private final Dot[][] board;
	
	private final int boardWidth;
	private final int boardHeight;
	
	private int recentColumn;
	private int recentRow;

	public Board(int boardWidth, int boardHeight) {
		this.boardWidth = boardWidth;
		this.boardHeight = boardHeight;
		board = new Dot[boardWidth][boardHeight];
		clear();
	}

	public void occupy(int column, EPlayer player) {
		if (column < 0)
			return;
		
		int row = findNotEmptyRow(column);

		if (isLastRow(row))
			return;
		
		recentColumn = column;
		recentRow = row + 1;
		
		board[recentColumn][recentRow].setPlayer(player);
	}
	
	public void clearField(int column) {
		board[column][findNotEmptyRow(column)].clear();
	}

	public boolean isColumnFree(int column) {
		return board[column][getLastRow()].isEmpty();
	}

	public boolean hasEndedInDraw() {
		int row = getLastRow();
		for (int column = getLastColumn(); column >= 0; column--)
			if (board[column][row].isEmpty())
				return false;
		return true;
	}

	public boolean isGameEnded(EPlayer player) {
		for (; isOccupied(recentColumn, recentRow); recentRow++);
		recentRow--;

		return hasRecentActionMake4InRow(player)
				|| hasRecentActionMake4InColumn(player)
				|| hasRecentActionMake4InFirstDiagonal(player)
				|| hasRecentActionMake4InSecondDiagonal(player);
	}

	public void clear() {
		for (int column = 0; column < boardWidth; ++column)
			for (int row = 0; row < boardHeight; ++row)
				board[column][row] = new Dot();
				//board[column][row] = (column%2 + row %2) %2 ==1 ? new Dot(EPlayer.HUMAN) : new Dot(EPlayer.COMPUTER);
	}
	
	public int getScore() {
	    int score = 0;
	    int columnScore = 0;
	    int rowScore = 0;

	    for (int column = 0; column < boardWidth; column++) {
	        columnScore = abs((boardWidth / 2) - column);
	        columnScore = (boardWidth / 2) - columnScore;

	        for (int row = 0; row < boardHeight; row++) {
	            rowScore = abs((boardHeight / 2) - row);
	            rowScore = (boardHeight / 2) - rowScore;

	            if (board[column][row].isOccupiedByComputer())
	            	score += columnScore + rowScore;
	            else if (board[column][row].isOccupiedByHuman())
	            	score -= columnScore + rowScore;                 
	        }
	    }
	    
	    return score;
	}

	public Dot getElement(int column, int row) {
		return board[column][row];
	}

	public int getWidth() {
		return boardWidth;
	}

	public int getHeight() {
		return boardHeight;
	}
	
	public int getLastColumn() {
		return boardWidth - 1;
	}
	
	public int getLastRow() {
		return boardHeight - 1;
	}
	
	private int findNotEmptyRow(int column) {
		int row;

		for (row = getLastRow(); row >= 0 && board[column][row].isEmpty(); row--) ;
		
		return row;
	}

	private boolean isLastRow(int row) {
		return row == boardHeight - 1;
	}
	
	private boolean hasRecentActionMake4InRow(EPlayer player) {
		int column1, column2;
		
		for (column1 = recentColumn; isOccupiedByPlayer(column1, recentRow, player); column1++) ;
		for (column2 = recentColumn; isOccupiedByPlayer(column2, recentRow, player); column2--) ;
		
		return (column1 - column2 - 1 >= 4);
	}
	
	private boolean hasRecentActionMake4InColumn(EPlayer player) {
		int row1, row2;
		
		for (row1 = recentRow; isOccupiedByPlayer(recentColumn, row1, player); row1++) ;
		for (row2 = recentRow; isOccupiedByPlayer(recentColumn, row2, player); row2--) ;
		
		return  (row1 - row2 - 1 >= 4);
	}
	
	private boolean hasRecentActionMake4InFirstDiagonal(EPlayer player) {
		int column1 = recentColumn;
		int row1 = recentRow;		
		while (isOccupiedByPlayer(column1, row1, player)) {
			column1++;
			row1--;
		}

		int column2 = recentColumn;
		int row2 = recentRow;
		while (isOccupiedByPlayer(column2, row2, player)) {
			column2--;
			row2++;
		}

		return (column1 - column2 - 1 >= 4);
	}
	
	private boolean hasRecentActionMake4InSecondDiagonal(EPlayer player) {
		int column1 = recentColumn;
		int row1 = recentRow;
		
		while (isOccupiedByPlayer(column1, row1, player)) {
			column1--;
			row1--;
		}

		int column2 = recentColumn;
		int row2 = recentRow;
		while (isOccupiedByPlayer(column2, row2, player)) {
			column2++;
			row2++;
		}
		
		return (column2 - column1 - 1 >= 4);
	}
	
	private boolean isOccupied(int column, int row) {
		return (0 <= column && column <= getLastColumn())
				&& (0 <= row && row <= getLastRow())
				&& !board[column][row].isEmpty();
	}
	
	private boolean isOccupiedByPlayer(int column, int row, EPlayer player) {
		return (0 <= column && column <= getLastColumn())
				&& (0 <= row && row <= getLastRow())
				&& board[column][row].isOccupiedBy(player);
	}
}