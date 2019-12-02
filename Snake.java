package snake;

import snake.board;

public class Snake {

    private static final int EMPTY = 0;
    private static final int FOOD = 1;
    private static final int SNAKE = 2;

    private static final int NORTH = 0;
    private static final int EAST = 1;
    private static final int SOUTH = 2;
    private static final int WEST = 3;

    private static final int FORWARD = 0;
    private static final int LEFT = 1;
    private static final int RIGHT = 2;

    private int direction;

    private int length;

    private int x[];
    private int y[];
    
    private Board board;

    public Snake(Board board, int x, int y) {
        int size = board.size();
        this.x = new int[size*size];
        this.y = new int[size*size];

        this.length = 3;
        this.board = board;
        this.x[0] = x;
        this.y[0] = y;
        this.direction = 0;
    }

    public void takeMove(int move) {
        if(move == FORWARD) {
            if(direction == NORTH) {
                y[0] -= 1;
            }
            else if(direction == EAST) {
                x[0] += 1;
            }
            else if(direction == SOUTH) {
                y[0] += 1;
            }
            else if(direction == WEST) {
                x[0] -= 1;
            }
        }
        else if(move == LEFT) {
            if(direction == NORTH) {
                x[0] -= 1;
            }
            else if(direction == EAST) {
                y[0] -= 1;
            }
            else if(direction == SOUTH) {
                x[0] += 1;
            }
            else if(direction == WEST) {
                y[0] += 1;
            }
        }
        else if(move == RIGHT) {
            if(direction == NORTH) {
                x[0] += 1;
            }
            else if(direction == EAST) {
                y[0] += 1;
            }
            else if(direction == SOUTH) {
                x[0] -= 1;
            }
            else if(direction == WEST) {
                y[0] -= 1;
            }
        }

        //look at where the snake is going and see what it is running into
        int nextPosition = board.getBoardState(x[0], y[0])
        if(nextPosition == -1 || nextPosition == SNAKE) {
            for(int i = 0; i < length; i ++) {
                board.updateBoard(EMPTY, x[i], y[i]);
            }
            return;
        } else if (nextPosition == FOOD) {
            length ++;
        }
        board.updateBoard(SNAKE, x[0], y[0]);

        //move all joints up the chain
        for(int i = length; i > 0; i --) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

    }

    

}