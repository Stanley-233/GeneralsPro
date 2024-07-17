package top.bearingwall.game.net;

import top.bearingwall.game.ServerMain;
import top.bearingwall.game.data.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ServerThread extends Thread {
    private final Socket socket;
    private final ArrayList<Player> playerList;

    public ServerThread(Socket socket, ArrayList<Player> playerList) {
        this.socket = socket;
        this.playerList = playerList;
    }

    @Override
    public void run() {
        try (ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())
        ) {
            while (true) {
                System.out.println(currentThread().getName() + ": 等待读取客户端回包...");
                Object data = ois.readObject();
                if (data instanceof ClientData) {
                    ClientData clientData = (ClientData) data;
                    addPlayer(clientData.getClientPlayer());
                } else {
                    System.out.println(currentThread().getName() + ": 不支持的数据类型...");
                }
                if (!ServerMain.isGameOpen) {
                    GameMap.INSTANCE.createMap(playerList);
                    // TODO: Calculate GameMap and send to client
                    oos.writeObject(GameMap.INSTANCE);
                    oos.flush();
                    ServerMain.isGameOpen = true;
                } else {
                    // TODO: Calculate map and send to client
                }
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized void addPlayer(Player player) throws InterruptedException {
        playerList.add(player);
        if (playerList.size() == 1) {
            System.out.println();
            notifyAll();
        } else {
            System.out.println("等待更多玩家加入...");
            wait();
        }
    }
}
