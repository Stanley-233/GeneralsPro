package top.bearingwall.game

import lombok.SneakyThrows
import top.bearingwall.game.data.*
import top.bearingwall.game.net.ServerThread
import top.bearingwall.game.net.ServerTurnCounter
import java.lang.Thread.sleep
import java.net.ServerSocket
import java.util.*

object ServerMain {
    val clients = HashMap<Int,Thread>()
    val playerMap = HashMap<Int,Player>()
    var isGameOpen: Boolean = false

    @SneakyThrows
    @JvmStatic
    fun main(args: Array<String>) {
        println("服务器启动...")
        println("Devleoped by 23301171 @ BJTU")
        val grids = Array(20) {
            arrayOfNulls<Grid>(20)
        }
        for (x in 0..19) {
            for (y in 0..19) {
                grids[x][y] = Blank(x, y)
            }
        }
        val r = Random(System.currentTimeMillis())
        for (i in 0..49) {
            val x = r.nextInt(20)
            val y = r.nextInt(20)
            grids[x][y] = Mountain(x, y)
        }
        for (i in 0..14) {
            val x = r.nextInt(20)
            val y = r.nextInt(20)
            grids[x][y] = Tower(SystemPlayer, r.nextInt(20) + 20, x, y)
        }
        GameMap.grids = grids
        ServerSocket(13695).use { serverSocket ->
            println("Main: 等待连接客户端0...")
            val client0 = serverSocket.accept()
            val thread0 = ServerThread(client0, playerMap)
            clients.put(0,thread0)
            thread0.start()
            println("Main: 客户端0连接成功，等待连接客户端1...")
            val client1 = serverSocket.accept()
            val thread1 = ServerThread(client1, playerMap)
            clients.put(1,thread1)
            thread1.start()
            println("Main: 客户端1连接成功！")
            while (!isGameOpen) {
                sleep(1000)
            }
            ServerTurnCounter().start()
        }
    }
}
