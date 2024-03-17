package main;

public class Move {
	private long bitFrom = 0, bitTo = 0;
	private String move;
	
	public Move(String move) {
		this.move = move.toLowerCase();
		
		if(move.length() != 4)
			return;
		stringMoveToBitMove();
	}
	
	private void stringMoveToBitMove() {
		
		for(int i = 0; i < 4; i+=2) {
			//System.out.println(+this.move.charAt(i+1)-49);
			long rowMask = -72057594037927936L >>> (((this.move.charAt(i+1)-49))*8),
				colMask = -9187201950435737472L >>> (8-this.move.charAt(i)-96);
			
			if(i == 0)
				this.bitFrom = rowMask & colMask;
			else
				this.bitTo = rowMask & colMask;
		}
	}
	
	public String toString() {
		return move;
	}
	
	public long getBitFrom() {
		return bitFrom;
	}
	
	public long getBitTo() {
		return bitTo;
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
}
