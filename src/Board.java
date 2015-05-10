
public class Board {
	public static final int ROWS = 3;
	public static final int COLS = 3;
	
	private Cell[][] cells;
	
	public Board() {
		cells = new Cell[ROWS][COLS];
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				cells[i][j] = new Cell(i, j);
			}
		}
	}
	
	public Board(Cell[][] cells) {
		this.cells = cells;
	}
	
	//Initialize or reinitialize board by clearing all cells
	public void init() {
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				cells[i][j].clear();
			}
		}
	}
	
	public boolean gameTied() {
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				if (cells[i][j].getContent() == Seed.EMPTY)
					return false;
			}
		}
		return true;
	}
	
	public boolean gameWon(Seed seed) {
		//row filled
		for (int i = 0; i < ROWS; i++) {
			if (cells[i][0].getContent() == seed && cells[i][1].getContent() == seed && cells[i][2].getContent() == seed)
				return true;
		}
		//col filled
		for (int j = 0; j < COLS; j++) {
			if (cells[0][j].getContent() == seed && cells[1][j].getContent() == seed && cells[2][j].getContent() == seed)
				return true;
		}
		//diagonal filled
		if (cells[1][1].getContent() == seed) {
			if (cells[0][0].getContent() == seed && cells[2][2].getContent() == seed)
				return true;
			if (cells[0][2].getContent() == seed && cells[2][0].getContent() == seed)
				return true;
		}
		return false;
	}
	
	public void setCell(int row, int col, Seed seed) {
		cells[row][col].setContent(seed);
	}
	
	public Seed getCell(int row, int col) {
		return cells[row][col].getContent();
	}
	
	public Cell[][] getCells() {
		return cells;
	}
	
	public Cell[][] copy() {
		Cell[][] copy = new Cell[ROWS][COLS];
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				copy[i][j] = new Cell(i, j);
				copy[i][j].setContent(cells[i][j].getContent());
			}
		}
		return copy;
	}
	
	
	public void paint() {
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				paintCell(cells[i][j].getContent());
				if (j != 2)
					System.out.print("|");
			}
			System.out.println();
			if (i != 2)
				System.out.println("-----------");
		}
	}
	
	public void paintCell(Seed seed) {
		switch(seed) {
		case EMPTY:
			System.out.print("   ");
			break;
		case X:
			System.out.print(" X ");
			break;
		case O:
			System.out.print(" O ");
			break;
		}
	}
	
}
