package top.bearingwall.game.data;

import lombok.Getter;

import java.io.Serializable;

public final class GameMap implements Serializable {
    private static final long serialVersionUID = 1L;
    private static GameMap instance;

    public static Grid[][] grids;

    private GameMap() {
        grids = new Grid[20][20];
    }

    public static GameMap getInstance() {
        if (instance == null) {
            instance = new GameMap();
        }
        return instance;
    }

    private Object readResolve() {
        return getInstance();
    }

    public static void setInstance(GameMap instance) {
        GameMap.instance = instance;
    }

    public static Grid[][] getGrids() {
        return grids;
    }

    public static void setGrids(Grid[][] grids) {
        GameMap.grids = grids;
    }
}
