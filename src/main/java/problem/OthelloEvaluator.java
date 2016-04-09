package problem;

import neuralnetwork.Network;
import org.jenetics.util.RandomRegistry;
import othello.Board;
import othello.Game;

import java.awt.*;

/**
 * Created by Laurens on 8-4-2016.
 */
public class OthelloEvaluator {


    public static final int GAME_CYCLES = 178;
    private int side;
    private int opponent;
    private static final String[] names = new String[]{"a","b"};

    public double evaluate(Network network){
        double score = 0;
        for (int i = 0; i < GAME_CYCLES; i++) {
            side = RandomRegistry.getRandom().nextInt(2)+1;
            opponent = side==1?2:1;
            score += playGame(network);
        }
        return score;
    }

    private double playGame(Network network){
        Game game = new Game(8,"a","b");
        game.prepareStandardGame();
        String me = names[side-1];
        String opponentName = names[opponent-1];
        game.setClientBegins(me.equals("a"));
        while(!game.checkIfMatchDone()){
            String player = game.getCurrentPlayer();
            if(player.equals(me)){
                game.doMove(getNetworkMove(network,game));
            }else{
                game.doMove(opponentMove(game));
            }
            game.endTurn();
        }
        int networkScore = game.getScore(me);
        int opponentScore = game.getScore(opponentName);
        return 64-opponentScore;
    }

    private int getNetworkMove(Network network, Game game){
        Point[] possibleMoves = game.getBoard().getPossibleMoves(side);
        float[] ratings = getRatings(game.getBoard(),network);
        float max = -Float.MAX_VALUE;
        int move = 0;
        for (Point possibleMove : possibleMoves) {
            int index = pointToInt(possibleMove);
            if(ratings[index]>max){
                max = ratings[index];
                move = index;
            }
        }
        return move;
    }

    private int opponentMove(Game game){
        Point[] possibleMoves = game.getBoard().getPossibleMoves(opponent);
        return pointToInt(possibleMoves[RandomRegistry.getRandom().nextInt(possibleMoves.length)]);
    }

    private int pointToInt(Point point) {
        return point.x * 8 + point.y;
    }

    private float[] getRatings(Board board, Network network){
        int[][] pieces = board.getBoardPieces();
        int count = 0;
        float[] input = new float[128];
        for (int[] piece : pieces) {
            for (int i : piece) {
                input[i++] = side==i? 1f : -1f;
                input[i++] = side==opponent? 1f : -1f;
            }
        }
        return network.evaluate(input);
    }
}
