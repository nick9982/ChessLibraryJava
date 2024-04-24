package main;

public class Move {
	private long bitFrom = 0, bitTo = 0;
	private String move;
	
	public Move(String move) {
		// store move in string
		this.move = move.toLowerCase();
		
		// don't eval illegal move format for bitboard
		if(move.length() != 4)
			return;
		
		// convert the string input into two longs representing the to and from positions for the bitboard to use
		stringMoveToBitMove();
	}
	
	private void stringMoveToBitMove() {
		
		for(int i = 0; i < 4; i+=2) {
			// calculate the row and column based on the move coordinates and bitwise AND them to get position
			
			long rowMask = -72057594037927936L >>> (((this.move.charAt(i+1)-49))*8),
				colMask = -9187201950435737472L >>> (8-this.move.charAt(i)-96);
			
			if(i == 0)
				this.bitFrom = rowMask & colMask;
			else
				this.bitTo = rowMask & colMask;
		}
	}
	
	// getters and setters
	public String toString() {
		return move;
	}
	
	public long getBitFrom() {
		return bitFrom;
	}
	
	public long getBitTo() {
		return bitTo;
	}
}
