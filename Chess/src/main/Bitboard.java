package main;

import java.io.FileWriter;
import java.io.IOException;

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
	private char turn = 0; // 0 = turn of white, 1 = turn of black
	private long enPassant = 0L;
	private long pinnedWhiteR = 0L, pinnedBlackR = 0L, pinnedWhiteB = 0L, pinnedBlackB = 0L, whiteAttackLane = 0L, blackAttackLane = 0L;
	private boolean checkmate = false;
	private boolean areThereLegalMoves = false;
	private int checkCount = 0;
	
	private static final char pieces[] = {'P', 'R', 'N', 'B', 'Q', 'K', 'p', 'r', 'n', 'b', 'q', 'k'};
	
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
		long blackPieces = this.blackPieces();
		long whitePieces = this.whitePieces();
		checkCount = 0;
		if(turn == 0) {
			blackAttackLane = 0L;
			pinnedWhiteR = 0L;
			pinnedWhiteB = 0L;
			long test_pos = this.state[5];
			long line = 0L;
			char whiteCnt = 0;
			//go up first
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

			if((KnightMove(this.state[5], 0L, (char)2, false) & this.state[8]) != 0)
				return true;
			
			if(((this.state[5] >>> 9 | this.state[5] >>> 7) & this.state[6]) != 0)
				return true;
		} else {
			whiteAttackLane = 0L;
			pinnedBlackR = 0L;
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
			
			long knightMoves = KnightMove(this.state[11], 0L, (char)2, false);
			if((knightMoves & this.state[2]) != 0)
				return true;
			
			if(((this.state[11] >>> 9 | this.state[11] >>> 7) & this.state[0]) != 0)
				return true;
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
	
	private long NaiveRookMove(long position, boolean rPin, long checkAttacks, char color, boolean forKing) {
		long test_pos = position;
		long result = 0L;
		long allPieces = this.allPiecePositions();
		//go up first
		while((test_pos & -72057594037927936L) == 0)
		{
			test_pos <<= 8;
			result |= test_pos;
			if((test_pos & allPieces) != 0L) {
				break;
			}
		}
		//System.out.println("\n\n" + bitmapToString(result));
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
	
	private long PawnMove(long position, boolean bPin, boolean rPin, char color, boolean forKing) {
		long moves = 0L;
		long whitePieces = whitePieces(), blackPieces = blackPieces();
		if(color == 0) {
			if(enPassant != 0L) {
				if((position >>> 1) == enPassant && (position & 72340172838076673L) == 0) {
					moves |= position >>> 9;
				}
				else if(position << 1 == enPassant && (position & -9187201950435737472L) == 0) {
					moves |= position >>> 7;
				}	
			}
			moves |= (position >>> 8) & ~whitePieces;
			if(moves != 0 && (position & 71776119061217280L) != 0) {
				moves |= position >>> 16 & ~whitePieces;
			}
			moves |= ((position >>> 7 | position >>> 9) & blackPieces);
			
			if(rPin) moves &= pinnedWhiteR;
			else if(bPin) moves &= pinnedWhiteB;
			if(blackAttackLane != 0L) moves &= blackAttackLane;
			if(!forKing) moves &= ~whitePieces();
		}
		else {
			if(enPassant != 0L) {
				if((position >>> 1) == enPassant && (position & 72340172838076673L) == 0) {
					moves |= position << 9;
				}
				else if(position << 1 == enPassant && (position & -9187201950435737472L) == 0) {
					moves |= position << 7;
				}
			}
			moves |= (position << 8) & ~blackPieces;
			if(moves != 0 && (position & 65280L) != 0) {
				moves |= position << 16 & ~blackPieces;
			}
			moves |= ((position << 7 | position << 9) & whitePieces);

			if(rPin) moves &= pinnedBlackR;
			else if(bPin) moves &= pinnedBlackB;
			if(whiteAttackLane != 0L) moves &= whiteAttackLane;
			if(!forKing) moves &= ~blackPieces();
		}
		return moves;
	}
	
	private long KnightMove(long position, long checkAttacks, char color, boolean forKing) {
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
	
	private long BishopMove(long position, boolean bPin, long chckAttacks, char color, boolean forKing) {
		long test_pos = position;
		long result = 0L;
		long allPieces = this.allPiecePositions();
		
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
	
	private long KingMove(long position, char color, boolean checkOppMoves) {
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
		
		if(color == 0) {
			result &= ~whitePieces();
			if(checkOppMoves) {
				result &= ~allLegalMoves((char)1, true, true);
			}
		} else {
			result &= ~blackPieces();
			if(checkOppMoves) {
				result &= ~allLegalMoves((char)0, true, true);
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
						legalMoves |= PawnMove(currentPosition, bPin, rPin, turn, forKing);
						break;
					case 1:
					case 7:
						if(bPin || kingMustMove) continue;
						legalMoves |= NaiveRookMove(currentPosition, rPin, chckAttcks, turn, forKing);
						break;
					case 2:
					case 8:
						if(rPin || bPin || kingMustMove) continue;
						legalMoves |= KnightMove(currentPosition, chckAttcks, turn, forKing);
						break;
					case 3:
					case 9:
						if(rPin || kingMustMove) continue;
						legalMoves |= BishopMove(currentPosition, bPin, chckAttcks, turn, forKing);
						break;
					case 4:
					case 10:
						if(kingMustMove) continue;
						if(rPin) legalMoves |= NaiveRookMove(currentPosition, rPin, chckAttcks, turn, forKing);
						else if(bPin) legalMoves |= BishopMove(currentPosition, bPin, chckAttcks, turn, forKing);
						else legalMoves |= NaiveRookMove(currentPosition, false, chckAttcks, turn, forKing) | BishopMove(currentPosition, false, chckAttcks, turn, forKing);
						break;
					case 5:
					case 11:
						legalMoves = KingMove(currentPosition, color, false);
						break;
					}
				}
			}
			currentPosition <<= 1;
		}
		
		return legalMoves;
	}
	
	public boolean move(Move move) {
		// System.out.println("\n\n\n" + bitmapToString((4L << 9) >>> 9));
		if(move.getBitFrom() == 0) return false; // Move is invalid
		int start = 0, length = 0;
		boolean inCheck = inCheck();
		boolean rPin = false, bPin = false;
		boolean kingMustMove = false;
		if(checkCount > 1) kingMustMove = true;
		long chckAttcks = 0L;
		if(this.turn == 0) {
			if((move.getBitFrom() & whitePieces()) == 0) return false;
			if((pinnedWhiteR & move.getBitFrom()) != 0) rPin = true;
			else if((pinnedWhiteB & move.getBitFrom()) != 0) bPin = true;
			chckAttcks = blackAttackLane;
			start = 0;
			length = 6;
		}
		else {
			if((move.getBitFrom() & blackPieces()) == 0) return false;
			if((pinnedBlackR & move.getBitFrom()) != 0) rPin = true;
			else if((pinnedBlackB & move.getBitFrom()) != 0) bPin = true;
			chckAttcks = whiteAttackLane;
			start = 6;
			length=12;
		}
		
		if(kingMustMove && !areThereLegalMoves) {
			int kingIdx = (this.turn+1)*6-1;
			if(KingMove(this.state[kingIdx], this.turn, true) == 0L) {
				checkmate = true;
				return false;
			}
			areThereLegalMoves = true;
		}
		
		if(inCheck && !areThereLegalMoves) {
			//checking if there are any legal moves
			int kingIdx = (this.turn+1)*6-1;
			if((allLegalMoves(this.turn, false, false) & KingMove(this.state[kingIdx], this.turn, true)) == 0L) {
				checkmate = true;
				return false;
			}
			areThereLegalMoves = true;
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
					legalMoves = KingMove(position, turn, true);
					idx = i;
				}
			}
		}

		//System.out.println("\n\n" +bitmapToString(pinnedWhiteB)+"\n\n");
		/*if(turn == 0 ) {
			System.out.println("\n\n" +bitmapToString(pinnedWhite)+"\n\n");
			if((move.getBitFrom() & pinnedWhite) != 0) {
				legalMoves &= pinnedWhite;
			}
		}
		else {
			if((move.getBitFrom() & pinnedBlack) != 0) {
				legalMoves &= pinnedBlack;
			}
		}*/
		
		if(inCheck && !kingMustMove) {
			// If check move must cover attack lane
			if(turn == 0)
				legalMoves &= blackAttackLane;
			else
				legalMoves &= whiteAttackLane;
				
		}
		
		if((legalMoves & destination) != 0) {
			
			enPassant = 0L;
			if((idx == 0 || idx == 6) && (destination == (legalMoves >>> 16) || destination == (legalMoves << 16))) {
				enPassant = destination;
			}
			
			//this makes the move happen
			char rmvIdx = 0;
			for(char i = 0; i < state.length; i++) {
				if((state[i] & destination) != 0) {
					rmvIdx = i;
					this.state[i] &= ~destination;
				}
			}
			
			this.state[idx] &= ~position;
			this.state[idx] |= destination;
			
			/*char rmvIdx = 0;
			for(char i = 0; i < state.length; i++) {
				if((state[i] & destination) != 0) {
					rmvIdx = i;
					this.state[i] &= ~destination;
				}
			}
			
			this.state[idx] &= ~position;
			this.state[idx] |= destination;

			if(inCheck()) {
				if(KingMove(position) == 0L) {
					checkmate = true;
					return false;
				}
				this.state[idx] |= position;
				this.state[idx] &= ~destination;
				this.state[rmvIdx] |= destination;
				return false;
			}*/
		}
		else
			return false;
		
		if(turn == 1) turn = 0;
		else turn = 1;
		areThereLegalMoves = false;
		return true;
	}
	
	private long[]rook_moves = new long[64];
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
			while((currentPosition & -9187201950435737089L) == 0/*cnt++ < 5*/) {
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
	
	private String bitmapToString(long bm) {
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
