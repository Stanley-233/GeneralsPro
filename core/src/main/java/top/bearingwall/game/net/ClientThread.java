package top.bearingwall.game.net;

import top.bearingwall.game.ClientMain;
import top.bearingwall.game.util.ClientDataHandler;
import top.bearingwall.game.data.GameMap;
import top.bearingwall.game.data.Grid;
import top.bearingwall.game.util.DatabaseThread;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientThread extends Thread {
    private boolean isMapReceived = false;
    private Socket connection;
    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;

    @Override
    public void run() {
        try {
            connection = new Socket(ClientDataHandler.INSTANCE.getServerIP(), 13696);
            oos = new ObjectOutputStream(connection.getOutputStream());
            ois = new ObjectInputStream(connection.getInputStream());
            System.out.println("已连接到服务器");
            while (!isMapReceived) {
                if (!ClientDataHandler.INSTANCE.isGameStarted()) {
                    oos.reset();
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
                    data = ois.readObject();
                    if (data instanceof Integer) {
                        ClientDataHandler.INSTANCE.setId(((Integer) data));
                        System.out.println("设置ID为" + ClientDataHandler.INSTANCE.getId());
                    }
                }
            }
            ClientMain.INSTANCE.getReadySound().play();
            ClientMain.INSTANCE.getReadySound_2().play();
            ClientDataHandler.databaseThread = new DatabaseThread();
            ClientDataHandler.databaseThread.start();
            while (true) {
                var data = ois.readObject();
                if (data instanceof Grid[][]) {
                    System.out.println("已收到GameMap");
                    GameMap.setGrids((Grid[][]) data);
                } else if (data instanceof Integer) {
                    ClientDataHandler.INSTANCE.setTurnCounter((Integer) data);
                    System.out.println("当前回合数：" + data);
                    ClientDataHandler.databaseThread.writeData((Integer) data,GameMap.getGrids());
                } else if (data instanceof String) {
                    System.out.println("收到消息：" + data);
                    ClientDataHandler.INSTANCE.setGameEndType((String) data);
                    ClientDataHandler.INSTANCE.setGameEnd(true);
                }
                if (isMapReceived) {
                    ClientDataHandler.INSTANCE.calculateMap();
                }
            }
        } catch (SocketException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendMove(Move move) throws IOException {
        oos.reset();
        oos.writeObject(move);
        System.out.println("已发送移动信息!");
        oos.flush();
    }
}
