package top.bearingwall.game.data;

import java.io.Serializable;

public class King extends Tower implements Serializable {
    public King(Player owner, int power, int x, int y) {
        super(owner, power, x, y);
    }
}
