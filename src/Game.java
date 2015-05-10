import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.util.List;


public class Game extends JPanel {
	Board board;
	Seed currentPlayer;
	boolean gameOn;
	Scanner sc = new Scanner(System.in);
	
	Seed computerPlayer, humanPlayer;
	
	private static final int CELL_SIZE = 150;
	private static final int CELL_GAP = 3;
	private static final int CELL_PADDING = 40;
	
	private int hoverRow, hoverCol;
	boolean playerLocked = false;
	
	public Game() {
		init();
		setFocusable(true);
		MouseAdapter adapter = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (gameOn) {
					int row = e.getY() / (CELL_SIZE + CELL_GAP);
					int col = e.getX() / (CELL_SIZE + CELL_GAP);
					if (validMove(row, col)) {
						board.setCell(row, col, currentPlayer);
					}
					repaint();
					if (gameOn) {
						checkFinish(currentPlayer);
						currentPlayer = (currentPlayer == Seed.X) ? Seed.O : Seed.X;
						playerLocked = true;
					}
					
					
					Timer timer = new Timer(1000, new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							minimax(2, board, currentPlayer, Integer.MIN_VALUE, Integer.MAX_VALUE);
							repaint();
							if (gameOn) {
								checkFinish(currentPlayer);
								currentPlayer = (currentPlayer == Seed.X) ? Seed.O : Seed.X;
								playerLocked = false;
							}
						}
					});
					timer.start();
					timer.setRepeats(false);
				}
			}
			public void mouseMoved(MouseEvent e) {
				if (!playerLocked) {
					int newRow = e.getY() / (CELL_SIZE + CELL_GAP);
					int newCol = e.getX() / (CELL_SIZE + CELL_GAP);
					if (newRow != hoverRow || newCol != hoverCol) {
						hoverRow = newRow;
						hoverCol = newCol;
						repaint();
					}
				}
			}
		};
		KeyListener l = new KeyAdapter() {
			@Override
            public void keyPressed(KeyEvent e) {
				if (!gameOn) {
	            	if (e.getKeyCode() == KeyEvent.VK_R) {
	            		init();
	            		repaint();
	            	}
				}
            }
		};
		this.addMouseListener(adapter);
		this.addMouseMotionListener(adapter);
		this.addKeyListener(l);
	}
	
	public void init() {
		board = new Board();
		board.init();
		gameOn = true;
		computerPlayer = Seed.O;
		humanPlayer = Seed.X;
		currentPlayer = humanPlayer;
		//currentPlayer = Seed.X;
		hoverRow = -1;
		hoverCol = -1;
	}
	
	public boolean validMove(int row, int col) {
		if (board.getCell(row, col) != Seed.EMPTY)
			return false;
		return true;
	}
	
	public void checkFinish(Seed currentPlayer) {
		if (board.gameWon(currentPlayer)) {
			gameOn = false;
			if (currentPlayer == Seed.X)
				System.out.println("X has won!");
			else
				System.out.println("O has won!");
		} else if (board.gameTied()) {
			gameOn = false;
			System.out.println("Game over. Neither side has won.");
		} 
	}
	
	//AI
	private int minimax(int depth, Board gameState, Seed player, int alpha, int beta) {
		
		if (depth == 0 || gameState.gameWon(Seed.X) || gameState.gameWon(Seed.O) || gameState.gameTied()) {
			//System.out.println(evaluate(gameState, player) + " Depth: " + depth);
			return evaluate(gameState, player);
		}
		
		Seed opponent = (player == Seed.X) ? Seed.O : Seed.X;
		List<int[]> moves = generateMoves(gameState);
		int[] bestMove = new int[2];
		
		for (int[] move : moves) {
			Cell[][] copy = gameState.copy();
			copy[move[0]][move[1]].setContent(player);
			int current = minimax(depth-1, new Board(copy), opponent, alpha, beta);
			
			//if maximize (i.e. computer)
			if (player == computerPlayer) {	
				if (current > alpha) {
					alpha = current;
					bestMove = move;
				}
			} 
			//if minimize (i.e. human)
			else if (player == humanPlayer){
				if (current < beta) {
					beta = current;
					bestMove = move;
				}
			}
			//prune
			if (alpha > beta)
				break;
		}
		
		//make move
		gameState.setCell(bestMove[0], bestMove[1], player);
		//return score
		if (player == computerPlayer)
			return alpha;
		else
			return beta;
		
	}
	
	private int evaluate(Board gameState, Seed player) {
	      int score = 0;
	      // Evaluate score for each of the 8 lines (3 rows, 3 columns, 2 diagonals)
	      score += scoreLine(gameState, player, 0, 0, 0, 1, 0, 2);  // row 0
	      score += scoreLine(gameState, player, 1, 0, 1, 1, 1, 2);  // row 1
	      score += scoreLine(gameState, player, 2, 0, 2, 1, 2, 2);  // row 2
	      score += scoreLine(gameState, player, 0, 0, 1, 0, 2, 0);  // col 0
	      score += scoreLine(gameState, player, 0, 1, 1, 1, 2, 1);  // col 1
	      score += scoreLine(gameState, player, 0, 2, 1, 2, 2, 2);  // col 2
	      score += scoreLine(gameState, player, 0, 0, 1, 1, 2, 2);  // diagonal 1
	      score += scoreLine(gameState, player, 0, 2, 1, 1, 2, 0);  // diagonal 2
	      return score;
	   }
	
	private int scoreLine(Board gameState, Seed player, int row1, int col1, int row2, int col2, int row3, int col3) {
	      int score = 0;
	      Cell[][] cells = gameState.getCells();
	      Seed opponent = (player == Seed.X) ? Seed.O : Seed.X;
	      Seed empty = Seed.EMPTY;
	      
	      Seed firstCell = cells[row1][col1].getContent();
	      Seed secondCell = cells[row2][col2].getContent();
	      Seed thirdCell = cells[row3][col3].getContent();
	      
	      if (firstCell == player) {
	    	  if (secondCell == player) {
	    		  if (thirdCell == player) //1 1 1
	    			  score = 100;
	    		  else if (thirdCell == empty) //1 1 0
	    			  score = 10;
	    		  else { //1 1 -1
	    			  score = 0;
	    		  }
	    	  }
	    	  else if (secondCell == empty) {
	    		  if (thirdCell == player) //1 0 1
	    			  score = 10;
	    		  else if (thirdCell == empty) //1 0 0
	    			  score = 1;
	    		  else { //1 0 -1
	    			  score = 0;
	    		  }
	    	  } else { //1 -1 --
	    		  score = 0;
	    	  }
	      } else if (firstCell == empty) {
	    	  if (secondCell == player) {
	    		  if (thirdCell == player) //0 1 1
	    			  score = 10;
	    		  else if (thirdCell == empty) //0 1 0
	    			  score = 1;
	    		  else { //0 1 -1
	    			  score = 0;
	    		  }
	    	  }
	    	  else if (secondCell == empty) {
	    		  if (thirdCell == player) //0 0 1
	    			  score = 1;
	    		  else if (thirdCell == empty) //0 0 0
	    			  score = 0;
	    		  else { //0 0 -1
	    			  score = -1;
	    		  }
	    	  } else { 
	    		  if (thirdCell == player) //0 -1 1
	    			  score = 0;
	    		  else if (thirdCell == empty) //0 -1 0
	    			  score = -1;
	    		  else { //0 -1 -1
	    			  score = -10;
	    		  }
	    	  }
	      } else { 
	    	  if (secondCell == player) { //-1 1 --
	    		  score = 0;
	    	  }
	    	  else if (secondCell == empty) {
	    		  if (thirdCell == player) //-1 0 1
	    			  score = 0;
	    		  else if (thirdCell == empty) //-1 0 0
	    			  score = -1;
	    		  else { //-1 0 -1
	    			  score = -10;
	    		  }
	    	  } else { 
	    		  if (thirdCell == player) //-1 -1 1
	    			  score = 0;
	    		  else if (thirdCell == empty) //-1 -1 0
	    			  score = -10;
	    		  else { //-1 -1 -1
	    			  score = -100;
	    		  }
	    	  }
	      }
	      return score;
	   }

	
	private List<int[]> generateMoves(Board gameState) {
		List<int[]> moves = new ArrayList<int[]>(); 
		
		for (int row = 0; row < 3; ++row) {
			for (int col = 0; col < 3; ++col) {
				if (gameState.getCells()[row][col].getContent() == Seed.EMPTY) {
	               moves.add(new int[] {row, col});
	            }
			}
		}
		return moves;
	}
	
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, this.getSize().width, this.getSize().height);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
            	boolean hover = false;
                if (hoverRow == i && hoverCol == j) {
                	hover = true;
                }
                drawCell(g, board.getCell(i, j), j, i, hover);
            }
        }
        paintHover(g);
	}
	
	public void drawCell(Graphics g, Seed seed, int xPos, int yPos, boolean hover) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int x = getLocation(xPos);
		int y = getLocation(yPos);
		g2.setColor(new Color(216, 230, 221));
		g2.fillRect(x, y, CELL_SIZE, CELL_SIZE);
		
		int x1 = x + CELL_PADDING;
		int y1 = y + CELL_PADDING;
		int x2 = x + CELL_SIZE - CELL_PADDING;
		int y2 = y + CELL_SIZE - CELL_PADDING;
		
		g2.setStroke(new BasicStroke(10));
		
		if (seed == Seed.X) {
			g2.setColor(new Color(130, 130, 130));
			g2.drawLine(x1, y1, x2, y2);
			g2.drawLine(x2, y1, x1, y2);
			
		} else if (seed == Seed.O) {
			g2.setColor(new Color(60, 138, 91));
			g2.drawOval(x1, y1, CELL_SIZE - 2*CELL_PADDING, CELL_SIZE - 2*CELL_PADDING);
		} else {
			if (hover) {
				if (currentPlayer == Seed.X) {
					g2.setColor(new Color(180, 180, 180, 220));
					g2.drawLine(x1, y1, x2, y2);
					g2.drawLine(x2, y1, x1, y2);
					
				} else if (currentPlayer == Seed.O) {
					g2.setColor(new Color(135, 185, 150, 180));
					g2.drawOval(x1, y1, CELL_SIZE - 2*CELL_PADDING, CELL_SIZE - 2*CELL_PADDING);
				}
			}
		}
	}
	
	public int getLocation(int position) {
		return position * (CELL_SIZE + CELL_GAP);
	}
	
	public void paintHover(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
	}
	
	/** The entry main() method */
	public static void main(String[] args) {
		//new Game(); 
		JFrame f = new JFrame();
        f.setTitle("Invincible Tic Tac Toe");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(455, 475);
        f.setResizable(false);
        
        Game game = new Game();
        f.add(game);
        
        f.setVisible(true);
	}

}
