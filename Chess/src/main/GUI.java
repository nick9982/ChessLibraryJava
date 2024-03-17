package main;

import javax.swing.JFrame;
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
		this.b = b;
		setSize(512, 535);
		setResizable(false);
		setTitle("Chess");
		//JPanel mainPanel = new JPanel();
		cb = new ChessBoard();
		//mainPanel.add(cb);
		add(cb);
		setVisible(true);
	}
	
	private class PiecePanel extends JPanel{
		private int x, y, width, height;
		private int newX = 0, newY = 0;
		private ChessPiece pc;
		private int offsetX = 0, offsetY = 0;
		
		public PiecePanel(int x_, int y_, int width, int height, ChessPiece piece) {
			this.x = x_;
			this.y = y_;
			this.width = width;
			this.height = height;
			this.pc = piece;
			
			//System.out.println("x: " + x + ", y: " + y);
			setSize(width, height);
			setBounds(x, y, width, height);
			setBackground(new Color(0, 0, 0, 0));
			
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					if(turn == 0 && pc.getType() > 5) return;
					else if(turn == 1 && pc.getType() < 6) return;
					super.mousePressed(e);
					offsetX = e.getX();
					offsetY = e.getY();
				}
				public void mouseReleased(MouseEvent e) {
					if(turn == 0 && pc.getType() > 5) return;
					else if(turn == 1 && pc.getType() < 6) return;
					
					int xCell = (int) Math.ceil((double)(newX+32) / width);
					int yCell = (int) Math.ceil((double)newY / height);
					String move = (char)(pc.getPosX() + 97) + "" + (char)(8-pc.getPosY() + 48) + "" + (char)(xCell + 96) + "" + (char)(8-yCell + 49);
					Move mv = new Move(move);
					System.out.println(move);
					if(!b.move(mv)) {
						if(b.isFinished()) {
							System.out.println(turn + " has lost. CHeckmate idiot");
						}
						setBounds(x, y, getWidth(), getHeight());
					}
					else {
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
						setBounds(x, y, getWidth(), getHeight());
						if(turn == 0) turn = 1;
						else turn = 0;
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
	
	private class ChessBoard extends JPanel{
		private int x = 500, y = 500;
		private int sqSizeX = (int) Math.floor((double)this.x/8);
		private int sqSizeY = (int) Math.floor((double)this.y/8);
		public ChessBoard() {
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
				piecePanels.add(new PiecePanel(pieces.get(i).x_coord*sqSizeX, pieces.get(i).y_coord*sqSizeY, sqSizeX, sqSizeY, pieces.get(i).piece));
				add(piecePanels.get(i));
			}
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
				g.fillRect((int)(i/8) * sqSizeX, iMod8 * sqSizeY, sqSizeX, sqSizeY);
			}
			
			/*for(int i = 0; i < pieces.size(); i++) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.f));
				g2d.drawImage(pieces.get(i).piece.getImage(), pieces.get(i).x_coord*sqSizeX, pieces.get(i).y_coord*sqSizeY, sqSizeX, sqSizeY, this);
				g2d.dispose();
			}*/
		}
	}
}
