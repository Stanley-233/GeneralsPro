package top.bearingwall.game.util;

public class ExcelRow {
    private int turn;
    private int grid;
    private int power;

    public ExcelRow(int turn, int grid, int power) {
        this.turn = turn;
        this.grid = grid;
        this.power = power;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public int getGrid() {
        return grid;
    }

    public void setGrid(int grid) {
        this.grid = grid;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }
}
