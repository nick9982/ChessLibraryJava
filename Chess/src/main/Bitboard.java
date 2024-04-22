package main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import main.GUI.Point;

public class Bitboard {
	private long []state = new long[12];
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
	
	//Changes will be encoded in a stack to ensure that moves can be undone, w/ numbers pointing to each case, chars.
	//pinnedWhiteR - 1 then next 8 bytes
	//pinnedBlackR - 2 then..
	//pinnedWhiteB - 3 then..
	//pinnedWhiteR - 4 then..
	//whiteAttackLane - 5 then..
	//blackAttackLane - 6 then..
	//checkmate - 7
	//areThereLegalMoves - 8
	//wrCastle - 9
	//wlCastle = 10
	//brCastle - 11
	//blCastle - 12
	//castleHappen - 13
	//enPassantHappen - 14
	//enPassant - 15 then next 8 bytes
	//castleFrom - 16 then next 8 bytes
	//castleTo - 17 then next 8 bytes
	//checkCount 18 - then next 4 bytes
	//inCheck - 19
	//kingMustMove - 20
	//enPassantTake - 21 then next 4 byets
	//pawnToQueen - 22
	//state change - 0 ~ -1 - begin ~ state idx next 4 bytes ~ old state next 8 bytes ~, can repeat this old pattern ~ -1 end of state change: NEEDS TO BE CHANGED CURRENTLY IT IS IMPLEMENTED WRONG
	
	Stack<Character> changesMade = new Stack<Character>();
	Stack<Character> movesMade = new Stack<Character>();
	Stack<Bitboard> prevStates = new Stack<Bitboard>();
	private int changesMadeCnt = 0;
	private int movesMadeCnt = 0;
	private char turn = 0; // 0 = turn of white, 1 = turn of black
	private long enPassant = 0L;
	private long pinnedWhiteR = 0L, pinnedBlackR = 0L, pinnedWhiteB = 0L, pinnedBlackB = 0L, whiteAttackLane = 0L, blackAttackLane = 0L;
	private boolean checkmate = false;
	private boolean areThereLegalMoves = false;
	private boolean wrRookCastle = true, wlRookCastle = true, brRookCastle = true, blRookCastle = true, castleHappen = false, enPassantHappen = false;
	long castleFrom = 0L, castleTo = 0L;
	private int checkCount = 0;
	private boolean inCheck = false;
	private boolean kingMustMove = false;
	private long enPassantTake = 0;
	private boolean pawnToQueen = false;
	
	// [] pawn to queen storage
	// [] castleHappen storage
	// [] enPassant happen storage
	private static final char pieces[] = {'P', 'R', 'N', 'B', 'Q', 'K', 'p', 'r', 'n', 'b', 'q', 'k'};
	
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
	
	private char[] stackToCharArr(int length, Stack<Character> stack) {
		char arr[] = new char[length];
		for(int i = arr.length-1; i >= 0; i--) {
			arr[i] = stack.pop();
		}
		
		return arr;
	}
	
	private boolean charToBoolean(char inp) {
		if(inp == 0) {
			return false;
		}
		return true;
	}
	
	private void popoutNChangesFromStack(int n, Stack<Character> stack) {
		while(n-- > 0) {
			stack.pop();
		}
	}
	
	private void rollback() {
		popoutNChangesFromStack(changesMadeCnt, changesMade);
		popoutNChangesFromStack(movesMadeCnt, movesMade);
	}
	
	public void Undo() {
		//System.out.println("undo called");
		while(!movesMade.empty() && movesMade.peek() != 200) {
			char idx = movesMade.pop();
			long old_val = Number.decodeLong(stackToCharArr(4, movesMade));
			this.state[idx] = old_val;
			if(idx == 5 && this.state[5] == 0) {
				System.out.println("The white king state is set to 0 here.");
				System.exit(0);
			}
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
		//this.set(this.prevStates.pop().Duplicate());
	}
	
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
		// TODO Auto-generated method stub
		this.castleFrom = castleFrom;
	}
	
	private void setCastleTo(long castleTo) {
		this.castleTo = castleTo;
	}

	public void setEnPassantTake(long l) {
		this.enPassantTake = l;
	}
	
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
	}
	
	public long getenPassant() {
		// TODO Auto-generated method stub
		return this.enPassant;
	}

	public ArrayList<String> generateChildren(char color){
		boolean mustKingMove = false;
		if(checkCount > 1) mustKingMove = true;
		
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
		long position = 1L;
		ArrayList<String> legalMoves = new ArrayList<String>();
		for(int i = 0; i < 64; i++) {
			setHasBeenSetToFalse();
			changesMadeCnt = 0; // preparing for a rollback
			movesMadeCnt = 0;
			//prevStates.push(this.Duplicate());
			for(int j = start; j < end; j++) {
				if((position & this.state[j]) != 0) {
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
						if(mustKingMove) continue;
						legalMoves.addAll(numberToLegalMoveList(position, PawnMove(position, bPin, rPin, color, false) & ~position, j));
						break;
					case 1:
					case 7:
						if(bPin || mustKingMove) continue;
						legalMoves.addAll(numberToLegalMoveList(position, NaiveRookMove(position, rPin, chckAttcks, color, false) & ~position, j));
						break;
					case 2:
					case 8:
						if(bPin || rPin || mustKingMove) continue;
						legalMoves.addAll(numberToLegalMoveList(position, KnightMove(position, chckAttcks, color, false) & ~position, j));
						break;
					case 3:
					case 9:
						if(rPin || mustKingMove) continue;
						legalMoves.addAll(numberToLegalMoveList(position, BishopMove(position, bPin, chckAttcks, color, false) & ~position, j));
						break;
					case 4:
					case 10:
						if(mustKingMove) continue;
						if(rPin) legalMoves.addAll(numberToLegalMoveList(position, NaiveRookMove(position, rPin, chckAttcks, color, false) & ~position, j));
						else if(bPin) legalMoves.addAll(numberToLegalMoveList(position, BishopMove(position, bPin, chckAttcks, color, false) & ~position, j));
						else legalMoves.addAll(numberToLegalMoveList(position, ((NaiveRookMove(position, rPin, chckAttcks, color, false) | BishopMove(position, bPin, chckAttcks, color, false)) & ~position), j));
						break;
					case 5:
					case 11:
						legalMoves.addAll(numberToLegalMoveList(position, KingMove(position, turn, true, false) & ~position, j));
					}
				}
			}
			rollback();
			position <<= 1L;
		}
		return legalMoves;
	}
	
	private ArrayList<String> numberToLegalMoveList(long source, long result, int pType){
		
		int position = (int)(Math.log(source) / Math.log(2));
		int x = position % 8;
		int y = 7-(int)(position / 8);
		char p1 = (char)(x+97);
		char p2 = (char)(y+49);
		
		String src = p1 + "" + p2;
		
		ArrayList<String> moves = new ArrayList<String>();
		long idx = 1L;
		for(int i = 0; i < 64; i++) {
			if((idx & result) != 0) {
				moves.add(src + "" + (char)((i % 8)+97) + "" + (char)((7 - (int)(i / 8))+49));
				//System.out.println(src + "" + (char)((i % 8)+97) + "" + (char)((7 - (int)(i / 8))+49));
			}
			idx <<= 1L;
		}
		
		
		return moves;
	}
	
	/*private Point coordToPoint(String coord) {
		return new Point((int)coord.charAt(0)-97, 7-((int)coord.charAt(1)-49));
	}*/
	
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
	
	private long allPiecePositions() {
		long result = 0L;
		for(int i = 0; i < state.length; i++) {
			result |= state[i];
		}
		return result;
	}
	
	public void printState() {
		System.out.println();
		for(int i = 0; i < state.length; i++) {
			System.out.println("state[" + i + "] = " + state[i] + "L;");
		}
	}
	private boolean inCheck() {
		//System.out.println("Turn: " + (this.turn==0?"white":"black"));
		long blackPieces = this.blackPieces();
		long whitePieces = this.whitePieces();
		setChangeInt((char)18, checkCount);
		changesMadeCnt += 3;
		checkCount = 0;
		if(turn == 0) {
			setChangeLong((char)6, blackAttackLane);
			blackAttackLane = 0L;
			setChangeLong((char)4, pinnedWhiteR);
			pinnedWhiteR = 0L;
			setChangeLong((char)3, pinnedWhiteB);
			changesMadeCnt += 15;
			pinnedWhiteB = 0L;
			long test_pos = this.state[5];
			long line = 0L;
			char whiteCnt = 0;
			//go up first
			if(test_pos==0) {
				System.out.println("king is equal to 0");
				System.exit(0);
			}
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
		} else {
			setChangeLong((char)5, whiteAttackLane);
			whiteAttackLane = 0L;
			setChangeLong((char)2, pinnedBlackR);
			pinnedBlackR = 0L;
			setChangeLong((char)1, pinnedBlackB);
			changesMadeCnt += 15;
			pinnedBlackB = 0L;
			long test_pos = this.state[11];
			long line = 0L;
			char blackCnt = 0;
			//go up first
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
	
	private long whitePieces() {
		long result = 0L;
		for(int i = 0; i < 6; i++) {
			result |= state[i];
		}
		return result;
	}
	
	private long blackPieces() {
		long result = 0L;
		for(int i = 6; i < 12; i++) {
			result |= state[i];
		}
		return result;
	}
	
	public long NaiveRookMove(long position, boolean rPin, long checkAttacks, char color, boolean forKing) {
		long test_pos = position;
		long result = 0L;
		long allPieces = this.allPiecePositions();
		if(forKing) {
			if(color == 0) allPieces &= ~this.state[11];
			else allPieces &= ~this.state[5];
		}

		//go up first
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
		
		if(checkAttacks != 0L) {
			result &= checkAttacks;
		}
		
		//System.out.println(bitmapToString(-35604928818740737L));
		return result;
	}
	
	public long PawnMove(long position, boolean bPin, boolean rPin, char color, boolean forKing) {
		long moves = 0L;
		long whitePieces = whitePieces(), blackPieces = blackPieces();
		if(color == 0) {
			if(enPassant != 0L) {
				if((position >>> 1) == enPassant && (position & 72340172838076673L) == 0) {
					setChangeLong((char)21, enPassantTake);
					changesMadeCnt += 5;
					enPassantTake = enPassant;
					enPassant = position >>> 9;
					moves |= position >>> 9;
				}
				else if(position << 1 == enPassant && (position & -9187201950435737472L) == 0) {
					setChangeLong((char)21, enPassantTake);
					changesMadeCnt += 5;
					enPassantTake = enPassant;
					enPassant = position >>> 7;
					moves |= position >>> 7;
				}
				else {
					enPassant = 0;
				}
			}
			if(!forKing) {
				moves |= (position >>> 8) & ~(whitePieces | blackPieces);
				if(moves != 0 && (position & 71776119061217280L) != 0) {
					moves |= position >>> 16 & ~(blackPieces | whitePieces);
				}
			}
			//moves |= ((position >>> 7 | position >>> 9) & blackPieces);
			if((position & 72340172838076673L) == 0) {
				moves |= position >>> 9 & blackPieces;
				if(forKing) moves |= position >>> 9;
			}
			if((position & -9187201950435737472L) == 0) {
				moves |= position >>> 7 & blackPieces;
				if(forKing) moves |= position >>> 7;
			}
			//System.out.println("\n\n" + bitmapToString(moves));
			
			if(rPin) moves &= pinnedWhiteR;
			else if(bPin) moves &= pinnedWhiteB;
			if(blackAttackLane != 0L) moves &= blackAttackLane;
			if(!forKing) moves &= ~whitePieces();
		}
		else {
			if(enPassant != 0L) {
				if((position >>> 1) == enPassant && (position & 72340172838076673L) == 0) {
					setChangeLong((char)21, enPassantTake);
					changesMadeCnt += 5;
					enPassantTake = enPassant;
					enPassant = position << 7;
					moves |= position << 7;
				}
				else if(position << 1 == enPassant && (position & -9187201950435737472L) == 0) {
					setChangeLong((char)21, enPassantTake);
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
			//moves |= ((position << 7 | position << 9) & whitePieces);
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
	
	public long KnightMove(long position, long checkAttacks, char color, boolean forKing) {
		long E1 = position >>> 17, E2 = position >>> 10, E3 = position << 6, E4 = position << 15;
		long W1 = E1 << 2, W2 = E2 << 4, W3 = E3 << 4, W4 = E4 << 2;
		long result = 0L;
		result = E1 | E2 | E3 | E4 | W1 | W2 | W3 | W4;
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
			
		if(!forKing) {
			if(color == 0)
				result &= ~whitePieces();
			else if(color == 1)
				result &= ~blackPieces();
		}
		
		if(checkAttacks != 0L) result &= checkAttacks;
		
		return result;
	}
	
	public long BishopMove(long position, boolean bPin, long chckAttacks, char color, boolean forKing) {
		long test_pos = position;
		long result = 0L;
		long allPieces = this.allPiecePositions();
		if(forKing) {
			
			if(color == 0) allPieces &= ~this.state[11];
			else allPieces &= ~this.state[5];
		}
		
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
		if(chckAttacks != 0L) result &= chckAttacks;
		return result;
	}
	
	public long KingMove(long position, char color, boolean checkOppMoves, boolean forKing) {
		long result;
		result = position >>> 1 | position << 1 | position >>> 8 | position << 8 | position >>> 9 | position >>> 7 | position << 9 | position << 7;
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

		long blackPieces = blackPieces();
		long whitePieces = whitePieces();
		if(color == 0) {
			if(!forKing) result &= ~whitePieces;
			if(checkOppMoves) {
				long legalMoves = allLegalMoves((char)1, true, false);
				result &= ~legalMoves;
				if(wrRookCastle) {
					if(((whitePieces | blackPieces) & 6917529027641081856L) == 0 && (legalMoves & -1152921504606846976L) == 0) {
						result |= 4611686018427387904L;
					}
				}
				if(wlRookCastle) {
					if(((whitePieces | blackPieces) & 1008806316530991104L) == 0 && (legalMoves & 2233785415175766016L) == 0) {
						result |= 288230376151711744L;
					}
				}
			}
		} else {
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
	
	private void placeArrInStack(char [] arr, Stack<Character> stack) {
		for(int i = 0; i < arr.length; i++) {
			stack.push(arr[i]);
		}
	}
	
	private long allLegalMoves(char color, boolean forKing, boolean checkKing) {
		char start = 0, length = 0;
		boolean inCheck = inCheck();
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
		long currentPosition = 1L;
		for(int i = 0; i < 64; i++) {
			for(int x = start; x < length; x++) {
				if((currentPosition & this.state[x]) != 0) {
					boolean rPin = false, bPin = false;
					if(color == 0) {
						if((pinnedWhiteR & currentPosition) != 0) rPin = true;
						if((pinnedWhiteB & currentPosition) != 0) bPin = true;
					}
					else {
						if((pinnedBlackR & currentPosition) != 0) rPin = true;
						if((pinnedBlackB & currentPosition) != 0) bPin = true;
					}
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
			currentPosition <<= 1;
		}
		return legalMoves;
	}
	
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
	
	private boolean hasMoveBeenSet[] = new boolean[12];
	private boolean hasChangeBeenSet[] = new boolean[22];
	
	private void setHasBeenSetToFalse() {
		for(int i = 0; i < 12; i++) {
			hasMoveBeenSet[i] = false;
		}
		
		for(int i = 0; i < 22; i++) {
			hasChangeBeenSet[i] = false;
		}
	}
	
	private void setMove(char idx) {
		if(hasMoveBeenSet[idx]) return;
		hasMoveBeenSet[idx] = true;
		placeArrInStack(Number.encodeLong(this.state[idx]), movesMade);
		movesMade.push(idx);
	}
	
	private void setChangeInt(char idx, int val) {
		if(hasChangeBeenSet[idx-1]) return;
		hasChangeBeenSet[idx-1] = true;
		placeArrInStack(Number.encodeInteger(val), changesMade);
		changesMade.push(idx);
	}
	
	private void setChangeLong(char idx, long val) {
		if(hasChangeBeenSet[idx-1]) return;
		hasChangeBeenSet[idx-1] = true;
		placeArrInStack(Number.encodeLong(val), changesMade);
		changesMade.push(idx);
	}
	
	private void setChangeBool(char idx, boolean inp) {
		if(hasChangeBeenSet[idx-1]) return;
		hasChangeBeenSet[idx-1] = true;
		changesMade.push(booleanToChar(inp));
		changesMade.push(idx);
	}
	
	public void clearPrevStack() {
		while(!prevStates.isEmpty()) {
			prevStates.pop();
		}
	}
	
	public void clearStacks() {
		while(!changesMade.isEmpty()) {
			changesMade.pop();
		}
		
		while(!movesMade.isEmpty()) {
			movesMade.pop();
		}
	}
	
	public boolean move(Move move) {
		//Set king and rook position stored each time, bc of castling. same with pawns bc of en passant.
		//System.out.println("beginning");
		movesMadeCnt = 0; //preparing to rollback
		changesMadeCnt = 0;
		setHasBeenSetToFalse();
		//System.out.println(movesMade.size());
		//System.out.println(changesMade.size());
		
		//prevStates.push(this.Duplicate());
		//System.out.println(prevStates.size());
		
		//MOVES MADE NEEDS COMPLETE REWORK
		
		if(move.getBitFrom() == 0) return false; // Move is invalid
		changesMade.push((char)200);
		changesMadeCnt += 1;
		if(checkmate) {
			rollback();
			return false;
		}
		int start = 0, length = 0;
		enPassantHappen = false;
		castleHappen = false;
		//In check class member, move it down
		boolean rPin = false, bPin = false;
		setChangeBool((char)20, kingMustMove);
		setChangeLong((char)15, enPassant);
		changesMadeCnt += 7;
		if(checkCount > 1) kingMustMove = true;
		else kingMustMove = false;
		long chckAttcks = 0L;
		if(this.turn == 0) {
			if((move.getBitFrom() & whitePieces()) == 0) {
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
			if((move.getBitFrom() & blackPieces()) == 0) {
				rollback();
				return false;
			}
			if((pinnedBlackR & move.getBitFrom()) != 0) rPin = true;
			else if((pinnedBlackB & move.getBitFrom()) != 0) bPin = true;
			chckAttcks = whiteAttackLane;
			start = 6;
			length=12;
		}
		long legalMoves = 0;
		long position = move.getBitFrom(), destination = move.getBitTo();
		int idx = 0;
		for(int i = start; i < length; i++) {
			if((state[i] & move.getBitFrom()) != 0) {
				switch(i) {
				case 0:
				case 6:
					if(kingMustMove) return false;
					legalMoves = PawnMove(position, bPin, rPin, turn, false);
					idx = i;
					break;
				case 1:
				case 7:
					if(bPin || kingMustMove) return false;
					legalMoves = NaiveRookMove(position, rPin, chckAttcks, turn, false);
					idx = i;
					break;
				case 2:
				case 8:
					if(rPin || bPin || kingMustMove) return false;
					legalMoves = KnightMove(position, chckAttcks, turn, false);
					idx = i;
					break;
				case 3:
				case 9:
					if(rPin || kingMustMove) return false;
					legalMoves = BishopMove(position, bPin, chckAttcks, turn, false);
					idx = i;
					break;
				case 4:
				case 10:
					if(kingMustMove) return false;
					if(rPin) legalMoves = NaiveRookMove(position, rPin, chckAttcks, turn, false);
					else if(bPin) legalMoves = BishopMove(position, bPin, chckAttcks, turn, false);
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
		
		/*if(inCheck && !kingMustMove) {
			// If check move must cover attack lane
			if(turn == 0)
				legalMoves &= blackAttackLane;
			else
				legalMoves &= whiteAttackLane;
				
		}*/
		
		movesMade.push((char)200);
		movesMadeCnt += 1;
		if((legalMoves & destination) != 0) {
			
			if(enPassant != 0L) {
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
			if(this.state[5] == 0) {
				System.out.println("here3");
				System.exit(0);
			}
			enPassant = 0;
			boolean pawnToDiffPiece = false;
			if((idx == 0 || idx == 6) && ((move.getBitFrom() >>> 16) == destination || (move.getBitFrom() << 16) == destination)) {
				enPassant = destination;
			}
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
				setChangeBool((char)22, pawnToQueen);
				changesMadeCnt += 2;
				pawnToQueen = true;
			}
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
				setChangeBool((char)22, pawnToQueen);
				changesMadeCnt += 2;
				pawnToQueen = true;
			}
			if(this.state[5] == 0) {
				System.out.println("here1");
				System.exit(0);
			}

			//this makes the move happen
			
			if(idx == 11 && (brRookCastle == true || blRookCastle == true)) {
				setChangeBool((char)11, brRookCastle);
				brRookCastle = false;
				setChangeBool((char)12, blRookCastle);
				changesMadeCnt += 4;
				blRookCastle = false;
				if(destination == 64) {
					setChangeLong((char)16, castleFrom);
					castleFrom = 128;
					setChangeLong((char)17, castleTo);
					castleTo = 32;
					changesMadeCnt += 10;
					setMove((char)7);
					movesMadeCnt += 5;
					this.state[7] &= 128;
					this.state[7] |= 32;
					castleHappen = true;
				}
				else if(destination == 4) {
					setChangeLong((char)16, castleFrom);
					castleFrom = 1;
					setChangeLong((char)17, castleTo);
					castleTo = 8;
					changesMadeCnt += 10;
					setMove((char)7);
					movesMadeCnt += 5;
					this.state[7] &= ~1;
					this.state[7] |= 8;
					castleHappen = true;
				}
			}
			else if(idx == 5 && (wrRookCastle == true || wlRookCastle == true)) {
				setChangeBool((char)9, wrRookCastle);
				wrRookCastle = false;
				setChangeBool((char)10, wlRookCastle);
				changesMadeCnt += 4;
				wlRookCastle = false;
				//only working one
				if(destination == 4611686018427387904L) {
					setChangeLong((char)16, castleFrom);
					castleFrom = -9223372036854775808L;
					setChangeLong((char)17, castleTo);
					changesMadeCnt += 10;
					castleTo = 2305843009213693952L;
					setMove((char)1);
					movesMadeCnt += 5;
					this.state[1] &= ~-9223372036854775808L;
					this.state[1] |= 2305843009213693952L;
					castleHappen = true;
				}
				else if(destination == 288230376151711744L) {
					setChangeLong((char)16, castleFrom);
					castleFrom = 72057594037927936L;
					setChangeLong((char)17, castleTo);
					changesMadeCnt += 10;
					castleTo = 576460752303423488L;
					setMove((char)1);
					movesMadeCnt += 5;
					this.state[1] &= ~72057594037927936L;
					this.state[1] |= 576460752303423488L;
					castleHappen = true;
				}
			}
			else if(idx == 7 && brRookCastle && (move.getBitFrom() & 255) != 0) {
				setChangeBool((char)11, brRookCastle);
				changesMadeCnt += 2;
				brRookCastle = false;
			}
			else if(idx == 7 && blRookCastle && (move.getBitFrom() & 1) != 0) {
				setChangeBool((char)12, blRookCastle);
				changesMadeCnt += 2;
				blRookCastle = false;
			}
			else if(idx == 1 && wlRookCastle && (move.getBitFrom() & -9223372036854775808L) != 0) {
				setChangeBool((char)10, wlRookCastle);
				changesMadeCnt += 2;
				wlRookCastle = false;
			}
			else if(idx == 1 && wrRookCastle && (move.getBitFrom() & 72057594037927936L) != 0) {
				setChangeBool((char)9, wrRookCastle);
				changesMadeCnt += 2;
				wrRookCastle = false;
			}
			if(this.state[5] == 0) {
				System.out.println("here2");
				System.exit(0);
			}
			
			if(!pawnToDiffPiece) {
				for(char i = 0; i < state.length; i++) {
					if((state[i] & destination) != 0) {
						if(i == 11 || i == 5) { // you can't capture a king
							rollback();
							return false;
						}
						checkRooks(i, destination);
						setMove((char)i);
						movesMadeCnt += 5;
						this.state[i] &= ~destination;
					}
				}
				setMove((char)idx);
				movesMadeCnt += 5;
				checkRooks(idx, position);
				this.state[idx] &= ~position;
				this.state[idx] |= destination;
			}
		}
		else {
			rollback();
			return false;
		}
		
		if(turn == 1) turn = 0;
		else turn = 1;
		
		setChangeBool((char)19, inCheck);
		changesMadeCnt += 2;
		//System.out.println("Before");
		inCheck = inCheck();
		//System.out.println("After");
		//Move these two down
		
		if(kingMustMove && !areThereLegalMoves) {
			int kingIdx = (this.turn+1)*6-1;
			if(KingMove(this.state[kingIdx], this.turn, true ,false) == 0L) {
				setChangeBool((char)7, checkmate);
				changesMadeCnt += 2;
				checkmate = true;
			}
			setChangeBool((char)8, areThereLegalMoves);
			changesMadeCnt += 2;
			areThereLegalMoves = true;
		}
		else if(inCheck && !areThereLegalMoves) {
			//checking if there are any legal moves
			int kingIdx = (this.turn+1)*6-1;
			if((allLegalMoves(this.turn, false, true) | KingMove(this.state[kingIdx], this.turn, true, false)) == 0L) {
				setChangeBool((char)7, checkmate);
				changesMadeCnt += 2;
				checkmate = true;
			}
			setChangeBool((char)8, areThereLegalMoves);
			changesMadeCnt += 2;
			areThereLegalMoves = true;
		}
		else if(!inCheck) {
			setChangeBool((char)8, areThereLegalMoves);
			changesMadeCnt += 2;
			areThereLegalMoves = false;
		}
		return true;
	}
	
	/*private long[]rook_moves = new long[64];
	private long[]bishop_moves = new long[64];
	public void calculateMoveBoard() {
		int idx = 0;
		
		long corners = -9151314442816847743L;
		for(int i = 0; i < 64; i++) {
			long col = -9187201950435737472L >>> (i/8);
			long row = -72057594037927936L >>> (i%8)*8;
			long edges = -35604928818740737L;
			if(i%8 == 0) {
				edges &= ~-36028797018963968L;
			}
			else if(i % 8 == 7) {
				edges &= ~255L;
			}
			if(i/8 == 0)
			{
				edges &= ~-9187201950435737472L;
			}
			else if(i/8 == 7) {
				edges &= ~72340172838076673L;
			}
			long rookSqs = ((col ^ row) &~edges) & ~(-9223372036854775808L >>> i) & ~corners;
			rook_moves[idx] = rookSqs;
			

			edges = -35604928818740737L;
			long bishopPos = col & row;
			long currentPosition = bishopPos;
			long bishopSqs = 0;
			while((currentPosition & -9187201950435737089L) == 0cnt++ < 5) {
				currentPosition >>>= 7;
				bishopSqs |= currentPosition;
			}
			currentPosition = bishopPos;
			while((currentPosition & -71775015237779199L) == 0) {
				currentPosition <<= 7;
				bishopSqs |= currentPosition;
			}
			currentPosition = bishopPos;
			while((currentPosition & 72340172838076927L) == 0) {
				currentPosition >>>= 9;
				bishopSqs |= currentPosition;
			}
			currentPosition = bishopPos;
			while((currentPosition & -35887507618889600L) == 0) {
				currentPosition <<= 9;
				bishopSqs |= currentPosition;
			}
			
			bishopSqs = (bishopSqs & ~bishopPos) & ~edges;
			bishop_moves[idx] = bishopSqs;
		}
	}*/
	
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
	
	public boolean getEnPassantHappen() {
		boolean enPassantHappen_ = this.enPassantHappen;
		this.enPassantHappen = false;
		return enPassantHappen_;
	}
	
	public boolean getPawnToQueen() {
		boolean ptoq = this.pawnToQueen;
		this.pawnToQueen = false;
		return ptoq;
	}
	
	public long getEnPassantTake() {
		return this.enPassantTake;
	}
	
	public int score() {
		int result = 0;
		
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
		
		int scoreWhite = 0, scoreBlack = 0;
		long position = 1L;
		for(int i = 0; i < 64; i++) {
			for(int j = 0; j < 12; j++) {
				if((this.state[j] & position) != 0) {
					//i = i%8*8 + i/8;
					switch(j) {
					case 5:
					case 11:
						continue;
					case 0:
						scoreWhite += pawn_table_w[i];
						break;
					case 1:
						scoreWhite += (int)(positionalEval[i] * 100 + 500) - punish_home_w[i];
						break;
					case 2:
					case 3:
						scoreWhite += (int)(positionalEval[i] * 100 + 300) - punish_home_w[i];
						break;
					case 4:
						scoreWhite += (int)(positionalEval[i] * 200 + 900) - punish_home_w[i];
						break;
					case 6:
						scoreBlack += pawn_table_b[i];
						break;
					case 7:
						scoreBlack += (int)(positionalEval[i] * 100 + 500) - punish_home_w[i];
						break;
					case 8:
					case 9:
						scoreBlack += (int)(positionalEval[i] * 100 + 300) - punish_home_w[i];
						break;
					case 10:
						scoreBlack += (int)(positionalEval[i] * 200 + 900) - punish_home_w[i];
						break;
					}
				}
			}
			position <<= 1L;
		}
		if(turn == 0) {
			if(checkmate)
				scoreBlack += 2000;
		}
		else if(checkmate)
			scoreWhite += 2000;
		
		return scoreWhite - scoreBlack;
	}
	
	private String intToCoords(int input) {
		int row = input%8;
		int col = 7-(input/8);
		return (char)(col + 97) + "" + (char)(row+49);
	}
	
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
	
	private boolean validatePiecePositionsUnique() {
		long boardState = 0;
		for(int i = 0; i < this.state.length; i++) {
			boardState |= this.state[i];
			if((this.state[i] & boardState) != 0) return false;
		}
		return true;
	}
}
