package main;

public class Number {
	
	// Encodes integers for the changes made stack in the bitboard
	public static char[] encodeInteger(int input) {
		char[] chars = new char[2];
        for (int i = 0; i < 2; i++) {
            chars[i] = (char) ((input >>> (i * 16)) & 0xFFFF);
        }
        return chars;
	}
	
	// encodes longs for the changes made stack in the bitboard
	public static char[] encodeLong(long input) {
        char[] chars = new char[4];
        for (int i = 0; i < 4; i++) {
            chars[i] = (char) ((input >>> (i * 16)) & 0xFFFF);
        }
        return chars;
	}
	
	// decodes longs from the changes made stack in the bitboard
	public static long decodeLong(char[] input) {
        long value = 0;
        for (int i = 0; i < 4; i++) {
            value |= ((long) input[i] & 0xFFFF) << (i * 16);
        }
        return value;
	}
	
	// decodes integers from the changes made stack in the bitboard
	public static int decodeInteger(char[] input) {
		int value = 0;
        for (int i = 0; i < 2; i++) {
            value |= ((long) input[i] & 0xFFFF) << (i * 16);
        }
        return value;
	}
}
