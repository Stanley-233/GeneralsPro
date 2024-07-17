package top.bearingwall.game.net;

import top.bearingwall.game.ServerMain;
import top.bearingwall.game.data.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Random;

public class ServerThread extends Thread {
    private final Socket socket;
    private final HashMap<Integer,Player> playerMap;
    private int playerIndex = 0;

    public ServerThread(Socket socket, HashMap<Integer,Player> playerMap) {
        this.socket = socket;
        this.playerMap = playerMap;
    }

    @Override
    public void run() {
        try (ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())
        ) {
            while (true) {
                System.out.println(currentThread().getName() + ": 等待读取客户端回包...");
                Object data = ois.readObject();
                if (data instanceof Player) {
                    var clientData = (Player) data;
                    addPlayer(clientData);
                } else {
                    System.out.println(currentThread().getName() + ": 不支持的数据类型...");
                }
                if (!ServerMain.INSTANCE.isGameOpen()) {
                    // TODO: create map
                    Random r = new Random(System.currentTimeMillis());
                    int x = r.nextInt(20);
                    int y = r.nextInt(20);
                    var grids = GameMap.getGrids();
                    grids[x][y] = new King(playerMap.get(0),1,x,y);
                    GameMap.setGrids(grids);
                    oos.writeObject(GameMap.getGrids());
                    System.out.println(currentThread().getName() + ": 已发送GameMap");
                    oos.flush();
                    ServerMain.INSTANCE.setGameOpen(true);
                }
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized void addPlayer(Player player) throws InterruptedException {
        playerMap.put(playerIndex,player);
        playerIndex++;
        if (playerMap.size() == 1) {
            System.out.println();
            notifyAll();
        } else {
            System.out.println("等待更多玩家加入...");
            wait();
        }
    }

}
