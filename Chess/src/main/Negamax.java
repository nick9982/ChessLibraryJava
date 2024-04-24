package main;

import java.util.ArrayList;

public class Negamax {
	public static Bitboard state = null;
	public static int maxDepth = 0;
	
	// Data type to store the score next to the path. This helps to return the optimal move from negamax
	public static class Score {
		public String path = "";
		public int score = 0;
		public Score(String pth, int score) {
			this.path = pth;
			this.score = score;
		}
	}
	
	
	// Recursive game tree evaluator
	public static Score negamax(int depth, int color, String path) {
        // leaf nodes at max depth or if the game is finished
        if (depth == 0 || state.isFinished()) {
            // Calculate the score based on the current game state
            int val = color * state.score();
            return new Score(path, val);
        }

        // Generate children moves
        ArrayList<String> children = state.generateChildren((char) ((color == 1) ? 0 : 1));
        Score bestScore = new Score(path, Integer.MIN_VALUE);

        // Iterate over all possible moves
        for (String move : children) {
            // Make the move
            if (!state.move(new Move(move))) {
                continue;
            }
            
            // Recursive call to negamax
            Score childScore = negamax(depth - 1, -color, path + move);

            int currentScore = -childScore.score;

            // If the current score is greater than the best score, update it
            if (currentScore > bestScore.score) {
                bestScore.score = currentScore;
                bestScore.path = childScore.path;
            }

            // Undo the mov
            state.Undo();
        }

        return bestScore;
    }
}
