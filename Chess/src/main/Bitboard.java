package main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;
import main.GUI.Point;

public class Bitboard {
	private long []state = new long[12];
	//STATE INDEXES
	// 0 - white pawns
	// 1 - white rooks
	// 2 - white knights
	// 3 - white bishops
	// 4 - white queens
	// 5 - white kings
	// 6 - black pawns
	// 7 - black rooks
	// 8 - black knights
	// 9 - black bishops
	// 10 - black queens
	// 11 - black kings
	
	Stack<Character> changesMade = new Stack<Character>();
	Stack<Character> movesMade = new Stack<Character>();
	private int changesMadeCnt = 0;
	private int movesMadeCnt = 0;
	private char turn = 0; // 0 = turn of white, 1 = turn of black
	private long enPassant = 0L; // Stores the value of potential enPassants
	private long pinnedWhiteR = 0L, pinnedBlackR = 0L, pinnedWhiteB = 0L, pinnedBlackB = 0L, whiteAttackLane = 0L, blackAttackLane = 0L; // Manages king pins and allows pieces to block check lanes
	private boolean checkmate = false; // Is a king in checkmate
	private boolean areThereLegalMoves = false; // Are there any legal moves. Detects checkmate and stalemate
	private boolean wrRookCastle = true, wlRookCastle = true, brRookCastle = true, blRookCastle = true, castleHappen = false, enPassantHappen = false; // Is the game state allowing each type of castle
	long castleFrom = 0L, castleTo = 0L; // stores the castle coordinates for the GUI to update
	private int checkCount = 0; // Stores the number of active checks on the king. If there are more than 1, the king must move and no piece can block
	private boolean inCheck = false; // Is the king in check
	private boolean kingMustMove = false; // Does the king have to move, it is associated with check count being greater than 1
	private long enPassantTake = 0; // Stores the coordinates of an enPassant take so the GUI can update
	private boolean pawnToQueen = false; // Is the move upgrading a pawn to a queen?
	
	// For the console application I have symbols for each piece
	private static final char pieces[] = {'P', 'R', 'N', 'B', 'Q', 'K', 'p', 'r', 'n', 'b', 'q', 'k'};
	
	// Some getters and setters
	public void setCheckmate(boolean cm) {
		this.checkmate = cm;
	}
	
	public boolean getCheckmate() {
		return this.checkmate;
	}
	
	public void setEnPassant(long enPassant) {
		this.enPassant = enPassant;
	}
	
	public char getTurn() {
		return turn;
	}
	
	public void setTurn(char turn) {
		this.turn = turn;
	}
	
	public long getPinnedWhiteR() {
		return pinnedWhiteR;
	}
	
	public void setPinnedWhiteR(long pinnedWhiteR) {
		this.pinnedWhiteR = pinnedWhiteR;
	}
	
	public void setPinnedBlackR(long pinnedBlackR) {
		this.pinnedBlackB = pinnedBlackR;
	}
	
	public long getPinnedBlackR() {
		return this.pinnedBlackR;
	}
	
	public void setPinnedBlackB(long pinnedBlackB) {
		this.pinnedBlackB = pinnedBlackB;
	}
	
	public long getPinnedBlackB() {
		return this.pinnedBlackB;
	}
	
	public void setPinnedWhiteB(long pinnedWhiteB) {
		this.pinnedWhiteB = pinnedWhiteB;
	}
	
	public long getPinnedWhiteB() {
		return this.pinnedWhiteB;
	}
	
	public long getWhiteAttackLane() {
		return this.whiteAttackLane;
	}
	
	public void setWhiteAttackLane(long whiteAttackLane) {
		this.whiteAttackLane = whiteAttackLane;
	}
	
	public long getBlackAttackLane() {
		return this.blackAttackLane;
	}
	
	public void setBlackAttackLane(long blackAttackLane) {
		this.blackAttackLane = blackAttackLane;
	}
	
	public boolean getCheckMate() {
		return this.checkmate;
	}
	
	public void setCheckMate(boolean checkmate) {
		this.checkmate = checkmate;
	}
	
	public boolean getAreThereLegalMoves() {
		return this.areThereLegalMoves;
	}
	
	public void setAreThereLegalMoves(boolean areThereLegalMoves) {
		this.areThereLegalMoves = areThereLegalMoves;
	}
	
	public boolean getWrRookCastle() {
		return this.wrRookCastle;
	}
	
	public void setWrRookCastle(boolean wrRookCastle) {
		this.wrRookCastle = wrRookCastle;
	}
	
	public boolean getBrRookCastle() {
		return this.brRookCastle;
	}
	
	public void setBrRookCastle(boolean brRookCastle) {
		this.brRookCastle = brRookCastle;
	}
	
	public boolean getWlRookCastle() {
		return this.wrRookCastle;
	}
	
	public void setWlRookCastle(boolean wlRookCastle) {
		this.wlRookCastle = wlRookCastle;
	}
	
	public boolean getBlRookCastle() {
		return this.blRookCastle;
	}
	
	public void setBlRookCastle(boolean blRookCastle) {
		this.blRookCastle = blRookCastle;
	}
	
	public int getCheckCount() {
		return this.checkCount;
	}
	
	public void setCheckCount(int checkCount) {
		this.checkCount = checkCount;
	}
	
	public void setInCheck(boolean inCheck) {
		this.inCheck = inCheck;
	}
	
	public boolean getInCheck() {
		return this.inCheck;
	}
	
	public boolean getKingMustMove() {
		return this.kingMustMove;
	}
	
	public void setKingMustMove(boolean kingMustMove) {
		this.kingMustMove = kingMustMove;
	}
	
	// Get n elements off stack into a char array and return that array.
	private char[] stackToCharArr(int length, Stack<Character> stack) {
		char arr[] = new char[length];
		for(int i = arr.length-1; i >= 0; i--) {
			arr[i] = stack.pop();
		}
		
		return arr;
	}
	
	// Convert a char of 0 or not 0 into false or true
	private boolean charToBoolean(char inp) {
		if(inp == 0) {
			return false;
		}
		return true;
	}
	
	// removes n items from the top of stack
	private void popoutNChangesFromStack(int n, Stack<Character> stack) {
		while(n-- > 0) {
			stack.pop();
		}
	}
	
	// rollback the last move after it was deemed illegal. This will reverse the game state to what it was before the move by popping n changes off the stack.
	// the number of changes made is stored
	private void rollback() {
		popoutNChangesFromStack(changesMadeCnt, changesMade);
		popoutNChangesFromStack(movesMadeCnt, movesMade);
	}
	
	// Undo the last move. This is used in the negamax algorithm to develop a large game tree without using up very much heap space
	// The changes made during last move are encoded into a stack and popped and reassigned to their initial value in this function
	public void Undo() {
		while(!movesMade.empty() && movesMade.peek() != 200) {
			char idx = movesMade.pop();
			long old_val = Number.decodeLong(stackToCharArr(4, movesMade));
			this.state[idx] = old_val;
		}
		if(!movesMade.empty()) movesMade.pop();
		if(turn == 0) turn = 1;
		else turn = 0;
		while(!changesMade.empty() && changesMade.peek() != 200) {
			char code = changesMade.pop();
			switch(code) {
			case 22:
				pawnToQueen = charToBoolean(changesMade.pop());
				break;
			case 21:
				enPassantTake = Number.decodeLong(stackToCharArr(4, changesMade));
				break;
			case 20:
				kingMustMove = charToBoolean(changesMade.pop());
				break;
			case 19:
				inCheck = charToBoolean(changesMade.pop());
				break;
			case 18:
				checkCount = Number.decodeInteger(stackToCharArr(2, changesMade));
				break;
			case 17:
				castleTo = Number.decodeLong(stackToCharArr(4, changesMade));
				break;
			case 16:
				castleFrom = Number.decodeLong(stackToCharArr(4, changesMade));
				break;
			case 15:
				enPassant = Number.decodeLong(stackToCharArr(4, changesMade));
				break;
			case 12:
				blRookCastle = charToBoolean(changesMade.pop());
				break;
			case 11:
				brRookCastle = charToBoolean(changesMade.pop());
				break;
			case 10:
				wlRookCastle = charToBoolean(changesMade.pop());
				break;
			case 9:
				wrRookCastle = charToBoolean(changesMade.pop());
				break;
			case 8:
				areThereLegalMoves = charToBoolean(changesMade.pop());
				break;
			case 7:
				checkmate = charToBoolean(changesMade.pop());
				break;
			case 6:
				blackAttackLane = Number.decodeLong(stackToCharArr(4, changesMade));
				break;
			case 5:
				whiteAttackLane = Number.decodeLong(stackToCharArr(4, changesMade));
				break;
			case 4:
				pinnedWhiteR = Number.decodeLong(stackToCharArr(4, changesMade));
				break;
			case 3:
				pinnedWhiteB = Number.decodeLong(stackToCharArr(4, changesMade));
				break;
			case 2:
				pinnedBlackR = Number.decodeLong(stackToCharArr(4, changesMade));
				break;
			case 1:
				pinnedBlackB = Number.decodeLong(stackToCharArr(4, changesMade));
				break;
			}
		}
		if(!changesMade.empty()) changesMade.pop();
	}
	
	// This is to duplicate the bitboard object
	public Bitboard Duplicate() {
		Bitboard b = new Bitboard();
		b.setTurn(turn);
		b.setPinnedWhiteR(pinnedWhiteR);
		b.setPinnedWhiteB(pinnedWhiteB);
		b.setPinnedBlackR(pinnedBlackR);
		b.setPinnedBlackB(pinnedBlackB);
		b.setEnPassant(enPassant);
		b.setWhiteAttackLane(whiteAttackLane);
		b.setBlackAttackLane(blackAttackLane);
		b.setCheckMate(checkmate);
		b.setAreThereLegalMoves(areThereLegalMoves);
		b.setWrRookCastle(wrRookCastle);
		b.setBrRookCastle(brRookCastle);
		b.setWlRookCastle(wlRookCastle);
		b.setBlRookCastle(blRookCastle);
		b.setCheckCount(checkCount);
		b.setInCheck(inCheck);
		b.setEnPassantTake(enPassantTake);
		b.setKingMustMove(kingMustMove);
		b.setState(state);
		b.setCastleFrom(castleFrom);
		b.setCastleTo(castleTo);
		b.setInCheck(inCheck);
		b.setKingMustMove(kingMustMove);
		return b;
	}
	
	private void setCastleFrom(long castleFrom) {
		this.castleFrom = castleFrom;
	}
	
	private void setCastleTo(long castleTo) {
		this.castleTo = castleTo;
	}

	public void setEnPassantTake(long l) {
		this.enPassantTake = l;
	}
	
	// This is to set the state of the current bitboard to the state of a parameter bitboard b.
	public void set(Bitboard b) {
		this.setTurn(b.getTurn());
		this.setPinnedWhiteR(b.getPinnedWhiteR());
		this.setPinnedWhiteB(b.getPinnedWhiteB());
		this.setPinnedBlackR(b.getPinnedBlackR());
		this.setPinnedBlackB(b.getPinnedBlackB());
		this.setEnPassant(b.getenPassant());
		this.setWhiteAttackLane(b.getWhiteAttackLane());
		this.setBlackAttackLane(b.getBlackAttackLane());
		this.setCheckMate(b.getCheckMate());
		this.setAreThereLegalMoves(b.getAreThereLegalMoves());
		this.setWrRookCastle(b.getWrRookCastle());
		this.setBrRookCastle(b.getBrRookCastle());
		this.setWlRookCastle(b.getWlRookCastle());
		this.setBlRookCastle(b.getBlRookCastle());
		this.setCheckCount(b.getCheckCount());
		this.setInCheck(b.getInCheck());
		this.setKingMustMove(b.getKingMustMove());
		this.setState(b.getState());
		this.setEnPassantTake(b.getEnPassantTake());
		this.setCheckmate(b.getCheckmate());
		this.setCastleFrom(b.getCastleFrom());
		this.setCastleTo(b.getCastleTo());
		this.setInCheck(b.getInCheck());
		this.setKingMustMove(b.getKingMustMove());
		this.setState(b.state);
	}
	
	public long getenPassant() {
		return this.enPassant;
	}

	// Generates all children states of current state for the negamax algorithm
	public ArrayList<String> generateChildren(char color){
		// checks to see if king must move next
		boolean mustKingMove = false;
		if(checkCount > 1) mustKingMove = true;
		
		// stores the different attack angles on king. Blocking paths
		long chckAttcks = 0L;
		int start = 0;
		int end = 6;
		if(color == 0) {
			chckAttcks = blackAttackLane;
		}
		else {
			chckAttcks = whiteAttackLane;
			start = 6;
			end = 12;
		}
		
		// idx the long array of the current state searching for all children of the player whose turn it is
		long position = 1L;
		ArrayList<String> legalMoves = new ArrayList<String>();
		for(int i = 0; i < 64; i++) {
			// Prepare to store state changes and to be ready to rollback
			setHasBeenSetToFalse();
			changesMadeCnt = 0; // These counts store the number of changes since last turn or last rollback.
			movesMadeCnt = 0;
			for(int j = start; j < end; j++) {
				if((position & this.state[j]) != 0) {
					// if a piece is pinned by a bishop it can only move diagonally
					// if a piece is pinned by a rook it can only move vertically and horizontally
					boolean bPin = false;
					boolean rPin = false;
					if(color == 0) {
						bPin = (pinnedWhiteB & position) != 0;
						rPin = (pinnedWhiteR & position) != 0;
					}
					else {
						bPin = (pinnedBlackB & position) != 0;
						rPin = (pinnedBlackR & position) != 0;
					}
					switch(j) {
					case 0:
					case 6:
						//Generate pawn moves
						if(mustKingMove) continue;
						
						// For each of the below move generation calls, This function turns the long returned by the legal move command and encodes it into a list of moves. Then this list of moves is added to the list of all moves generated.
						legalMoves.addAll(numberToLegalMoveList(position, PawnMove(position, bPin, rPin, color, false) & ~position));
						break;
					case 1:
					case 7:
						//Generate rook moves
						if(bPin || mustKingMove) continue;
						legalMoves.addAll(numberToLegalMoveList(position, NaiveRookMove(position, rPin, chckAttcks, color, false) & ~position));
						break;
					case 2:
					case 8:
						//Generate knight moves
						if(bPin || rPin || mustKingMove) continue;
						legalMoves.addAll(numberToLegalMoveList(position, KnightMove(position, chckAttcks, color, false) & ~position));
						break;
					case 3:
					case 9:
						//Generate bishop moves
						if(rPin || mustKingMove) continue;
						legalMoves.addAll(numberToLegalMoveList(position, BishopMove(position, bPin, chckAttcks, color, false) & ~position));
						
						for(int x = 0; x < legalMoves.size(); x++) {
							if(legalMoves.get(x).substring(2,4).equals("h2")) {
								legalMoves.remove(x);
								x--;
							}
						}
						break;
					case 4:
					case 10:
						//Generate Rook moves
						if(mustKingMove) continue;
						if(rPin) legalMoves.addAll(numberToLegalMoveList(position, NaiveRookMove(position, rPin, chckAttcks, color, false) & ~position));
						else if(bPin) legalMoves.addAll(numberToLegalMoveList(position, BishopMove(position, bPin, chckAttcks, color, false) & ~position));
						else legalMoves.addAll(numberToLegalMoveList(position, ((NaiveRookMove(position, rPin, chckAttcks, color, false) | BishopMove(position, bPin, chckAttcks, color, false)) & ~position)));
						
						// There is a bug where the queen likes to go h2 and sacrafice itself. It is the only logical bug i have had. Ran out of time, so I have disabled the bot from being allowed to move the queen here.
						for(int x = 0; x < legalMoves.size(); x++) {
							if(legalMoves.get(x).substring(2,4).equals("h2")) {
								legalMoves.remove(x);
								x--;
							}
						}
						break;
					case 5:
					case 11:
						//Generate king moves
						legalMoves.addAll(numberToLegalMoveList(position, KingMove(position, turn, true, false) & ~position));
					}
				}
			}
			//Rollback after generating moves. The reason for this is that some move generation algorithms are not immutable.
			rollback();
			// shift the bitmask over 1-bit to search for the next piece.
			position <<= 1L;
		}
		
		// returns a string list of all legal moves
		return legalMoves;
	}
	
	// Turns the legal move list encoded in a long into a list of strings
	private ArrayList<String> numberToLegalMoveList(long source, long result){
		
		// Uses log base 2 to get the source of the move. The source is going to be 1 set bit located somwhere in the long
		int position = (int)(Math.log(source) / Math.log(2));
		int x = position % 8;
		int y = 7-(int)(position / 8);
		char p1 = (char)(x+97);
		char p2 = (char)(y+49);
		
		// Able to create 2 coordinates based on the position from earlier
		String src = p1 + "" + p2;
		
		// Create all of the destination coordinates and encode each one with the source of the move and append it to the list.
		ArrayList<String> moves = new ArrayList<String>();
		long idx = 1L;
		for(int i = 0; i < 64; i++) {
			if((idx & result) != 0) {
				moves.add(src + "" + (char)((i % 8)+97) + "" + (char)((7 - (int)(i / 8))+49));
			}
			idx <<= 1L;
		}
		
		
		return moves;
	}
	
	public Bitboard() {
		state[0] = 71776119061217280L;
		state[1] = -9151314442816847872L;
		state[2] = 4755801206503243776L;
		state[3] = 2594073385365405696L;
		state[4] = 576460752303423488L;
		state[5] = 1152921504606846976L;
		state[6] = 65280L;
		state[7] = 129L;
		state[8] = 66L;
		state[9] = 36L;
		state[10] = 8L;
		state[11] = 16L;
		inCheck();
	}
	
	public boolean setState(long[] state) {
		if(state.length != 12) return false;
		for(int i = 0; i < this.state.length; i++) this.state[i] = state[i];
		inCheck();
		if(!validatePiecePositionsUnique()) return false;
		return true;
	}
	
	public long[] getState() {
		return this.state;
	}
	
	public boolean isFinished() {
		return this.checkmate;
	}
	
	// Returns a bitmap of all piece positions on the board
	private long allPiecePositions() {
		long result = 0L;
		for(int i = 0; i < state.length; i++) {
			result |= state[i];
		}
		return result;
	}
	
	// Prints out the state. This is useful for debugging as I can wire the state into the bitboard fairly easily.
	public void printState() {
		System.out.println();
		for(int i = 0; i < state.length; i++) {
			System.out.println("state[" + i + "] = " + state[i] + "L;");
		}
	}
	
	// Checks if the king is in check
	private boolean inCheck() {
		// Get all black piece positions
		long blackPieces = this.blackPieces();
		// Get all white piece positions
		long whitePieces = this.whitePieces();
		
		//Record the change made to checkCount
		setChange((char)18, (int)checkCount);
		changesMadeCnt += 3;
		checkCount = 0;
		// If it is white's move
		if(turn == 0) {
			// Resetting the attack lanes so they can be rediscovered
			setChange((char)6, (long)blackAttackLane);
			blackAttackLane = 0L;
			setChange((char)4, (long)pinnedWhiteR);
			pinnedWhiteR = 0L;
			setChange((char)3, (long)pinnedWhiteB);
			changesMadeCnt += 15;
			pinnedWhiteB = 0L;
			long test_pos = this.state[5];
			long line = 0L;
			char whiteCnt = 0;
			// In the following while loops I am checking vertical, horizontal, and on all diagonals scanning for opponent sliding pieces
			// I need to look through white pieces to see if any white pieces are pinned. I do not know the direction that each loop is searching
			// -72057594037927936L represents the edge of the board in that direction. I will stop the search once I meet the edge of the board
			while((test_pos & -72057594037927936L) == 0)
			{
				test_pos <<= 8;
				line |= test_pos;
				if((test_pos & whitePieces) != 0 && ++whiteCnt == 2) break;
				if((test_pos & blackPieces) != 0) {
					if((test_pos & this.state[7]) != 0 || (test_pos & this.state[10]) != 0) {
						pinnedWhiteR |= line;
						if((line & whitePieces) == 0) {
							blackAttackLane |= line;
							checkCount++;
						}
					}
					break;
					
				}
			}
			
			whiteCnt = 0;
			line = 0L;
			test_pos = this.state[5];
			while((test_pos & 255) == 0L)
			{
				test_pos >>>= 8;
				line |= test_pos;
				if((test_pos & whitePieces) != 0 && ++whiteCnt == 2) break;
				if((test_pos & blackPieces) != 0) {
					if((test_pos & this.state[7]) != 0 || (test_pos & this.state[10]) != 0) {
						pinnedWhiteR |= line;
						if((line & whitePieces) == 0)
						{
							blackAttackLane |= line;
							checkCount++;
						}
					}
					break;
				}
			}
			
			whiteCnt = 0;
			line = 0L;
			test_pos = this.state[5];
			while((test_pos & -9187201950435737472L) == 0L)
			{
				test_pos <<= 1;
				line |= test_pos;
				if((test_pos & whitePieces) != 0 && ++whiteCnt == 2) break;
				if((test_pos & blackPieces) != 0) {
					if((test_pos & this.state[7]) != 0 || (test_pos & this.state[10]) != 0) {
						pinnedWhiteR |= line;
						if((line & whitePieces) == 0)
						{
							blackAttackLane |= line;
							checkCount++;
						}
					}
					break;
				}
			}
			
			whiteCnt = 0;
			line = 0L;
			test_pos = this.state[5];
			while((test_pos & 72340172838076673L) == 0L)
			{
				test_pos >>>= 1;
				line |= test_pos;
				if((test_pos & whitePieces) != 0 && ++whiteCnt == 2) break;
				if((test_pos & blackPieces) != 0) {
					if((test_pos & this.state[7]) != 0 || (test_pos & this.state[10]) != 0) {
						pinnedWhiteR |= line;
						if((line & whitePieces) == 0)
						{
							blackAttackLane |= line;
							checkCount++;
						}
					}
					break;
				}
			}

			whiteCnt = 0;
			line = 0L;
			test_pos = this.state[5];
			while ((test_pos & (255L | 72340172838076673L)) == 0)
			{
				test_pos >>>= 9;
				line |= test_pos;
				if((test_pos & whitePieces) != 0 && ++whiteCnt == 2) break;
				if((test_pos & blackPieces) != 0) {
					if((test_pos & this.state[9]) != 0 || (test_pos & this.state[10]) != 0) {
						pinnedWhiteB |= line;
						if((line & whitePieces) == 0)
						{
							blackAttackLane |= line;
							checkCount++;
						}
					}
					break;
				}
			}
			
			whiteCnt = 0;
			line = 0L;
			test_pos = this.state[5];
			while((test_pos & (-72057594037927936L | -9187201950435737472L)) == 0)
			{
				test_pos <<= 9;
				line |= test_pos;
				if((test_pos & whitePieces) != 0 && ++whiteCnt == 2) break;
				if((test_pos & blackPieces) != 0) {
					if((test_pos & this.state[9]) != 0 || (test_pos & this.state[10]) != 0) {
						pinnedWhiteB |= line;
						if((line & whitePieces) == 0) {
							blackAttackLane |= line;
							checkCount++;
						}
					}
					break;
				}
			}
			
			whiteCnt = 0;
			line = 0L;
			test_pos = this.state[5];
			while((test_pos & (-9187201950435737472L | 255L)) == 0)
			{
				test_pos >>>= 7;
				line |= test_pos;
				if((test_pos & whitePieces) != 0 && ++whiteCnt == 2) break;
				if((test_pos & blackPieces) != 0) {
					if((test_pos & this.state[9]) != 0 || (test_pos & this.state[10]) != 0) {
						pinnedWhiteB |= line;
						if((line & whitePieces) == 0) {
							blackAttackLane |= line;
							checkCount++;
						}
					}
					break;
				}
			}
			
			whiteCnt = 0;
			line = 0L;
			test_pos = this.state[5];
			while((test_pos & (-72057594037927936L | 72340172838076673L)) == 0)
			{
				test_pos <<= 7;
				line |= test_pos;
				if((test_pos & whitePieces) != 0 && ++whiteCnt == 2) break;
				if((test_pos & blackPieces) != 0) {
					if((test_pos & this.state[9]) != 0 || (test_pos & this.state[10]) != 0) {
						pinnedWhiteB |= line;
						if((line & whitePieces) == 0) {
							blackAttackLane |= line;
							checkCount++;
						}
					}
					break;
				}
			}
			long KnightChecks = KnightMove(this.state[5], 0L, (char)2, false) & this.state[8];
			
			// The attack lane for the opponenet includes knights as to allow the opponent to capture a knight that is putting the king in check
			if(KnightChecks != 0)
			{
				blackAttackLane |= KnightChecks;
				return true;
			}
			
			long pawnChecks = (this.state[5] >>> 9 | this.state[5] >>> 7) & this.state[6]; 
			if(pawnChecks != 0) {
				blackAttackLane |= pawnChecks;
				return true;
			}
		// Otherwise it is blacks turn
		} else {
			setChange((char)5, (long)whiteAttackLane);
			whiteAttackLane = 0L;
			setChange((char)2, (long)pinnedBlackR);
			pinnedBlackR = 0L;
			setChange((char)1, (long)pinnedBlackB);
			changesMadeCnt += 15;
			pinnedBlackB = 0L;
			long test_pos = this.state[11];
			long line = 0L;
			char blackCnt = 0;
			//go up first
			// In the following while loops I am checking vertical, horizontal, and on all diagonals scanning for opponent sliding pieces
			// I need to look through black pieces to see if any white pieces are pinned. I do not know the direction that each loop is searching
			// -72057594037927936L represents the edge of the board in that direction. I will stop the search once I meet the edge of the board
			while((test_pos & -72057594037927936L) == 0)
			{
				test_pos <<= 8;
				line |= test_pos;
				if((test_pos & blackPieces) != 0 && ++blackCnt == 2) break;
				if((test_pos & whitePieces) != 0) {
					if((test_pos & this.state[1]) != 0 || (test_pos & this.state[4]) != 0) {
						pinnedBlackR |= line;
						if((line & blackPieces) == 0)
						{
							whiteAttackLane |= line;
							checkCount++;
						}
					}
					break;
				}
			}
			
			blackCnt = 0;
			line = 0L;
			test_pos = this.state[11];
			while((test_pos & 255) == 0L)
			{
				test_pos >>>= 8;
				line |= test_pos;
				if((test_pos & blackPieces) != 0 && ++blackCnt == 2) break;
				if((test_pos & whitePieces) != 0) {
					if((test_pos & this.state[1]) != 0 || (test_pos & this.state[4]) != 0) {
						pinnedBlackR |= line;
						if((line & blackPieces) == 0)
						{
							whiteAttackLane |= line;
							checkCount++;
						}
					}
					break;
				}
			}

			blackCnt = 0;
			line = 0L;
			test_pos = this.state[11];
			while((test_pos & -9187201950435737472L) == 0L)
			{
				test_pos <<= 1;
				line |= test_pos;
				if((test_pos & blackPieces) != 0 && ++blackCnt == 2) break;
				if((test_pos & whitePieces) != 0) {
					if((test_pos & this.state[1]) != 0 || (test_pos & this.state[4]) != 0) {
						pinnedBlackR |= line;
						if((line & blackPieces) == 0)
						{
							whiteAttackLane |= line;
							checkCount++;
						}
					}
					break;
				}
			}

			blackCnt = 0;
			line = 0L;
			test_pos = this.state[11];
			while((test_pos & 72340172838076673L) == 0L)
			{
				test_pos >>>= 1;
				line |= test_pos;
				if((test_pos & blackPieces) != 0 && ++blackCnt == 2) break;
				if((test_pos & whitePieces) != 0) {
					if((test_pos & this.state[1]) != 0 || (test_pos & this.state[4]) != 0) {
						pinnedBlackR |= line;
						if((line & blackPieces) == 0)
						{
							whiteAttackLane |= line;
							checkCount++;
						}
					}
					break;
				}
			}

			blackCnt = 0;
			line = 0L;
			test_pos = this.state[11];
			while ((test_pos & (255L | 72340172838076673L)) == 0)
			{
				test_pos >>>= 9;
				line |= test_pos;
				if((test_pos & blackPieces) != 0 && ++blackCnt == 2) break;
				if((test_pos & whitePieces) != 0) {
					if((test_pos & this.state[3]) != 0 || (test_pos & this.state[4]) != 0) {
						pinnedBlackB |= line;
						if((line & blackPieces) == 0)
						{
							whiteAttackLane |= line;
							checkCount++;
						}
					}
					break;
				}
			}

			blackCnt = 0;
			line = 0L;
			test_pos = this.state[11];
			while((test_pos & (-72057594037927936L | -9187201950435737472L)) == 0)
			{
				test_pos <<= 9;
				line |= test_pos;
				if((test_pos & blackPieces) != 0 && ++blackCnt == 2) break;
				if((test_pos & whitePieces) != 0) {
					if((test_pos & this.state[3]) != 0 || (test_pos & this.state[4]) != 0) {
						pinnedBlackB |= line;
						if((line & blackPieces) == 0)
						{
							whiteAttackLane |= line;
							checkCount++;
						}
					}
					break;
				}
			}

			blackCnt = 0;
			line = 0L;
			test_pos = this.state[11];
			while((test_pos & (-9187201950435737472L | 255L)) == 0)
			{
				test_pos >>>= 7;
				line |= test_pos;
				if((test_pos & blackPieces) != 0 && ++blackCnt == 2) break;
				if((test_pos & whitePieces) != 0) {
					if((test_pos & this.state[3]) != 0 || (test_pos & this.state[4]) != 0) {
						pinnedBlackB |= line;
						if((line & blackPieces) == 0)
						{
							whiteAttackLane |= line;
							checkCount++;
						}
					}
					break;
				}
			}

			blackCnt = 0;
			line = 0L;
			test_pos = this.state[11];
			while((test_pos & (-72057594037927936L | 72340172838076673L)) == 0)
			{
				test_pos <<= 7;
				line |= test_pos;
				if((test_pos & blackPieces) != 0 && ++blackCnt == 2) break;
				if((test_pos & whitePieces) != 0) {
					if((test_pos & this.state[3]) != 0 || (test_pos & this.state[4]) != 0) {
						pinnedBlackB |= line;
						if((line & blackPieces) == 0)
						{
							whiteAttackLane |= line;
							checkCount++;
						}
					}
					break;
				}
			}

			// The attack lane for the opponenet includes knights as to allow the opponent to capture a knight that is putting the king in check
			long knightMoves = KnightMove(this.state[11], 0L, (char)2, false) & this.state[2];
			if((knightMoves & this.state[2]) != 0) {
				whiteAttackLane |= knightMoves;
				return true;
			}
			
			long pawnMoves = (this.state[11] << 9 | this.state[11] << 7) & this.state[0];
			if(pawnMoves != 0) {
				whiteAttackLane |= pawnMoves;
				return true;
			}
		}
		if(checkCount > 0)
			return true;
		return false;
	}
	
	// return all white pieces
	private long whitePieces() {
		long result = 0L;
		for(int i = 0; i < 6; i++) {
			result |= state[i];
		}
		return result;
	}
	
	// return all black pieces
	private long blackPieces() {
		long result = 0L;
		for(int i = 6; i < 12; i++) {
			result |= state[i];
		}
		return result;
	}
	
	// This generates the legal moves for a rook
	public long NaiveRookMove(long position, boolean rPin, long checkAttacks, char color, boolean forKing) {
		long test_pos = position;
		long result = 0L;
		long allPieces = this.allPiecePositions();
		if(forKing) {
			if(color == 0) allPieces &= ~this.state[11];
			else allPieces &= ~this.state[5];
		}

		// These loops scan in horizontal and vertical directions until finding an edge or a piece blocking its path
		while((test_pos & -72057594037927936L) == 0)
		{
			test_pos <<= 8;
			result |= test_pos;
			if((test_pos & allPieces) != 0L) {
				break;
			}
		}
		test_pos = position;
		while((test_pos & 255) == 0L)
		{
			test_pos >>>= 8;
			result |= test_pos;
			if((test_pos & allPieces) != 0L) {
				break;
			}
		}
		
		test_pos = position;
		while((test_pos & -9187201950435737472L) == 0L)
		{
			test_pos <<= 1;
			result |= test_pos;
			if((test_pos & allPieces) != 0L) {
				break;
			}
			
		}
		
		test_pos = position;
		while((test_pos & 72340172838076673L) == 0L)
		{
			test_pos >>>= 1;
			result |= test_pos;
			if((test_pos & allPieces) != 0L) {
				break;
			}
		}
		
		// If there is a rook pinned by king attacker, the rook's destination must be in the same file or row as the pin.
		if(color == 0) {
			if(rPin) {
				result &= pinnedWhiteR;
			}
			if(!forKing)result &= ~whitePieces();
		}
		else {
			if(rPin) {
				result &= pinnedBlackR;
			}
			if(!forKing)result &= ~blackPieces();
		}
		
		// If there is a check attack but no pin, if the rook moves it can only be to capture the attacking piece.
		if(checkAttacks != 0L) {
			result &= checkAttacks;
		}
		return result;
	}
	
	// Calculates all pawn moves
	public long PawnMove(long position, boolean bPin, boolean rPin, char color, boolean forKing) {
		long moves = 0L;
		// Stores all white and black pieces
		long whitePieces = whitePieces(), blackPieces = blackPieces();
		// If it is white's turn
		if(color == 0) {
			// If enPassant is set. So there is an opportunity to en passant
			if(enPassant != 0L) {
				// Checks current pawn location relative to en passant to see if it is able to jump it.
				// Checks left side
				if((position >>> 1) == enPassant && (position & 72340172838076673L) == 0) {
					setChange((char)21, (long)enPassantTake);
					changesMadeCnt += 5;
					enPassantTake = enPassant;
					enPassant = position >>> 9;
					moves |= position >>> 9;
				}
				//Checks right side
				else if(position << 1 == enPassant && (position & -9187201950435737472L) == 0) {
					setChange((char)21, (long)enPassantTake);
					changesMadeCnt += 5;
					enPassantTake = enPassant;
					enPassant = position >>> 7;
					moves |= position >>> 7;
				}
				else {
					// If there is no enPassant opportunity, then we reset the value for the current state.
					enPassant = 0;
				}
			}
			// Pawns can't move forward into enemy or friendly pieces
			if(!forKing) {
				moves |= (position >>> 8) & ~(whitePieces | blackPieces);
				if(moves != 0 && (position & 71776119061217280L) != 0) {
					moves |= position >>> 16 & ~(blackPieces | whitePieces);
				}
			}
			
			// can jump a piece with a pawn
			if((position & 72340172838076673L) == 0) {
				moves |= position >>> 9 & blackPieces;
				if(forKing) moves |= position >>> 9;
			}
			if((position & -9187201950435737472L) == 0) {
				moves |= position >>> 7 & blackPieces;
				if(forKing) moves |= position >>> 7;
			}
			
			// ensures that if the pawn is pinned it only can move in that direction
			if(rPin) moves &= pinnedWhiteR;
			else if(bPin) moves &= pinnedWhiteB;
			if(blackAttackLane != 0L) moves &= blackAttackLane;
			if(!forKing) moves &= ~whitePieces();
		}
		// Otherwise it is black's turn
		else {
			if(enPassant != 0L) {
				// The following code block works the same as the code block above. If you would like information on it, then refer to the above code block.
				if((position >>> 1) == enPassant && (position & 72340172838076673L) == 0) {
					setChange((char)21, (long)enPassantTake);
					changesMadeCnt += 5;
					enPassantTake = enPassant;
					enPassant = position << 7;
					moves |= position << 7;
				}
				else if(position << 1 == enPassant && (position & -9187201950435737472L) == 0) {
					setChange((char)21, (long)enPassantTake);
					changesMadeCnt += 5;
					enPassantTake = enPassant;
					enPassant = position << 9;
					moves |= position << 9;
				}
				else {
					enPassant = 0;
				}
			}
			if(!forKing) {
				moves |= (position << 8) & ~(blackPieces | whitePieces);
				if(moves != 0 && (position & 65280L) != 0) {
					moves |= position << 16 & ~(blackPieces | whitePieces);
				}
			}
			if((position & 72340172838076673L) == 0) {
				moves |= position << 7 & whitePieces;
				if(forKing) moves |= position << 7;
			}
			if((position & -9187201950435737472L) == 0) {
				moves |= position << 9 & whitePieces;
				if(forKing) moves |= position << 9;
			}
			if(rPin) moves &= pinnedBlackR;
			else if(bPin) moves &= pinnedBlackB;
			if(whiteAttackLane != 0L) moves &= whiteAttackLane;
			if(!forKing) moves &= ~blackPieces();
		}
		return moves;
	}
	
	// Knight moves
	public long KnightMove(long position, long checkAttacks, char color, boolean forKing) {
		// E1 to E4 and W1 to W4 are 8 different longs that facilitate the offset of knight legal moves from the input knight position
		long E1 = position >>> 17, E2 = position >>> 10, E3 = position << 6, E4 = position << 15;
		long W1 = E1 << 2, W2 = E2 << 4, W3 = E3 << 4, W4 = E4 << 2;
		long result = 0L;
		result = E1 | E2 | E3 | E4 | W1 | W2 | W3 | W4;
		
		// For the following code, I am ensuring that the knight is located in the regions of the board where certain knight moves are allowed before returning them as legal
		if((position & 4629771061636907072L) != 0) {
			result = result & ~(W2 | W3);
		}
		if((position & -9187201950435737472L) != 0) {
			result = result & ~(W1 | W2 | W3 | W4);
		}
		if((position & 144680345676153346L) != 0) {
			result = result & ~(E2 | E3);
		}
		if((position & 72340172838076673L) != 0) {
			result = result & ~(E1 | E2 | E3 | E4);
		}
		if((position & 255) != 0) {
			result = result & ~(E1 | E2 | W1 | W2);
		}
		if((position & -72057594037927936L) != 0) {
			result = result & ~(E3 | E4 | W3 | W4);
		}
		if((position & 71776119061217280L) != 0) {
			result = result & ~(E4 | W4);
		}
		if((position & 65280L) != 0) {
			result = result & ~(E1 | W1);
		}
			
		// If we are not calculating the king's legal moves, friendly pieces will block the knight from being able to move to a destination
		if(!forKing) {
			if(color == 0)
				result &= ~whitePieces();
			else if(color == 1)
				result &= ~blackPieces();
		}
		
		// If there is a check on the king, the knight move must be used to capture the attacking piece
		if(checkAttacks != 0L) result &= checkAttacks;
		
		return result;
	}
	
	
	// Calculate bishop moves
	public long BishopMove(long position, boolean bPin, long chckAttacks, char color, boolean forKing) {
		long test_pos = position;
		long result = 0L;
		long allPieces = this.allPiecePositions();
		// If calculating move for king, don't stop scanning at king. This way the king can't just move further away in the attack lane where it is still in check
		if(forKing) {
			
			if(color == 0) allPieces &= ~this.state[11];
			else allPieces &= ~this.state[5];
		}
		
		// Checks all diagonals from current position
		while ((test_pos & (255L | 72340172838076673L)) == 0)
		{
			test_pos >>>= 9;
			result |= test_pos;
			if((test_pos & allPieces) != 0) break;
		}
		
		test_pos = position;
		while((test_pos & (-72057594037927936L | -9187201950435737472L)) == 0)
		{
			test_pos <<= 9;
			result |= test_pos;
			if((test_pos & allPieces) != 0) break;
		}
		
		test_pos = position;
		while((test_pos & (-9187201950435737472L | 255L)) == 0)
		{
			test_pos >>>= 7;
			result |= test_pos;
			if((test_pos & allPieces) != 0) break;
		}
		
		test_pos = position;
		while((test_pos & (-72057594037927936L | 72340172838076673L)) == 0)
		{
			test_pos <<= 7;
			result |= test_pos;
			if((test_pos & allPieces) != 0) break;
		}
		
		// If there is a diagonal pin, the bishop must stay pinned, but is allowed to move
		if(color == 0) {
			if(bPin) {
				result &= pinnedWhiteB;
			}
			if(!forKing) result &= ~whitePieces();
		}
		else {
			if(bPin) {
				result &= pinnedBlackB;
			}
			if(!forKing) result &= ~blackPieces();
		}
		
		// If the king is in check, the bishop move must be capturing the attacking piece
		if(chckAttacks != 0L) result &= chckAttacks;
		return result;
	}
	
	public long KingMove(long position, char color, boolean checkOppMoves, boolean forKing) {
		long result;
		// this mask is created, it represents the kings initial moves before any calculation
		result = position >>> 1 | position << 1 | position >>> 8 | position << 8 | position >>> 9 | position >>> 7 | position << 9 | position << 7;
				
		// checks to see if the king moves overlap with any edges. If so, it removes those moves.
		if((position & -9187201950435737472L) != 0) {
			result = result & ~(position << 1 | position << 9 | position >>> 7);
		}
		if((position & 72340172838076673L) != 0) {
			result = result & ~(position >>> 1 | position >>> 9 | position << 7);
		}
		if((position & 255) != 0) {
			result = result & ~(position >>> 7 | position >>> 9 | position >>> 8);
		}
		if((position & -72057594037927936L) != 0) {
			result = result & ~(position << 9 | position << 8 | position << 7);
		}

		// All black pieces
		long blackPieces = blackPieces();
		// All white pieces
		long whitePieces = whitePieces();
		// If color is white
		if(color == 0) {
			// white king cannot capture white pieces
			if(!forKing) result &= ~whitePieces;
			// white king cannot move where the black pieces can move on their next turn. The reason there is a boolean is to avoid infinite recursion when the king is checking the opponent's king's legal moves.
			if(checkOppMoves) {
				// get all oponent legal moves
				long legalMoves = allLegalMoves((char)1, true, false);
				result &= ~legalMoves;
				// check if castle is legal
				if(wrRookCastle) {
					if(((whitePieces | blackPieces) & 6917529027641081856L) == 0 && (legalMoves & -1152921504606846976L) == 0) {
						result |= 4611686018427387904L;
					}
				}
				// check if castle is legal
				if(wlRookCastle) {
					if(((whitePieces | blackPieces) & 1008806316530991104L) == 0 && (legalMoves & 2233785415175766016L) == 0) {
						result |= 288230376151711744L;
					}
				}
			}
		// OTHERwise the color is black
		} else {
			// Same logic as white's moves above
			if(!forKing) result &= ~blackPieces;
			if(checkOppMoves) {
				long legalMoves = allLegalMoves((char)0, true, false);
				result &= ~legalMoves;
				if(brRookCastle) {
					if(((whitePieces | blackPieces) & 96) == 0 && (legalMoves & 240) == 0) {
						result |= 64;
					}
				}
				if(blRookCastle) {
					if(((whitePieces | blackPieces) & 14) == 0 && (legalMoves & 31) == 0) {
						result |= 4;
					}
				}
			}
		}
		return result;
		
	}
	
	// Bitboard definition if passing in a custom state, useful for debugging
	public Bitboard(long []initialState) throws Exception {
		if(initialState.length != 12) {
			throw new Exception("Invalid board state. The number of piece categories is greater than 12");
		}
		if(!validatePiecePositionsUnique()) {
			throw new Exception("Two different pieces are sharing one spot on the board");
		}
		for(int i = 0; i < this.state.length; i++) {
			this.state[i] = initialState[i];
		}
		inCheck();
	}
	
	// places array in a stack. It is useful for when I am storing a long integer inside of my Stacks of characters
	private void placeArrInStack(char [] arr, Stack<Character> stack) {
		for(int i = 0; i < arr.length; i++) {
			stack.push(arr[i]);
		}
	}
	
	// Returns all legal moves. This is used to validate that a king's move is legal. It basically is looking at the legal moves of whatever color specified at their current/next turn
	private long allLegalMoves(char color, boolean forKing, boolean checkKing) {
		char start = 0, length = 0;
		// check if the king is in check
		inCheck();
		// find if king must move and find attack lanes
		boolean kingMustMove = false;
		long chckAttcks = 0L;
		if(checkCount > 1) kingMustMove = true;
		if(color == 0) {
			chckAttcks = blackAttackLane;
			start = 0;
			length = 6;
		}
		else {
			chckAttcks = whiteAttackLane;
			start = 6;
			length = 12;
		}
		
		long legalMoves = 0L;
		// Use current Position to index the state array
		long currentPosition = 1L;
		for(int i = 0; i < 64; i++) {
			for(int x = start; x < length; x++) {
				if((currentPosition & this.state[x]) != 0) {
					boolean rPin = false, bPin = false;
					// Setting the attack lanes while generating moves
					if(color == 0) {
						if((pinnedWhiteR & currentPosition) != 0) rPin = true;
						if((pinnedWhiteB & currentPosition) != 0) bPin = true;
					}
					else {
						if((pinnedBlackR & currentPosition) != 0) rPin = true;
						if((pinnedBlackB & currentPosition) != 0) bPin = true;
					}
					// Generating all of the moves. I am bitwise OR all moves to a long that represents all moves. The point is that If the KING move & opponent LEGAL moves != 0 then the move is invalid.
					switch(x) {
					case 0:
					case 6:
						if(kingMustMove) continue;
						legalMoves |= PawnMove(currentPosition, bPin, rPin, color, forKing);
						break;
					case 1:
					case 7:
						if(bPin || kingMustMove) continue;
						legalMoves |= NaiveRookMove(currentPosition, rPin, chckAttcks, color, forKing);
						break;
					case 2:
					case 8:
						if(rPin || bPin || kingMustMove) continue;
						legalMoves |= KnightMove(currentPosition, chckAttcks, color, forKing);
						break;
					case 3:
					case 9:
						if(rPin || kingMustMove) continue;
						legalMoves |= BishopMove(currentPosition, bPin, chckAttcks, color, forKing);
						break;
					case 4:
					case 10:
						if(kingMustMove) continue;
						if(rPin) legalMoves |= NaiveRookMove(currentPosition, rPin, chckAttcks, color, forKing);
						else if(bPin) legalMoves |= BishopMove(currentPosition, bPin, chckAttcks, color, forKing);
						else legalMoves |= NaiveRookMove(currentPosition, false, chckAttcks, color, forKing) | BishopMove(currentPosition, false, chckAttcks, color, forKing);
						break;
					case 5:
					case 11:
						legalMoves |= KingMove(currentPosition, color, checkKing, forKing);
						break;
					}
				}
			}
			// Move the index to find next move
			currentPosition <<= 1;
		}
		
		// return bitmap of off limit squares to king
		return legalMoves;
	}
	
	// Used in move function to disable future castling when a rook moves
	private void checkRooks(int i, long val) {
		if(i == 1 && (val & -9223372036854775808L) != 0) {
			wrRookCastle = false;
		}
		else if(i == 1 && (val & 72057594037927936L) != 0) {
			wlRookCastle = false;
		}
		else if(i == 7 && (val & 128) != 0) {
			brRookCastle = false;
		}
		else if(i == 7 && (val & 1) != 0) {
			blRookCastle = false;
		}
	}
	private char booleanToChar(boolean inp) {
		return (char)(inp?1:0);
	}
	
	// This is a list that keeps track of when each state is changed. It pointless to store a value twice in the stack for 1 turn.
	private boolean hasMoveBeenSet[] = new boolean[12];
	private boolean hasChangeBeenSet[] = new boolean[22];
	
	// Reset the above lists
	private void setHasBeenSetToFalse() {
		for(int i = 0; i < 12; i++) {
			hasMoveBeenSet[i] = false;
		}
		
		for(int i = 0; i < 22; i++) {
			hasChangeBeenSet[i] = false;
		}
	}
	
	// Set move information for rollbacks and undo
	private void setMove(char idx) {
		if(hasMoveBeenSet[idx]) return;
		hasMoveBeenSet[idx] = true;
		placeArrInStack(Number.encodeLong(this.state[idx]), movesMade);
		movesMade.push(idx);
	}
	
	// Push an integer to the changes made stack
	private void setChange(char idx, int val) {
		if(hasChangeBeenSet[idx-1]) return;
		hasChangeBeenSet[idx-1] = true;
		placeArrInStack(Number.encodeInteger(val), changesMade);
		changesMade.push(idx);
	}
	
	// Push a long to the changes made stack
	private void setChange(char idx, long val) {
		if(hasChangeBeenSet[idx-1]) return;
		hasChangeBeenSet[idx-1] = true;
		placeArrInStack(Number.encodeLong(val), changesMade);
		changesMade.push(idx);
	}
	
	// Push a boolean to the changes made stack
	private void setChange(char idx, boolean inp) {
		if(hasChangeBeenSet[idx-1]) return;
		hasChangeBeenSet[idx-1] = true;
		changesMade.push(booleanToChar(inp));
		changesMade.push(idx);
	}
	
	// Clear all stacks after each negamax to avoid filling the heap
	public void clearStacks() {
		while(!changesMade.isEmpty()) {
			changesMade.pop();
		}
		
		while(!movesMade.isEmpty()) {
			movesMade.pop();
		}
	}
	
	// Move
	public boolean move(Move move) {
		movesMadeCnt = 0; //preparing to rollback
		changesMadeCnt = 0;
		setHasBeenSetToFalse();
		
		// If the move is invalid return false
		if(move.getBitFrom() == 0) return false;
		// signify the beginning of a move in the changes made stack
		changesMade.push((char)200);
		changesMadeCnt += 1;
		// if checkmate roll back changes
		if(checkmate) {
			rollback();
			return false;
		}
		// set all important game state flags and prepare to check the validity of the move
		int start = 0, length = 0;
		enPassantHappen = false;
		castleHappen = false;
		boolean rPin = false, bPin = false;
		setChange((char)20, kingMustMove);
		setChange((char)15, (long)enPassant);
		changesMadeCnt += 7;
		if(checkCount > 1) kingMustMove = true;
		else kingMustMove = false;
		long chckAttcks = 0L;
		if(this.turn == 0) {
			if((move.getBitFrom() & whitePieces()) == 0) { // if the destination of the move is on a white piece the move is invalid
				rollback();
				return false;
			}
			if((pinnedWhiteR & move.getBitFrom()) != 0) rPin = true;
			else if((pinnedWhiteB & move.getBitFrom()) != 0) bPin = true;
			chckAttcks = blackAttackLane;
			start = 0;
			length = 6;
		}
		else {
			if((move.getBitFrom() & blackPieces()) == 0) { // if the destination of the move is on a black piece the move is invalid
				rollback();
				return false;
			}
			if((pinnedBlackR & move.getBitFrom()) != 0) rPin = true;
			else if((pinnedBlackB & move.getBitFrom()) != 0) bPin = true;
			chckAttcks = whiteAttackLane;
			start = 6;
			length=12;
		}
		
		// Check the legal moves for the piece located at said position on the board
		long legalMoves = 0;
		long position = move.getBitFrom(), destination = move.getBitTo();
		int idx = 0;
		for(int i = start; i < length; i++) {
			if((state[i] & move.getBitFrom()) != 0) {
				switch(i) {
				case 0:
				case 6:
					// If the king must move, the pawn doesn't move
					if(kingMustMove) return false;
					legalMoves = PawnMove(position, bPin, rPin, turn, false);
					idx = i;
					break;
				case 1:
				case 7:
					// If the king must move or the rook is pinned by a bishop or queen there are no legal moves for the rook
					if(bPin || kingMustMove) return false;
					legalMoves = NaiveRookMove(position, rPin, chckAttcks, turn, false);
					idx = i;
					break;
				case 2:
				case 8:
					// If the king must move or the knight is pinned, there are no legal moves for the knight
					if(rPin || bPin || kingMustMove) return false;
					legalMoves = KnightMove(position, chckAttcks, turn, false);
					idx = i;
					break;
				case 3:
				case 9:
					// If the king must move or the bishop is pinned by a rook or queen there ar eno legal moves for the bishop
					if(rPin || kingMustMove) return false;
					legalMoves = BishopMove(position, bPin, chckAttcks, turn, false);
					idx = i;
					break;
				case 4:
				case 10:
					// if the king must move the queen can't move
					if(kingMustMove) return false;
					// if the queen is pinned by a rook, it can only move on the file/row it is pinned on
					if(rPin) legalMoves = NaiveRookMove(position, rPin, chckAttcks, turn, false);
					// if the queen is pinned by a bishop, it can only move on that diagonal
					else if(bPin) legalMoves = BishopMove(position, bPin, chckAttcks, turn, false);
					// if the queen is not pinned its moves are calculated by bitwise OR of bishop moves and rook moves
					else legalMoves = NaiveRookMove(position, false, chckAttcks, turn, false) | BishopMove(position, false, chckAttcks, turn, false);
					idx = i;
					break;
				case 5:
				case 11:
					legalMoves = KingMove(position, turn, true, false);
					idx = i;
				}
			}
		}

		// signify that the moves made stack has just begun for this turn
		movesMade.push((char)200);
		movesMadeCnt += 1;
		// If there are legal moves we can commence.
		if((legalMoves & destination) != 0) {
			// If there is an enPassant
			if(enPassant != 0L) {
				// If you are doing an en passant we need to update the board and tell the GUI to update
				if(enPassant == destination) {
					if(turn == 0) {
						setMove((char) 6);
						movesMadeCnt += 5;
						this.state[6] &= ~enPassantTake;
					}
					else {
						setMove((char) 0);
						movesMadeCnt += 5;
						this.state[0] &= ~enPassantTake;
					}
					enPassantHappen = true;
				}
			}
			enPassant = 0;
			boolean pawnToDiffPiece = false;
			// If you are moving off home row for the pawn there is an enPassant opportunity for the opponent
			if((idx == 0 || idx == 6) && ((move.getBitFrom() >>> 16) == destination || (move.getBitFrom() << 16) == destination)) {
				enPassant = destination;
			}
			// if you are upgrading your pawn to a queen for white
			else if(idx == 0 && (destination & 255) != 0) {
				for(char i = 0; i < state.length; i++) {
					if((state[i] & destination) != 0) {
						checkRooks(i, destination);
						setMove((char)i);
						movesMadeCnt += 5;
						if(i == 11 || i == 5) {
							rollback();
							return false;
						} // can't take king
						this.state[i] &= ~destination;
					}
				}

				setMove((char)0);
				setMove((char)4);
				movesMadeCnt += 10;
				this.state[0] &= ~move.getBitFrom();
				this.state[4] |= destination;
				pawnToDiffPiece = true;
				setChange((char)22, pawnToQueen);
				changesMadeCnt += 2;
				pawnToQueen = true;
			}
			// if you are upgrading your pawn to a queen for black
			else if(idx == 6 && (destination & -72057594037927936L) != 0) {
				for(char i = 0; i < state.length; i++) {
					if((state[i] & destination) != 0) {
						checkRooks(i, destination);
						setMove((char)i);
						movesMadeCnt += 5;
						if(i == 11 || i == 5) {
							rollback();
							return false;
						}
						this.state[i] &= ~destination;
					}
				}
				setMove((char)6);
				setMove((char)10);
				movesMadeCnt += 10;
				this.state[6] &= ~move.getBitFrom();
				this.state[10] |= destination;
				pawnToDiffPiece = true;
				setChange((char)22, pawnToQueen);
				changesMadeCnt += 2;
				pawnToQueen = true;
			}

			// if you can castle
			if(idx == 11 && (brRookCastle == true || blRookCastle == true)) {
				// if the black king moves castling will no longer be an option
				setChange((char)11, brRookCastle);
				brRookCastle = false;
				setChange((char)12, blRookCastle);
				changesMadeCnt += 4;
				blRookCastle = false;
				// is the move castling?
				if(destination == 64) {
					setChange((char)16, (long)castleFrom);
					castleFrom = 128;
					setChange((char)17, (long)castleTo);
					castleTo = 32;
					changesMadeCnt += 10;
					setMove((char)7);
					movesMadeCnt += 5;
					this.state[7] &= 128;
					this.state[7] |= 32;
					castleHappen = true;
				}
				// is the move castling
				else if(destination == 4) {
					setChange((char)16, (long)castleFrom);
					castleFrom = 1;
					setChange((char)17, (long)castleTo);
					castleTo = 8;
					changesMadeCnt += 10;
					setMove((char)7);
					movesMadeCnt += 5;
					this.state[7] &= ~1;
					this.state[7] |= 8;
					castleHappen = true;
				}
			}
			// is white able to castle?
			else if(idx == 5 && (wrRookCastle == true || wlRookCastle == true)) {
				// if the white king moves, castling will no longer be an option
				setChange((char)9, wrRookCastle);
				wrRookCastle = false;
				setChange((char)10, wlRookCastle);
				changesMadeCnt += 4;
				wlRookCastle = false;
				//is the move castling?
				if(destination == 4611686018427387904L) {
					setChange((char)16, (long)castleFrom);
					castleFrom = -9223372036854775808L;
					setChange((char)17, (long)castleTo);
					changesMadeCnt += 10;
					castleTo = 2305843009213693952L;
					setMove((char)1);
					movesMadeCnt += 5;
					this.state[1] &= ~-9223372036854775808L;
					this.state[1] |= 2305843009213693952L;
					castleHappen = true;
				}
				//is the move castling?
				else if(destination == 288230376151711744L) {
					setChange((char)16, (long)castleFrom);
					castleFrom = 72057594037927936L;
					setChange((char)17, (long)castleTo);
					changesMadeCnt += 10;
					castleTo = 576460752303423488L;
					setMove((char)1);
					movesMadeCnt += 5;
					this.state[1] &= ~72057594037927936L;
					this.state[1] |= 576460752303423488L;
					castleHappen = true;
				}
			}
			// if the rook is moving disable castling
			else if(idx == 7 && brRookCastle && (move.getBitFrom() & 255) != 0) {
				setChange((char)11, brRookCastle);
				changesMadeCnt += 2;
				brRookCastle = false;
			}
			else if(idx == 7 && blRookCastle && (move.getBitFrom() & 1) != 0) {
				setChange((char)12, blRookCastle);
				changesMadeCnt += 2;
				blRookCastle = false;
			}
			else if(idx == 1 && wlRookCastle && (move.getBitFrom() & -9223372036854775808L) != 0) {
				setChange((char)10, wlRookCastle);
				changesMadeCnt += 2;
				wlRookCastle = false;
			}
			else if(idx == 1 && wrRookCastle && (move.getBitFrom() & 72057594037927936L) != 0) {
				setChange((char)9, wrRookCastle);
				changesMadeCnt += 2;
				wrRookCastle = false;
			}

			// if the pawn did not turn to queen then this is how you can calculate moves
			if(!pawnToDiffPiece) {
				for(char i = 0; i < state.length; i++) {
					if((state[i] & destination) != 0) {
						if(i == 11 || i == 5) {
							rollback();
							return false;
						}
						checkRooks(i, destination);
						setMove((char)i);
						movesMadeCnt += 5;
						// remove old piece from destination
						this.state[i] &= ~destination;
					}
				}
				setMove((char)idx);
				movesMadeCnt += 5;
				checkRooks(idx, position);
				// place new piece in its target destination and remove it from its old position
				this.state[idx] &= ~position;
				this.state[idx] |= destination;
			}
		}
		else {
			// if move is not legal
			rollback();
			return false;
		}
		
		// the turn is permanent now, so we can change to opponent's turn
		// There is some state checking we need to do
		if(turn == 1) turn = 0;
		else turn = 1;
		
		// check if opponent king is in check
		setChange((char)19, inCheck);
		changesMadeCnt += 2;
		inCheck = inCheck();
		
		// check for checkmate
		if(kingMustMove && !areThereLegalMoves) {
			int kingIdx = (this.turn+1)*6-1;
			if(KingMove(this.state[kingIdx], this.turn, true ,false) == 0L) {
				setChange((char)7, checkmate);
				changesMadeCnt += 2;
				checkmate = true;
			}
			setChange((char)8, areThereLegalMoves);
			changesMadeCnt += 2;
			areThereLegalMoves = true;
		}
		// sets conditions for king safety for the next move
		else if(inCheck && !areThereLegalMoves) {
			//checking if there are any legal moves
			int kingIdx = (this.turn+1)*6-1;
			if((allLegalMoves(this.turn, false, true) | KingMove(this.state[kingIdx], this.turn, true, false)) == 0L) {
				setChange((char)7, checkmate);
				changesMadeCnt += 2;
				checkmate = true;
			}
			setChange((char)8, areThereLegalMoves);
			changesMadeCnt += 2;
			areThereLegalMoves = true;
		}
		else if(!inCheck) {
			setChange((char)8, areThereLegalMoves);
			changesMadeCnt += 2;
			areThereLegalMoves = false;
		}
		// The move was legal return true
		return true;
	}
	
	// after updating the GUI, the GUi calls this function
	public boolean checkCastle() {
		boolean castle = this.castleHappen;
		this.castleHappen = false;
		return castle;
	}
	
	public long getCastleFrom() {
		return this.castleFrom;
	}
	
	public long getCastleTo() {
		return this.castleTo;
	}
	
	// after updating the GUI, the GUI sets en passant happen back to false
	public boolean getEnPassantHappen() {
		boolean enPassantHappen_ = this.enPassantHappen;
		this.enPassantHappen = false;
		return enPassantHappen_;
	}

	// after updating the GUI, the GUI sets pawn to queen back to false
	public boolean getPawnToQueen() {
		boolean ptoq = this.pawnToQueen;
		this.pawnToQueen = false;
		return ptoq;
	}
	
	public long getEnPassantTake() {
		return this.enPassantTake;
	}
	
	public int score() {
		int punish_home_b[] = {
			     -50,  -50,  -50,  -50,  -50,  -50,  -50,  -50,
			     0,  0,  0,  0,  0,  0,  0,  0,
			     0,  0,  0,  0,  0,  0,  0,  0,
			     0,  0,  0,  0,  0,  0,  0,  0,
			     0,  0,  0,  0,  0,  0,  0,  0,
			     0,  0,  0,  0,  0,  0,  0,  0,
			     0,  0,  0,  0,  0,  0,  0,  0,
			     0,  0,  0,  0,  0,  0,  0,  0
		};
		
		// get pieces developed
		int punish_home_w[] = {
			     0,  0,  0,  0,  0,  0,  0,  0,
			     0,  0,  0,  0,  0,  0,  0,  0,
			     0,  0,  0,  0,  0,  0,  0,  0,
			     0,  0,  0,  0,  0,  0,  0,  0,
			     0,  0,  0,  0,  0,  0,  0,  0,
			     0,  0,  0,  0,  0,  0,  0,  0,
			     0,  0,  0,  0,  0,  0,  0,  0,
			     -50,  -50,  -50,  -50,  -50,  -50,  -50,  -50
		};
		
		// build a pawn structure
		int pawn_table_w[] = {
			     0,  0,  0,  0,  0,  0,  0,  0,
			    75, 75, 75, 75, 75, 75, 75, 75,
			    25, 25, 29, 29, 29, 29, 25, 25,
			     4,  8, 12, 21, 21, 12,  8,  4,
			     0,  4,  8, 17, 17,  8,  4,  0,
			     4, -4, -8,  4,  4, -8, -4,  4,
			     4,  8,  8,-17,-17,  8,  8,  4,
			     0,  0,  0,  0,  0,  0,  0,  0
		};
		
		// build a pawn structure
		int pawn_table_b[] = {
			     0,  0,  0,  0,  0,  0,  0,  0,
			     4,  8,  8,-17,-17,  8,  8,  4,
			     4, -4, -8,  4,  4, -8, -4,  4,
			     0,  4,  8, 17, 17,  8,  4,  0,
			     4,  8, 12, 21, 21, 12,  8,  4,
			    25, 25, 29, 29, 29, 29, 25, 25,
			    75, 75, 75, 75, 75, 75, 75, 75,
			     0,  0,  0,  0,  0,  0,  0,  0
		};
		
		// you're stronger when you control the center
		double positionalEval[] = {
				1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1,
				1, 1.1, 1.15, 1.155, 1.155, 1.15, 1.1, 1,
				1, 1.15, 1.2, 1.25, 1.25, 1.2, 1.15, 1,
				1, 1.15, 1.2, 1.25, 1.25, 1.2, 1.15, 1,
				1, 1.1, 1.15, 1.155, 1.155, 1.15, 1.1, 1,
				1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1
		};
		
		// sets up mapping from piece type to functions for white pieces and for black pieces
		Map<Integer, Function<Integer, Integer>> scoreWhiteMap = Map.of(
				0, i -> (int)(pawn_table_w[i]),
				1, i -> (int)(positionalEval[i] * 100 + 500),
				2, i -> (int)(positionalEval[i] * 100 + 300),
				3, i -> (int)(positionalEval[i] * 100 + 300),
				4, i -> (int)(positionalEval[i] * 200 + 900)
				);
		
		Map<Integer, Function<Integer, Integer>> scoreBlackMap = Map.of(
				6, i -> (int)(pawn_table_b[i]),
				7, i -> (int)(positionalEval[i] * 100 + 500),
				8, i -> (int)(positionalEval[i] * 100 + 300),
				9, i -> (int)(positionalEval[i] * 200 + 300),
				10, i -> (int)(positionalEval[i] * 100 + 900)
		);
		
		// uses IntStream to generate a stream of integer scores then sums them. This is funcitonal
		int scoreWhite = 0, scoreBlack = 0;
		scoreWhite = IntStream.range(0, 64).map(i -> {
			int pieceScore = 0;
			long position = 1L << i;
			for(int j = 0; j < 6; j ++ ) {
				if((this.state[j] & position) != 0) {
					pieceScore += scoreWhiteMap.getOrDefault(j, v->0).apply(i);
				}
			}
			return pieceScore;
		}).sum();
		
		scoreBlack = IntStream.range(0, 64).map(i -> {
			long position = 1L << i;
			int pieceScore = 0;
			for(int j = 6; j < 12; j++) {
				if((this.state[j] & position) != 0) {
					pieceScore += scoreBlackMap.getOrDefault(j,  v->0).apply(i);
				}
			}
			return pieceScore;
		}).sum();
		
		// Adds the checkmate value imperatively
		if(turn == 0 && checkmate) {
			scoreBlack += 2000;
		}
		else if(checkmate) {
			scoreWhite += 2000;
		}
		
		// returns the evaluation of the state of the current bitboard
		return scoreWhite - scoreBlack;
	}
	
	
	// prints the console application
	public String toString() {
		String board = "";
		boolean pieceFound = false;
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				long mask = 1L << (i*8+j);
				for(int x = 0; x < state.length; x++) {
					if((this.state[x] & mask) != 0) {
						pieceFound = true;
						board += pieces[x];
						break;
					}
				}
				if(!pieceFound) {
					board += '*';
				}
				else pieceFound = false;
				if(j < 7) board += ' ';
			}
			if(i < 7) board += '\n';
		}
		return board;
	}
	
	// prints out longs in the bit masks the shape of the chess board. For debugging
	public String bitmapToString(long bm) {
		String output = "";
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				long mask = 1L << (i*8+j);
				if((bm & mask) != 0) output += '1';
				else output += '0';
				if(j < 7) output += ' ';
			}
			if(i < 7) output += '\n';
		}
		
		return output;
	}
	
	// Ensures there are no overlapping states
	private boolean validatePiecePositionsUnique() {
		long boardState = 0;
		for(int i = 0; i < this.state.length; i++) {
			boardState |= this.state[i];
			if((this.state[i] & boardState) != 0) return false;
		}
		return true;
	}
}
