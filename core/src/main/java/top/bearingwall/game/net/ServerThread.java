package top.bearingwall.game.net;

import top.bearingwall.game.ServerMain;
import top.bearingwall.game.data.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

public class ServerThread extends Thread {
    private final Socket socket;
    private int playerIndex = 0;
    ObjectOutputStream oos;
    ObjectInputStream ois;

    public ServerThread(Socket socket, int playerIndex) {
        this.socket = socket;
        this.playerIndex = playerIndex;
    }

    @Override
    public void run() {
        var playerMap = ServerMain.INSTANCE.getPlayerMap();
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            while (true) {
                System.out.println(currentThread().getName() + ": 等待读取客户端回包...");
                Object data = ois.readObject();
                if (data instanceof Player) {
                    synchronized (playerMap) {
                        var clientPlayer = (Player) data;
                        playerMap.put(playerIndex, clientPlayer);
                        if (!ServerMain.INSTANCE.isGameOpen()) {
                            // 设置玩家King位置
                            Random r = new Random(System.currentTimeMillis());
                            int x = r.nextInt(20);
                            int y = r.nextInt(20);
                            var grids = GameMap.getGrids();
                            grids[x][y] = new King(clientPlayer,1,x,y);
                            GameMap.setGrids(grids);
                        }
                        if (playerMap.size() == 2) {
                            System.out.println(currentThread().getName() + ": 游戏开始");
                            playerMap.notifyAll();
                        } else {
                            System.out.println(currentThread().getName() + ": 等待更多玩家加入...");
                            playerMap.wait();
                        }
                    }
                } else if (data instanceof Move) {
                    ServerMain.INSTANCE.getMoveList().add((Move) data);
                    System.out.println("已加入Move");
                } else {
                    System.out.println(currentThread().getName() + ": 不支持的数据类型...");
                }
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendMap() throws IOException {
        var grids = GameMap.getGrids();
        if (oos != null) {
            oos.reset();
        }
        oos.writeObject(grids);
        System.out.println(currentThread().getName() + ": 已发送GameMap");
        oos.flush();
        ServerMain.INSTANCE.setGameOpen(true);
    }

    public void sendTurn(int turn) throws IOException {
        oos.writeObject(turn);
        System.out.println(currentThread().getName() + ": 已发送回合数" + turn);
        oos.flush();
    }

}
