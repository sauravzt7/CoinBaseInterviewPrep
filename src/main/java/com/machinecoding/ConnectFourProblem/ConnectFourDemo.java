package com.machinecoding.ConnectFourProblem;

enum Symbol {
    X,
    O
}


class Board{
    // Even though the board size is supposed to be 6 * 7, Lets declare a row and column

    private Board instance;
    String[][] board;
    int rows = 6, cols = 7;

    public Board(int rows, int cols) {
        this.board = new String[rows][cols];
    }

    public Board getInstance(){
        if(instance == null){
            instance = new Board(rows, cols);
        }
        return instance;
    }


    public void displayBoard() {

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(board[i][j] + " ");
            }
        }
    }

    public boolean hasDisc(int row, int col) {
        return board[row][col].equals("X") || board[row][col].equals("O");
    }

    public String getDiscInCell(int row, int col) {
        return board[row][col];
    }

    public boolean isBoardFull() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if(board[i][j] == null)return false;
            }
        }

        return true;
    }

    public void reset(){
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = null;
            }
        }
    }
    public boolean dropDisc(int col, Symbol s) {
        for(int i = rows - 1; i >= 0; i--){
            if(board[i][col] != null){
                board[i][col] = s.toString();
                return true;
            }
        }
        return false;
    }
}

class Player {
    String name;
    Symbol symbol;

    public Player(String name, Symbol symbol) {
        this.name = name;
        this.symbol = symbol;
    }
}

class PlayerFactory {
    public static Player getPlayer(String name, Symbol s) {
        return new Player(name, s);
    }
}

interface WinningStrategy {
    boolean isWinner( Board board, Player currPalyer);
}

class HorizontalWinStrategy implements WinningStrategy {
    @Override
    public boolean isWinner(Board board, Player player) {
        String symbol = player.symbol.toString();
        for (int row = 0; row < 6; row++) {
            int count = 0;
            for (int col = 0; col < 7; col++) {
                if (symbol.equals(board.getDiscInCell(row, col))) {
                    count++;
                    if (count == 4) return true;
                } else {
                    count = 0;
                }
            }
        }
        return false;
    }
}


class VerticalWinStrategy implements WinningStrategy {
    @Override
    public boolean isWinner(Board board, Player player) {
        String symbol = player.symbol.toString();
        for (int col = 0; col < 7; col++) {
            int count = 0;
            for (int row = 0; row < 6; row++) {
                if (symbol.equals(board.getDiscInCell(row, col))) {
                    count++;
                    if (count == 4) return true;
                } else {
                    count = 0;
                }
            }
        }
        return false;
    }
}

interface GameManager{
    void startGame();
    void dropDisc(Player p1, Player p2);

}




public class ConnectFourDemo {
    public static void main(String[] args) {


    }
}
