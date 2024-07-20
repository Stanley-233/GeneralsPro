package top.bearingwall.game.util;

import top.bearingwall.game.ClientMain;
import top.bearingwall.game.data.GameMap;

import java.io.IOException;
import java.sql.SQLException;

public class ReplayThread extends Thread {
    private int currentReplayTurn = 1;

    @Override
    public void run() {
        try {
            sleep(500);
            ClientMain.INSTANCE.getReplaySound().play();
            while (true) {
                // finish read grid[][] from JDBC
                // 循环调用calculateReplayMap
                GameMap.setGrids(ClientDataHandler.databaseThread.readMap(currentReplayTurn));
                ClientDataHandler.INSTANCE.calculateReplayMap();
                currentReplayTurn++;
                sleep(500);
            }
        } catch (InterruptedException | SQLException | IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
