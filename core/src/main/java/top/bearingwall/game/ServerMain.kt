package top.bearingwall.game

import lombok.SneakyThrows
import top.bearingwall.game.data.Player
//import top.bearingwall.game.net.GameServer
import java.net.ServerSocket
import java.net.Socket

object ServerMain {
    private val clients = HashMap<Int, Socket>()
    private var clientCount = 0
    private val playerList = ArrayList<Player>()
    @JvmField
    var isGameOpen: Boolean = false

    @SneakyThrows
    @JvmStatic
    fun main(args: Array<String>) {
//        playerList.add(new Player("Stanley", 1));
//        playerList.add(new Player("Test2", 2));
//        GameMap.createMap(playerList);
//        GameMap.gameMap;
        println("服务器启动...")
        println("Devleoped by 23301171 @ BJTU")
        ServerSocket(13695).use { serverSocket ->
            while (!isGameOpen) {
                println("Main: 等待连接客户端...")
                val socket = serverSocket.accept()
                if (!isGameOpen) {
                    clients[clientCount++] = socket
//                    GameServer(socket, playerList).start()
                    println("Main: 与客户端连接成功！")
                }
            }
        }
    }
}
