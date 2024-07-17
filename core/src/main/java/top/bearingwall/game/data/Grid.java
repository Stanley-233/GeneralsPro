package top.bearingwall.game.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class Grid implements Serializable {
    private Player player;
    private int power;
    private int x;
    private int y;

    public Grid(Player player, int power, int x, int y) {
        this.player = player;
        this.power = power;
        this.x = x;
        this.y = y;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
