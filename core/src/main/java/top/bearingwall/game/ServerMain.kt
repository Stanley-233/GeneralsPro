package top.bearingwall.game

import top.bearingwall.game.data.*
import top.bearingwall.game.data.GameMap.grids
import top.bearingwall.game.net.Move
import top.bearingwall.game.net.ServerThread
import java.lang.Thread.sleep
import java.net.ServerSocket
import java.util.*

object ServerMain {
    val clients = HashMap<Int,ServerThread>()
    @get:Synchronized
    val playerMap = HashMap<Int,Player>()
    @get:Synchronized
    @set:Synchronized
    var moveList = LinkedList<Move>()
    var isGameOpen: Boolean = false
    var turnCount: Int = 0
    var gameEnd = false

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
        ServerSocket(13696).use { serverSocket ->
            println("main: 等待连接客户端0...")
            val client0 = serverSocket.accept()
            val thread0 = ServerThread(client0, 0)
            clients.put(0,thread0)
            thread0.start()
            println("main: 客户端0连接成功，等待连接客户端1...")
            val client1 = serverSocket.accept()
            val thread1 = ServerThread(client1, 1)
            clients.put(1,thread1)
            thread1.start()
            println("Main: 客户端1连接成功！")
            sleep(500)
            for (x in 0..<clients.size) {
                val client = clients[x]
                client!!.sendMap()
                client.sendId(x)
            }
            while (!isGameOpen) {
                sleep(500)
            }
            println("main: 游戏已开始")
            while (!gameEnd) {
                nextTurn()
                for (x in 0..<clients.size) {
                    val client = clients[x]
                    client!!.sendMap()
                }
                for (x in 0..<clients.size) {
                    val client = clients[x]
                    client!!.sendTurn(turnCount)
                }
            }
        }
    }

    @Synchronized
    fun nextTurn() {
        println("turn:" + turnCount)
        turnCount++
        for (x in 0..19) {
            for (y in 0..19) {
                val grid = grids[x][y]
                if (grid is King) {
                    grid.power++
                } else if (grid is Tower && grid.player != SystemPlayer) {
                    grid.power++
                }
                if (turnCount % 25 == 0 && !(grid is Tower)) {
                    grid.power++
                }
                grids[x][y] = grid
            }
            try {
                for (m in moveList) {
                    if (grids[m.originX][m.originY].power == 1) {
                        moveList.remove(m)
                        continue
                    }
                    var endX : Int = m.originX
                    var endY : Int = m.originY
                    when (m.type) {
                        1 -> endY++
                        2 -> endX--
                        3 -> endY--
                        4 -> endX++
                    }
                    if (grids[endX][endY] is Blank) {
                        // 空地
                        grids[endX][endY] = Grid(grids[m.originX][m.originY].player,grids[m.originX][m.originY].power-1,endX,endY)
                        grids[m.originX][m.originY].power = 1
                    } else if (grids[endX][endY] is Tower) {
                        if (grids[endX][endY] is King) {
                            // 自家King
                            if (grids[endX][endY].player.name == m.playerName) {
                                val endPrePower = grids[endX][endY].power
                                val oriPrePower = grids[m.originX][m.originY].power
                                grids[endX][endY] = King(grids[m.originX][m.originY].player,endPrePower + oriPrePower - 1,endX,endY)
                                grids[m.originX][m.originY].power = 1
                            } else {
                                // 敌人的King
                                val endPrePower = grids[endX][endY].power
                                val oriPrePower = grids[m.originX][m.originY].power
                                if (endPrePower >= oriPrePower) {
                                    grids[endX][endY].power -= (oriPrePower-1)
                                    grids[m.originX][m.originY].power = 1
                                } else {
                                    val endNowPower = oriPrePower-1-endPrePower
                                    val winner = grids[m.originX][m.originY].player
                                    val loser = grids[endX][endY].player
                                    clients.get(winner.id)?.sendString("win")
                                    clients.get(loser.id)?.sendString("lose")
                                    grids[m.originX][m.originY].power = 1
                                    grids[endX][endY] = Tower(grids[m.originX][m.originY].player,endNowPower,endX,endY)
                                    gameEnd = true
                                }
                            }
                        } else {
                            // 自家塔
                            if (grids[endX][endY].player.name == m.playerName) {
                                val endPrePower = grids[endX][endY].power
                                val oriPrePower = grids[m.originX][m.originY].power
                                grids[endX][endY].power = endPrePower + oriPrePower - 1
                                grids[m.originX][m.originY].power = 1
                            } else {
                                // 野塔及其他人占领的塔
                                val endPrePower = grids[endX][endY].power
                                val oriPrePower = grids[m.originX][m.originY].power
                                if (endPrePower >= oriPrePower) {
                                    grids[endX][endY].power -= (oriPrePower-1)
                                    grids[m.originX][m.originY].power = 1
                                } else {
                                    val endNowPower = oriPrePower-1-endPrePower
                                    grids[m.originX][m.originY].power = 1
                                    grids[endX][endY] = Tower(grids[m.originX][m.originY].player,endNowPower,endX,endY)
                                }
                            }
                        }
                    } else if (grids[endX][endY] !is Mountain) {
                        if (grids[endX][endY].player.name == m.playerName) {
                            // 自己的格子
                            val endPrePower = grids[endX][endY].power
                            val oriPrePower = grids[m.originX][m.originY].power
                            grids[endX][endY].power = endPrePower + oriPrePower - 1
                            grids[m.originX][m.originY].power = 1
                        } else {
                            // 敌人的格子
                            val endPrePower = grids[endX][endY].power
                            val oriPrePower = grids[m.originX][m.originY].power
                            if (endPrePower >= oriPrePower) {
                                grids[endX][endY].power -= (oriPrePower-1)
                                grids[m.originX][m.originY].power = 1
                            } else {
                                val endNowPower = oriPrePower-1-endPrePower
                                grids[m.originX][m.originY].power = 1
                                grids[endX][endY] = Grid(grids[m.originX][m.originY].player,endNowPower,endX,endY)
                            }
                        }
                    }
                    // Mountain 无需判断
                    moveList.remove(m)
                }
            } catch (e: RuntimeException) {
                e.printStackTrace()
            }
        }
        sleep(1000)
    }
}
