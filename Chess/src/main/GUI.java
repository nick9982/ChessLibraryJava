package main;

import javax.swing.JFrame;

import main.Negamax.Score;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.*;

public class GUI extends JFrame {
	Bitboard b; // The bitboard being displayed by the GUI
	private ChessBoard cb; // The graphical chessboard
	private ArrayList<occupiedSpot> pieces = new ArrayList<occupiedSpot>(); // Pieces on the chess board
	private ArrayList<JPanel> piecePanels = new ArrayList<JPanel>(); // The JPanels for the pieces on the chess board
	private char turn = 0; // Tracks whose turn
	private boolean useBot = false; // boolean that controls whether or not bot will be active
	private boolean whiteBot = true; // boolean that controls what color the bot plays (Not implemented)
	private Object negamaxLock = new Object(); // Lock that blocks user input while negamax function is running
	private boolean negamaxDone = true; // mutex for negamax
	boolean waitingForNega = false; // blocks consecutive calls to the action listener while waiting for negamax to finish
	
	// Point is used to easily store a coordinate on the chess board
	class Point{
		public int x, y;
		public Point(int x, int y){
			this.x = x;
			this.y = y;
		}
	}
	
	// Object represents a spot, its location, and the piece associated with it
	private class occupiedSpot{
		public int x_coord;
		public int y_coord;
		public ChessPiece piece;
		
		public occupiedSpot(int x_coord, int y_coord, ChessPiece piece) {
			this.x_coord = x_coord;
			this.y_coord = y_coord;
			this.piece = piece;
		}
	}
	
	// Simple GUi constructor. Calls a more complex constructor with default settings
	public GUI(Bitboard b) {
		new GUI(b, false, false);
	}
	
	// GUI constructor, useBot will specify whether the user will play against the bot
	// whiteBot is not implemented, it would specify which color the user would be
	public GUI(Bitboard b, boolean useBot, boolean whiteBot) {
		this.useBot = useBot;
		this.whiteBot = whiteBot;
		this.b = b;
		setSize(512, 535); // set GUI size to fit chess board
		setResizable(false);
		setTitle("Chess");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		cb = new ChessBoard(this); // add the chess board to the GUI
		add(cb);
		setVisible(true);
	}
	
	private class PiecePanel extends JPanel{
		private int x, y, width, height; // piecepanel is the jpanel that contains a piece. Used for drag and drop functionality
		private int newX = 0, newY = 0;
		private ChessPiece pc; // the chess piece(graphics)
		private ChessBoard board; // the ChessBoard (graphical chess board)
		private int offsetX = 0, offsetY = 0; // offset is drag offset. for drag and drop
		
		//Negamax bot runs on a thread as to not freeze up the GUI while thinking
		public class NegamaxThread implements Runnable {
			int depth; // specify depth
			int color; // specify piece color
			Bitboard state; // specify the bitboard state
			
			//constructor
			public NegamaxThread(int depth, int color, Bitboard state) {
				this.depth = depth;
				this.color = color;
				this.state = state;
			}
			
			// thread run command
			@Override
			public void run() {
				// set mutex locking the interface input
				negamaxDone = false;
				Negamax.state = state.Duplicate();
				// start the negamax bot
				Score sc = Negamax.negamax(depth, color, "");

				// make the move the bot thinks is best
				Move mv = new Move(sc.path.substring(0, 4));
				// graphical stuff below
				board.clearLastMove();
				board.setLastMove(mv.getBitTo());
				
				// the move is actually made
				b.move(mv);
				// the move is encoded into points
				Point from = coordToPoint(sc.path.substring(0, 2));
				Point to = coordToPoint(sc.path.substring(2, 4));
				
				// remove piece at destination
				for(int h = 0; h < pieces.size(); h++) {
					if(to.x == pieces.get(h).piece.getPosX() && to.y == pieces.get(h).piece.getPosY()) {
						piecePanels.get(h).setVisible(false);
						piecePanels.remove(h);
						pieces.remove(h);
					}
				}
				
				//change the location of the piece that is moving
				int fromidx = 0;
				JPanel pp = null;
				for(int h = 0; h < pieces.size(); h++) {
					if(from.x == pieces.get(h).piece.getPosX() && from.y == pieces.get(h).piece.getPosY()) {
						pieces.get(h).piece.setPosX(to.x);
						pieces.get(h).piece.setPosY(to.y);
						pp = piecePanels.get(h);
						break;
					}
				}
				
				// check for a checkmate
				if(b.isFinished()) {
					if(turn == 0) {
						System.out.println("Checkmate! Black has lost");
					}else {
						System.out.println("Checkmate! white has lsot.:(");
					}
				}
				
				//check to see if a castle occurred
				if(b.checkCastle()) {
					Point castleFrom = bitToCell(b.castleFrom);
					Point castleTo = bitToCell(b.castleTo);
					for(int h = 0; h < pieces.size(); h++) {
						if(castleFrom.x == pieces.get(h).piece.getPosX() && castleFrom.y == pieces.get(h).piece.getPosY()) {
							System.out.println("Finds rook");
							piecePanels.get(h).setBounds(castleTo.x*width, castleTo.y*height, width, height);
							pieces.get(h).piece.setPosX(castleTo.x);
							pieces.get(h).piece.setPosY(castleTo.y);
							piecePanels.get(h).repaint();
						}
					}
				}
				
				// check to see if en passant occurred
				if(b.getEnPassantHappen()) {
					Point takenPiece = bitToCell(b.getEnPassantTake());
					for(int h = 0; h < pieces.size(); h++) {
						if(takenPiece.x == pieces.get(h).piece.getPosX() && takenPiece.y == pieces.get(h).piece.getPosY()) {
							piecePanels.get(h).setVisible(false);
							piecePanels.remove(h);
							pieces.remove(h);
						}
					}
				}
				
				// check to see if a pawn has converted into a queen
				if(b.getPawnToQueen()) {
					for(int h = 0; h < pieces.size(); h++) {
						if(to.x == pieces.get(h).piece.getPosX() && to.y == pieces.get(h).piece.getPosY()) {
							pieces.get(h).piece.changeType(turn==0?4:10);
							piecePanels.get(h).repaint();
							piecePanels.get(h).revalidate();
						}
					}
				}
				
				// set the location of the piece that moved
				pp.setBounds(to.x*width, to.y*height, width, height);
				pp.repaint();
				repaint();
				
				//change turns
				if(turn == 0) turn = 1;
				else turn = 0;
				
				//notify the GUI that it can accept user input again
				negamaxDone = true;
				synchronized(negamaxLock) {
					negamaxLock.notify();
				}
			}
		}
		
		public PiecePanel(int x_, int y_, int width, int height, ChessPiece piece, ChessBoard cb, JFrame win) {
			this.x = x_;
			this.y = y_;
			this.width = width;
			this.height = height;
			this.pc = piece;
			this.board = cb;
			
			setSize(width, height);
			setBounds(x, y, width, height);
			setBackground(new Color(0, 0, 0, 0));
			
			// event listeners that determine user interaction
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					// if the piece you are selecting is not one of your pieces return
					if(turn == 0 && pc.getType() > 5) return;
					else if(turn == 1 && pc.getType() < 6) return;
					// if the user input is blocked return
					if(waitingForNega) return;
					
					// wait for negamaxLock to be freed
					synchronized(negamaxLock) {
						while(!negamaxDone) {
							try {
								waitingForNega = true;
								negamaxLock.wait();
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
						if(waitingForNega) {
							waitingForNega = false;
							return;
						}
					}
					super.mousePressed(e);
					
					//Generate all legal moves for the selected piece
					boolean mustKingMove = false;
					if(b.getCheckCount() > 1) mustKingMove = true;
					long chckAttcks = 0L;
					if(turn == 0)
						chckAttcks = b.getBlackAttackLane();
					else
						chckAttcks = b.getWhiteAttackLane();
					
					int type = pc.getType();
					long legalMoves = 0L;
					Move pos = new Move((char)(pc.getPosX() + 97) + "" + (char)(8-pc.getPosY() + 48) + "" + (char)(pc.getPosX() + 97) + "" + (char)(8-pc.getPosY() + 48));
					long position = pos.getBitFrom();
					boolean bPin = false;
					boolean rPin = false;
					if(turn == 0) {
						bPin = (b.getPinnedWhiteB() & position) != 0;
						rPin = (b.getPinnedWhiteR() & position) != 0;
					}
					else {
						bPin = (b.getPinnedBlackB() & position) != 0;
						rPin = (b.getPinnedBlackR() & position) != 0;
					}
					switch(type) {
					case 0:
					case 6:
						if(!mustKingMove) 
							legalMoves = b.PawnMove(position, bPin, rPin, turn, false);
						break;
					case 1:
					case 7:
						if(!(bPin || mustKingMove))
							legalMoves = b.NaiveRookMove(position, rPin, chckAttcks, turn, false);
						break;
					case 2:
					case 8:
						if(!(bPin || rPin || mustKingMove))
							legalMoves = b.KnightMove(position, chckAttcks, turn, false);
						break;
					case 3:
					case 9:
						if(!(rPin || mustKingMove))
							legalMoves =b.BishopMove(position, bPin, chckAttcks, turn, false);
						break;
					case 4:
					case 10:
						if(!mustKingMove) {
							if(rPin) legalMoves = b.NaiveRookMove(position, rPin, chckAttcks, turn, false);
							else if(bPin) b.BishopMove(position, bPin, chckAttcks, turn, false);
							else legalMoves = b.NaiveRookMove(position, rPin, chckAttcks, turn, false) | b.BishopMove(position, bPin, chckAttcks, turn, false);
						}
						break;
					case 5:
					case 11:
						legalMoves= b.KingMove(position, turn, true, false);
					}
					
					
					// Set the legal moves to a boolean list, and repaint the GUI
					board.clearLegalSquares();
					board.setLegalSquares(legalMoves);
					
					// get initial offset in beginning of mouse drag
					offsetX = e.getX() + win.getLocationOnScreen().x;
					offsetY = e.getY() + win.getLocationOnScreen().y;
				}
				public void mouseReleased(MouseEvent e) {
					
					// MOVES ARE MADE IN HERE
					
					if(turn == 0 && pc.getType() > 5) return;
					else if(turn == 1 && pc.getType() < 6) return;
					
					//Where the piece was dropped, the destination cell can be decoded
					int xCell = (int) Math.ceil((double)(newX+32) / width);
					int yCell = (int) Math.ceil((double)newY / height);
					String move = (char)(pc.getPosX() + 97) + "" + (char)(8-pc.getPosY() + 48) + "" + (char)(xCell + 96) + "" + (char)(8-yCell + 49);
					// make the move
					Move mv = new Move(move);
					// if move is illegal, nothing happens
					if(!b.move(mv)) {
						setBounds(x, y, getWidth(), getHeight());
					}
					// if move is legal update the interface
					else {
						board.clearLastMove();
						board.clearLegalSquares();
						board.setLastMove(mv.getBitTo());
						x = (xCell-1) * width;
						y = (yCell-1) * height;
						
						// remove piece from destination. Captured
						for(int i = 0; i < pieces.size(); i++) {
							if(xCell-1 == pieces.get(i).piece.getPosX() && yCell-1 == pieces.get(i).piece.getPosY()) {
								piecePanels.get(i).setVisible(false);
								piecePanels.remove(i);
								pieces.remove(i);
							}
						}
						
						// if checkmate the game ends
						pc.setPosX(xCell-1);
						pc.setPosY(yCell-1);
						if(b.isFinished()) {
							if(turn == 0) {
								System.out.println("Checkmate! Black has lost");
							}else {
								System.out.println("Checkmate! white has lsot.:(");
							}
						}
						
						// if there is a castle update the interface
						if(b.checkCastle()) {
							Point castleFrom = bitToCell(b.castleFrom);
							Point castleTo = bitToCell(b.castleTo);
							for(int i = 0; i < pieces.size(); i++) {
								if(castleFrom.x == pieces.get(i).piece.getPosX() && castleFrom.y == pieces.get(i).piece.getPosY()) {
									piecePanels.get(i).setBounds(castleTo.x*width, castleTo.y*height, width, height);
									pieces.get(i).piece.setPosX(castleTo.x);
									pieces.get(i).piece.setPosY(castleTo.y);
								}
							}
						}
						
						// if en passant happened, reflect this on the interface
						if(b.getEnPassantHappen()) {
							Point takenPiece = bitToCell(b.getEnPassantTake());
							for(int i = 0; i < pieces.size(); i++) {
								if(takenPiece.x == pieces.get(i).piece.getPosX() && takenPiece.y == pieces.get(i).piece.getPosY()) {
									piecePanels.get(i).setVisible(false);
									piecePanels.remove(i);
									pieces.remove(i);
								}
							}
						}
						
						// if the pawn upgraded to queen, update this on the interface
						if(b.getPawnToQueen()) {
							for(int i = 0; i < pieces.size(); i++) {
								if(xCell-1 == pieces.get(i).piece.getPosX() && yCell-1 == pieces.get(i).piece.getPosY()) {
									pieces.get(i).piece.changeType(turn==0?4:10);
									piecePanels.get(i).repaint();
									piecePanels.get(i).revalidate();
								}
							}
						}
						setBounds(x, y, getWidth(), getHeight());
						repaint();
						if(turn == 0) turn = 1;
						else turn = 0;
						// if useBot is true, begin the negamax algorithm
						if(useBot && !b.isFinished()) {
							Negamax.state = b.Duplicate();
							NegamaxThread th = new NegamaxThread(4, -1, b);
							Thread t = new Thread(() -> th.run());
							t.start();
						}
					}
				}
			});
			
			// Repositions piece when mouse is dragging
			addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseDragged(MouseEvent e) {
					if(turn == 0 && pc.getType() > 5) return;
					else if(turn == 1 && pc.getType() < 6) return;
					super.mouseDragged(e);
					newX = e.getXOnScreen() - offsetX;
					newY = e.getYOnScreen()  - offsetY;
					setBounds(newX, newY, getWidth(), getHeight());
				}
			});
		}
		
		// draw the chess piece over the panel with a transparent background
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.f));
			g2d.drawImage(pc.getImage(), 0, 0, this.width, this.height, this);
		}
	}
	
	// shortcut to turn long into a point
	private Point bitToCell(long move) {
		long position = 1L;
		Point result = new Point(0, 0);
		for(int i = 0; i < 64; i++) {
			if((position & move) != 0) {
				result = new Point(i%8, (int)(i/8));
				break;
			}
			position <<= 1L;
		}
		
		return result;
		
	}
	
	// graphical chess board
	private class ChessBoard extends JPanel{
		// size of board
		private int x = 500, y = 500;
		// size of pieces
		private int sqSizeX = (int) Math.floor((double)this.x/8);
		private int sqSizeY = (int) Math.floor((double)this.y/8);
		// coloring of the special squares. Like last move and legal move previewer
		boolean legalSquares[] = new boolean[64];
		boolean lastMove[] = new boolean[64];
		
		// constructor which takes parent JFrame window as arg to pass onto the chess piece Panels
		public ChessBoard(JFrame parent) {
			setLayout(null);
			// no layout pure canvas
			long position = 1L;
			// read the bitboard and add the respective pieces to the graphical bitboard
			for(int i = 0; i < 64; i++) {
				for(int x = 0; x < b.getState().length; x++) {
					int sqSizeX = (int) Math.floor((double)this.x/8);
					int sqSizeY = (int) Math.floor((double)this.y/8);
					if((position & b.getState()[x]) != 0L) {
						pieces.add(new occupiedSpot((i%8), (int)(i/8), new ChessPiece(x, sqSizeX, sqSizeY, (i%8), (int)(i/8))));
					}
				}
				position <<= 1L;
			}
			
			// add the pieces to their parent JPanels
			for(int i = 0; i < pieces.size(); i++) {
				piecePanels.add(new PiecePanel(pieces.get(i).x_coord*sqSizeX, pieces.get(i).y_coord*sqSizeY, sqSizeX, sqSizeY, pieces.get(i).piece, this, parent));
				add(piecePanels.get(i));
			}
		}

		// reset the last move color
		public void clearLastMove() {
			for(int i = 0; i < 64; i++) {
				lastMove[i] = false;
			}
			repaint();
		}
		
		// reset the legal square color
		public void clearLegalSquares() {
			for(int i = 0; i < 64; i++) {
				legalSquares[i] = false;
			}
			repaint();
		}
		
		// set the last move tile to yellow based on the input long
		public void setLastMove(long l) {
			for(int i = 0; i < 64; i++) {
				if((l & 1l) != 0) {
					lastMove[i%8*8 + i/8] = true; // i %8 * 8 + i/8 rotates the image 90*
				}
				l >>>= 1L;
			}
			
			repaint();
		}
		
		// set the legal move tiles to turqoise based on input long
		public void setLegalSquares(long l) {
			for(int i = 0; i < 64; i++) {
				if((l & 1l) != 0) {
					legalSquares[i%8*8 + i/8] = true;// i %8 * 8 + i/8 rotates the image 90*
				}
				l >>>= 1L;
			}
			
			repaint();
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			setSize(this.x, this.y);
			int x = 0;
			for(int i = 0; i < 64; i++) {
				int iMod8 = 0;
				if((iMod8 = i % 8) == 0) { // stagger the chess board colors
					if(x == 0) x = 1;
					else x = 0;
				}
				if(i % 2 == x)g.setColor(Color.decode("#c89f74")); // every other tile paint this color
				else g.setColor(Color.decode("#a37043")); //  every other tile paint this color
				if(lastMove[i]) g.setColor(Color.decode("#EAF077")); // paint last move square yellow
				if(legalSquares[i]) g.setColor(Color.decode("#9CDCDC")); // paint legal move squares turqoise
				g.fillRect((int)(i/8) * sqSizeX, iMod8 * sqSizeY, sqSizeX, sqSizeY); // color the tile
			}
		}
	}
	
	// converting a string to a point
	private Point coordToPoint(String coord) {
		return new Point((int)coord.charAt(0)-97, 7-((int)coord.charAt(1)-49));
	}
}
