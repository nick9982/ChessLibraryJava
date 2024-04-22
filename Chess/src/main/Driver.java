package main;

import java.awt.Color;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

public class Driver {
	public static void main(String args[]) {

		int depth = 6;
		long []state = new long[12];
		state[0] = 47147059404341248L;
		state[1] = -9151314442816847872L;
		state[2] = 4399120252928L;
		state[3] = 2594073385365405696L;
		state[4] = 576460752303423488L;
		state[5] = 1152921504606846976L;
		state[6] = 3840L;
		state[7] = 129L;
		state[8] = 66L;
		state[9] = 36L;
		state[10] = 8L;
		state[11] = 16L;
		
		// The rook does not make the best moves
		/*state[0] = 47147059404341248L;
		state[1] = -9151314442816847872L;
		state[2] = 4399120252928L;
		state[3] = 2594073385365405696L;
		state[4] = 576460752303423488L;
		state[5] = 1152921504606846976L;
		state[6] = 3840L;
		state[7] = 129L;
		state[8] = 66L;
		state[9] = 36L;
		state[10] = 8L;
		state[11] = 16L;*/
		
		//Strange behavior
		state[0] = 46866683939258368L;
		state[1] = -9151314442816847872L;
		state[2] = 1207959552L;
		state[3] = 2594073385365405696L;
		state[4] = 274877906944L;
		state[5] = 1152921504606846976L;
		state[6] = 69120L;
		state[7] = 549755813889L;
		state[8] = 66L;
		state[9] = 36L;
		state[10] = 8L;
		state[11] = 16L;
		
		state[0] = 1548181431123968L;
		state[1] = 4683743612465315840L;
		state[2] = 0L;
		state[3] = 2308094809027379200L;
		state[4] = 0L;
		state[5] = 576460752303423488L;
		state[6] = 68096L;
		state[7] = 129L;
		state[8] = -9223372036854775806L;
		state[9] = 4L;
		state[10] = 549755813888L;
		state[11] = 16L;
		
		/*state[0] = 36310551973855232L;
		state[1] = 74309393851613184L;
		state[2] = 68719476736L;
		state[3] = 4398046511104L;
		state[4] = 0L;
		state[5] = 33554432L;
		state[6] = 8448L;
		state[7] = 1L;
		state[8] = 2112L;
		state[9] = 32L;
		state[10] = 0L;
		state[11] = 16L;*/
		
		state[0] = 1548181426946048L;
		state[1] = 4683743612465315840L;
		state[2] = 0L;
		state[3] = 3458764513820540928L;
		state[4] = 0L;
		state[5] = 576460752303423488L;
		state[6] = 68096L;
		state[7] = 65L;
		state[8] = -9223372036854775806L;
		state[9] = 4L;
		state[10] = 549755813888L;
		state[11] = 16L;
		
		state[0] = 318446390738944L;
		state[1] = 8388608L;
		state[2] = 0L;
		state[3] = 4503599627370496L;
		state[4] = 0L;
		state[5] = 1152921504606846976L;
		state[6] = 8960L;
		state[7] = 34359738368L;
		state[8] = 0L;
		state[9] = 134217728L;
		state[10] = 0L;
		state[11] = 32L;
		
		state[0] = 27162335521013760L;
		state[1] = 72057594037927936L;
		state[2] = 35184372088832L;
		state[3] = 0L;
		state[4] = 256L;
		state[5] = 4611686018427387904L;
		state[6] = 33612800L;
		state[7] = 136L;
		state[8] = 0L;
		state[9] = 0L;
		state[10] = 262144L;
		state[11] = 4L;
		
		state[0] = 1196303010758656L;
		state[1] = 0L;
		state[2] = 0L;
		state[3] = 0L;
		state[4] = 0L;
		state[5] = 1152921504606846976L;
		state[6] = 24832L;
		state[7] = 0L;
		state[8] = 0L;
		state[9] = 0L;
		state[10] = 0L;
		state[11] = 262144L;
		
		/*try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/

		Bitboard b = new Bitboard();
		//b.setState(state);
		
		/*b.setTurn((char)1);
		b.move(new Move("h4f2"));
		b.move(new Move("a2a3"));
		System.out.println(b);
		b.setTurn((char)1);
		Negamax.state = b;
		Negamax.Score sc = Negamax.negamax(6, Integer.MIN_VALUE, Integer.MAX_VALUE, -1, "");
		System.out.println();
		System.out.println("Score:\n" + sc.score);
		System.out.println("path:\n" + sc.path);
		b.move(new Move(sc.path.substring(0, 4)));
		System.out.println();
		System.out.println(b);*/
		
		/*long val = b.getState()[0]; // white pawns val
		System.out.println("Before val: " + val);
		b.move(new Move("a2a4"));
		//System.out.println("The decoded long: " + val);
		//char longEnc[] = Number.encodeLong(val);
		//System.out.println("The decoded long: " + Number.decodeLong(longEnc));
		
		b.Undo();
		val = b.getState()[0]; // white pawns val
		System.out.println("After val: " + val);
		System.out.println("\n" + b.toString() + "\n");*/
		
		//System.out.println("\n" + b.toString() + "\n");
		//Negamax.state = b;
		//Negamax.maxDepth = depth;
		/*int cnt = 0;
		while(true) {
			System.out.println("White move: ");
			Negamax.state = b;
			Negamax.Score sc = Negamax.negamax(depth,  Integer.MIN_VALUE, Integer.MAX_VALUE, 1, "");
			boolean isMoveLegal = b.move(new Move(sc.path.substring(0, 4)));
			b.clearStacks();
			//b.clearPrevStack();
			System.out.println("\n" + b.toString() + "\n");
			if(b.isFinished()) {
				System.out.println("Checkmate! black loses");
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Black move: ");
			//Negamax.setInitialState(b);
			Negamax.state = b;
			sc = Negamax.negamax(depth,  Integer.MIN_VALUE, Integer.MAX_VALUE, -1, "");
			b.move(new Move(sc.path.substring(0, 4)));
			b.clearStacks();
			//b.clearPrevStack();
			System.gc();
			System.out.println("\nThis the move\n" + b.toString() + "\n");
			if(b.isFinished()) {
				System.out.println("Checkmate! white loses");
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		/*int val = Negamax.negamax(depth, Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
		System.out.println(val);
		System.out.println();
		b.move(new Move(Negamax.optimalMove));
		System.out.println(Negamax.optimalMove.toString());
		System.out.println(b.toString());*/
		//b.setState(state);
		GUI gui = new GUI(b, true, false);
	}
}