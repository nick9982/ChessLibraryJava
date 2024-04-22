package main;

import java.util.ArrayList;

public class Negamax {
	public static Bitboard state = null;
	public static int maxDepth = 0;
	
	public static class Score {
		public String path = "";
		public int score = 0;
		public Score(String pth, int score) {
			this.path = pth;
			this.score = score;
		}
	}
	
	public static Score negamax(int depth, int color, String path) {
        // Terminating condition: either depth is zero or the game state is final
        if (depth == 0 || state.isFinished()) {
            // Calculate the score based on the current game state
            int val = color * state.score();
            //System.out.println(color);
            return new Score(path, val);
        }

        // Generate children moves
        ArrayList<String> children = state.generateChildren((char) ((color == 1) ? 0 : 1));
        Score bestScore = new Score(path, Integer.MIN_VALUE);

        // Iterate over all possible moves
        for (String move : children) {
            // Make the move
            if (!state.move(new Move(move))) {
                continue; // Skip invalid moves
            }
            
            if(depth == 1) {
            	System.out.println(color);
            }
            // Recursive call to negamax with updated alpha-beta
            Score childScore = negamax(depth - 1, -color, path + move);

            // Calculate the negated score for the parent
            int currentScore = -childScore.score;

            // If the current score is greater than the best score, update it
            if (currentScore > bestScore.score) {
                bestScore.score = currentScore;
                bestScore.path = childScore.path;
            }

            // Undo the move to backtrack
            state.Undo();
        }

        return bestScore;
    }
}
