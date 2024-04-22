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
	Bitboard b;
	private ChessBoard cb;
	private ArrayList<occupiedSpot> pieces = new ArrayList<occupiedSpot>();
	private ArrayList<JPanel> piecePanels = new ArrayList<JPanel>();
	private char turn = 0;
	private boolean useBot = false;
	private boolean whiteBot = true;
	private Object negamaxLock = new Object();
	private boolean negamaxDone = true;
	boolean waitingForNega = false;
	
	class Point{
		public int x, y;
		public Point(int x, int y){
			this.x = x;
			this.y = y;
		}
	}
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
	
	public GUI(Bitboard b) {
		new GUI(b, false, false);
	}
	
	public GUI(Bitboard b, boolean useBot, boolean whiteBot) {
		this.useBot = useBot;
		this.whiteBot = whiteBot;
		this.b = b;
		setSize(512, 535);
		setResizable(false);
		setTitle("Chess");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//JPanel mainPanel = new JPanel();
		cb = new ChessBoard(this);
		//mainPanel.add(cb);
		add(cb);
		setVisible(true);
	}
	
	private class PiecePanel extends JPanel{
		private int x, y, width, height;
		private int newX = 0, newY = 0;
		private ChessPiece pc;
		private ChessBoard board;
		private int offsetX = 0, offsetY = 0;
		
		public class NegamaxThread implements Runnable {
			int depth;
			int color;
			Bitboard state;
			public NegamaxThread(int depth, int color, Bitboard state) {
				this.depth = depth;
				this.color = color;
				this.state = state;
			}
			
			@Override
			public void run() {
				negamaxDone = false;
				Negamax.state = state.Duplicate();
				Score sc = Negamax.negamax(depth, color, "");
				b.clearPrevStack();
				System.out.println(state);
				
				Move mv = new Move(sc.path.substring(0, 4));
				board.clearLastMove();
				board.setLastMove(mv.getBitTo());
				b.move(mv);
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
				if(b.isFinished()) {
					if(turn == 0) {
						System.out.println("Checkmate! Black has lost");
					}else {
						System.out.println("Checkmate! white has lsot.:(");
					}
				}
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
				if(b.getPawnToQueen()) {
					for(int h = 0; h < pieces.size(); h++) {
						if(to.x == pieces.get(h).piece.getPosX() && to.y == pieces.get(h).piece.getPosY()) {
							pieces.get(h).piece.changeType(turn==0?4:10);
							piecePanels.get(h).repaint();
							piecePanels.get(h).revalidate();
						}
					}
				}
				
				pp.setBounds(to.x*width, to.y*height, width, height);
				pp.repaint();
				repaint();
				if(turn == 0) turn = 1;
				else turn = 0;
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
			
			//System.out.println("x: " + x + ", y: " + y);
			setSize(width, height);
			setBounds(x, y, width, height);
			setBackground(new Color(0, 0, 0, 0));
			
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					if(turn == 0 && pc.getType() > 5) return;
					else if(turn == 1 && pc.getType() < 6) return;
					if(waitingForNega) return;
					
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
					
					board.clearLegalSquares();
					board.setLegalSquares(legalMoves);
					
					offsetX = e.getX() + win.getLocationOnScreen().x;
					offsetY = e.getY() + win.getLocationOnScreen().y;
				}
				public void mouseReleased(MouseEvent e) {
					
					// MOVES ARE MADE IN HERE
					// ALSO WILL WANT TO LOCK THE GUI SOMEHOW WHILE THIS IS HAPPENING, AS IN LOCK OTHER DRAG LISTENERS
					
					if(turn == 0 && pc.getType() > 5) return;
					else if(turn == 1 && pc.getType() < 6) return;
					
					int xCell = (int) Math.ceil((double)(newX+32) / width);
					int yCell = (int) Math.ceil((double)newY / height);
					String move = (char)(pc.getPosX() + 97) + "" + (char)(8-pc.getPosY() + 48) + "" + (char)(xCell + 96) + "" + (char)(8-yCell + 49);
					Move mv = new Move(move);
					//System.out.println(move);
					if(!b.move(mv)) {
						setBounds(x, y, getWidth(), getHeight());
					}
					else {
						board.clearLastMove();
						board.clearLegalSquares();
						board.setLastMove(mv.getBitTo());
						System.out.println(b.score());
						x = (xCell-1) * width;
						y = (yCell-1) * height;
						
						
						for(int i = 0; i < pieces.size(); i++) {
							if(xCell-1 == pieces.get(i).piece.getPosX() && yCell-1 == pieces.get(i).piece.getPosY()) {
								piecePanels.get(i).setVisible(false);
								piecePanels.remove(i);
								pieces.remove(i);
							}
						}
						
						pc.setPosX(xCell-1);
						pc.setPosY(yCell-1);
						if(b.isFinished()) {
							if(turn == 0) {
								System.out.println("Checkmate! Black has lost");
							}else {
								System.out.println("Checkmate! white has lsot.:(");
							}
						}
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
						b.printState();
						if(useBot && !b.isFinished()) {
							Negamax.state = b.Duplicate();
							NegamaxThread th = new NegamaxThread(4, -1, b);
							Thread t = new Thread(() -> th.run());
							t.start();
						}
					}
				}
			});
			
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
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.f));
			g2d.drawImage(pc.getImage(), 0, 0, this.width, this.height, this);
		}
	}
	
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
	
	private class ChessBoard extends JPanel{
		private int x = 500, y = 500;
		private int sqSizeX = (int) Math.floor((double)this.x/8);
		private int sqSizeY = (int) Math.floor((double)this.y/8);
		boolean legalSquares[] = new boolean[64];
		boolean lastMove[] = new boolean[64];
		public ChessBoard(JFrame parent) {
			setLayout(null);
			long position = 1L;
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
			
			for(int i = 0; i < pieces.size(); i++) {
				piecePanels.add(new PiecePanel(pieces.get(i).x_coord*sqSizeX, pieces.get(i).y_coord*sqSizeY, sqSizeX, sqSizeY, pieces.get(i).piece, this, parent));
				add(piecePanels.get(i));
			}
			
			for(int i = 0; i < 64; i++) {
				legalSquares[i] = false;
			}
		}
		
		public void clearLastMove() {
			for(int i = 0; i < 64; i++) {
				lastMove[i] = false;
			}
			repaint();
		}
		
		public void clearLegalSquares() {
			for(int i = 0; i < 64; i++) {
				legalSquares[i] = false;
			}
			repaint();
		}
		
		public void setLastMove(long l) {
			for(int i = 0; i < 64; i++) {
				if((l & 1l) != 0) {
					lastMove[i%8*8 + i/8] = true;
				}
				l >>>= 1L;
			}
			
			repaint();
		}
		
		public void setLegalSquares(long l) {
			for(int i = 0; i < 64; i++) {
				if((l & 1l) != 0) {
					legalSquares[i%8*8 + i/8] = true;
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
				if((iMod8 = i % 8) == 0) {
					if(x == 0) x = 1;
					else x = 0;
				}
				if(i % 2 == x)g.setColor(Color.decode("#c89f74"));
				else g.setColor(Color.decode("#a37043"));
				if(legalSquares[i]) g.setColor(Color.decode("#9CDCDC"));
				if(lastMove[i]) g.setColor(Color.decode("#EAF077"));
				g.fillRect((int)(i/8) * sqSizeX, iMod8 * sqSizeY, sqSizeX, sqSizeY);
			}
		}
	}
	private Point coordToPoint(String coord) {
		return new Point((int)coord.charAt(0)-97, 7-((int)coord.charAt(1)-49));
	}
}
