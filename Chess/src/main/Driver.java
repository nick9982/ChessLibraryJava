package main;

import java.awt.Color;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

public class Driver {
	public static void main(String args[]) {

		long []state = new long[12];
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

		Bitboard b = new Bitboard();
		b.setState(state);
		GUI gui = new GUI(b);
	}
}