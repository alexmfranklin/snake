package snake;

import snake;
import java.util.ArrayList;

public class Board {

    private static final int EMPTY = 0;
    private static final int FOOD = 1;
    private static final int SNAKE = 2;

    private int size;

    private ArrayList<Snake> players = new ArrayList<Snake>;
    private int[][] board;

    public Board(int size) {
        this.size = size;

        //initialize the board
        board = new int[size][size];
        for(int i = 0; i < size; i ++) {
            for(int j = 0; j < size; j ++) {
                board[i][j] = EMPTY;
            }
        }
    }

    public void addSnake(int x, int y) {
        Snake snake = new Snake(this, x, y);
        players.add(snake);
        board[x][y] = SNAKE;
    }

    public void updateBoard(int state, int x, int y) {
        board[x][y] = state;
    }
    
    public int getBoardState(int x, int y) {
        if(x < size && y < size && x > -1 && size y > -1)
            return board[x][y];
        else
            return -1;
    }

    public void takeTurn() {

    }

    public int size() {
        return this.size;
    }
    

}