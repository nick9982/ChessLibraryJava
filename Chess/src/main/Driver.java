package main;

import java.awt.Color;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

public class Driver {
	public static void main(String args[]) {
		Bitboard b = new Bitboard();
		GUI gui = new GUI(b, true, false);
	}
}