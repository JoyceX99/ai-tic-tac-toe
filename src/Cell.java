
public class Cell {
	private Seed content;
	private int row, col;
	
	public Cell(int row, int col) {
		this.setRow(row); 
		this.setCol(col);
		this.setContent(Seed.EMPTY);
	}
	
	public void clear() {
		setContent(Seed.EMPTY);
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public Seed getContent() {
		return content;
	}

	public void setContent(Seed content) {
		this.content = content;
	}
}
