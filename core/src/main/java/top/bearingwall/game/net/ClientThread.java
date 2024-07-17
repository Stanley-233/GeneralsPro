package top.bearingwall.game.net;

import top.bearingwall.game.ClientDataHandler;
import top.bearingwall.game.data.GameMap;
import top.bearingwall.game.data.Grid;

import java.io.*;
import java.net.Socket;
import javax.swing.JOptionPane;

public class ClientThread extends Thread {
    private volatile boolean isMapReceived = false;

    @Override
    public void run() {
        try {
            Socket connection = new Socket("127.0.0.1", 13695);
            System.out.println("已连接到服务器");
            ObjectOutputStream oos = new ObjectOutputStream(connection.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(connection.getInputStream());
            while (true) {
                if (!ClientDataHandler.INSTANCE.isGameStarted()) {
                    oos.writeObject(ClientDataHandler.INSTANCE.getPlayer());
                    System.out.println("已发送玩家信息!");
                    oos.flush();
                    ClientDataHandler.INSTANCE.setGameStarted(true);
                } else {
                    Object data = ois.readObject();
                    if (data instanceof Grid[][]) {
                        System.out.println("已收到GameMap");
                        GameMap.setGrids((Grid[][]) data);
                        isMapReceived = true;
                    }
                }
                if (isMapReceived) {
                    ClientDataHandler.INSTANCE.setTurnCounter(ClientDataHandler.INSTANCE.getTurnCounter() + 1);
                    ClientDataHandler.INSTANCE.calculateMap();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getLocalizedMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}
