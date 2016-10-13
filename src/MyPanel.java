import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.Random;

import javax.swing.JPanel;

public class MyPanel extends JPanel {
	private static final long serialVersionUID = 3426940946811133635L;
	private static final int GRID_X = 25;
	private static final int GRID_Y = 25;
	private static final int INNER_CELL_SIZE = 29;
	private static final int TOTAL_COLUMNS = 9;
	private static final int TOTAL_ROWS = 9;   //Last row has only one cell
	private static final int BOMB_COUNT = 12;
	boolean chainswitch=true;
	public int x = -1;
	public int y = -1;
	public int mouseDownGridX = 0;
	public int mouseDownGridY = 0;
	public Color[][] colorArray = new Color[TOTAL_COLUMNS][TOTAL_ROWS];
	public boolean[][] bombArray = new boolean[TOTAL_COLUMNS][TOTAL_ROWS];
	public MyMouseAdapter myMouseAdapter = new MyMouseAdapter();

	
	public MyPanel() {   //This is the constructor... this code runs first to initialize
		if (INNER_CELL_SIZE + (new Random()).nextInt(1) < 1) {	//Use of "random" to prevent unwanted Eclipse warning
			throw new RuntimeException("INNER_CELL_SIZE must be positive!");
		}
		if (TOTAL_COLUMNS + (new Random()).nextInt(1) < 2) {	//Use of "random" to prevent unwanted Eclipse warning
			throw new RuntimeException("TOTAL_COLUMNS must be at least 2!");
		}
		if (TOTAL_ROWS + (new Random()).nextInt(1) < 3) {	//Use of "random" to prevent unwanted Eclipse warning
			throw new RuntimeException("TOTAL_ROWS must be at least 3!");
		}
		for (int x = 0; x < TOTAL_COLUMNS; x++) {   //Create the 9x9 grid colors
			for (int y = 0; y < TOTAL_ROWS; y++) {
				this.setColor(x, y, Color.WHITE);
			}
		}
		for (int x = 0; x < BOMB_COUNT;) {		//Assign 10 bombs to random spaces
			int i = new Random().nextInt(TOTAL_COLUMNS);
			int j = new Random().nextInt(TOTAL_ROWS);
			if(bombArray[i][j] == false) { 	//Assigns a bomb if the space is doesn't have one already
				bombArray[i][j] = true;
				x++;
			}
		}
	}
	public void paintComponent(Graphics g) {	//Method in charge of painting tiles and numbers
		super.paintComponent(g);

		//Compute interior coordinates
		Insets myInsets = getInsets();
		int x1 = myInsets.left;
		int y1 = myInsets.top;
		int x2 = getWidth() - myInsets.right - 1;
		int y2 = getHeight() - myInsets.bottom - 1;
		int width = x2 - x1;
		int height = y2 - y1;

		//Paint the background
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(x1, y1, width + 1, height + 1);

		//Draw the grid minus the bottom row (which has only one cell)
		//By default, the grid will be 10x10 (see above: TOTAL_COLUMNS and TOTAL_ROWS) 
		g.setColor(Color.BLACK);
		for (int y = 0; y <= TOTAL_ROWS; y++) {
			g.drawLine(x1 + GRID_X, y1 + GRID_Y + (y * (INNER_CELL_SIZE + 1)), x1 + GRID_X + ((INNER_CELL_SIZE + 1) * TOTAL_COLUMNS), y1 + GRID_Y + (y * (INNER_CELL_SIZE + 1)));
		}
		for (int x = 0; x <= TOTAL_COLUMNS; x++) {
			g.drawLine(x1 + GRID_X + (x * (INNER_CELL_SIZE + 1)), y1 + GRID_Y, x1 + GRID_X + (x * (INNER_CELL_SIZE + 1)), y1 + GRID_Y + ((INNER_CELL_SIZE + 1) * (TOTAL_ROWS )));
		}

		//Paint cell colors and paint numbers
		for (int x = 0; x < TOTAL_COLUMNS; x++) {
			for (int y = 0; y < TOTAL_ROWS; y++) {
				Color c = this.getColor(x, y);
				g.setColor(c);
				g.fillRect(x1 + GRID_X + (x * (INNER_CELL_SIZE + 1)) + 1, y1 + GRID_Y + (y * (INNER_CELL_SIZE + 1)) + 1, INNER_CELL_SIZE, INNER_CELL_SIZE);
				
				//If the tile has been pressed, count surrounding bombs
				if (this.getColor(x, y) == Color.GRAY) {
					int bombCount = this.surroundingBombs(x, y);
					//If the number of surrounding bombs is greater than 0, paint the number
					if(bombCount > 0){
						g.setColor(Color.BLUE);
						g.drawString(bombCount + "" + "", x1 + GRID_X + (x * (INNER_CELL_SIZE + 1)) + 12, y1 + GRID_Y + (y * (INNER_CELL_SIZE + 1)) + 21);
					}
				}
			}
		}	
	}
	
	//Setter and getter methods
	public int getGridX(int x, int y) {
		Insets myInsets = getInsets();
		int x1 = myInsets.left;
		int y1 = myInsets.top;
		x = x - x1 - GRID_X;
		y = y - y1 - GRID_Y;
		if (x < 0) {   //To the left of the grid
			return -1;
		}
		if (y < 0) {   //Above the grid
			return -1;
		}
		if ((x % (INNER_CELL_SIZE + 1) == 0) || (y % (INNER_CELL_SIZE + 1) == 0)) {   //Coordinate is at an edge; not inside a cell
			return -1;
		}
		x = x / (INNER_CELL_SIZE + 1);
		y = y / (INNER_CELL_SIZE + 1);
		if (x < 0 || x > TOTAL_COLUMNS - 1 || y < 0 || y > TOTAL_ROWS - 1) {   //Outside the rest of the grid
			return -1;
		}
		return x;
	}
	public int getGridY(int x, int y) {
		Insets myInsets = getInsets();
		int x1 = myInsets.left;
		int y1 = myInsets.top;
		x = x - x1 - GRID_X;
		y = y - y1 - GRID_Y;
		if (x < 0) {   //To the left of the grid
			return -1;
		}
		if (y < 0) {   //Above the grid
			return -1;
		}
		if ((x % (INNER_CELL_SIZE + 1) == 0) || (y % (INNER_CELL_SIZE + 1) == 0)) {   //Coordinate is at an edge; not inside a cell
			return -1;
		}
		x = x / (INNER_CELL_SIZE + 1);
		y = y / (INNER_CELL_SIZE + 1);
		if (x < 0 || x > TOTAL_COLUMNS - 1 || y < 0 || y > TOTAL_ROWS -1) {   //Outside the rest of the grid
			return -1;
		}
		return y;
	}
	public int getColumns() {
		return TOTAL_COLUMNS;
	}
	public int getRows() {
		return TOTAL_ROWS;
	}
	public Color getColor(int x, int y) {
		return colorArray[x][y];
	}
	public void setColor(int x, int y, Color color) {
		colorArray[x][y] = color;
		return;
	}
	public boolean isBomb(int x, int y) {	//Method to verify if the selected square is a bomb
		return bombArray[x][y];
	}
	
	public int surroundingBombs(int x, int y) {	//Method for counting number of bombs around a tile.
		
		int bombCounter = 0;
		
		for(int i = x-1; i <= x+1; i++) {
			for(int j = y-1; j <= y+1; j++) {
				//Check if coordinates are inside the grid
				if(i < this.getColumns() && i >= 0 && j < this.getRows() && j >= 0 ) {
					if(isBomb(i,j)) {
						bombCounter++;
					}
				}
			}
		}
		return bombCounter;
	}
	public void chain(int x, int y) { //Recursive method to uncover a chain of tiles that have no surrounding bombs
		//Check if there aren't any surrounding bombs
		if(this.surroundingBombs(x, y) == 0) {
			for(int i=x-1; i<=x+1; i++) {
				for (int j=y-1; j<=y+1; j++) {
					//Check if coordinates are inside the grid
					if(i < this.getColumns() && i >= 0 && j < this.getRows() && j >= 0) {
						//Check that tile is uncovered
						if(this.getColor(i,j) == Color.WHITE){
							this.setColor(i, j, Color.GRAY);
							this.chain(i, j);
						}
					}
				}
			}
		}
	}
	public void bombPressed() {	//Method for when user loses the game. Uncovers all tiles
		for(int i = 0; i < this.getColumns(); i++) {
			for(int j = 0; j < this.getRows(); j++) {
				if(this.isBomb(i,j)) {
					this.setColor(i,j,Color.BLACK);
				} else {
					this.setColor(i, j, Color.GRAY);
				}
			}
		}
	}
}