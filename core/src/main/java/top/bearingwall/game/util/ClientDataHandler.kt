package top.bearingwall.game.util

import top.bearingwall.game.ClientMain
import top.bearingwall.game.data.GameMap
import top.bearingwall.game.data.Player
import top.bearingwall.game.net.ClientThread
import top.bearingwall.game.net.Move

object ClientDataHandler {
    var player: Player? = null
    var isGameStarted = false
    var turnCounter = 0

    fun gameReady() {
        ClientThread().start()
    }

    fun calculateMap() {
        ClientMain.toDrawGridList.clear()
        for (i in 0..19) {
            for (j in 0..19) {
                // TODO: 战争迷雾
                GameMap.grids[i][j]?.let { ClientMain.toDrawGridList.add(it) }
            }
        }
        ClientMain.mapUpdateFlag = true
    }

    fun setMove(type: Int, x: Int, y: Int) {
        ClientMain.currentMove = Move(type, x, y)
        ClientThread.sendMove(ClientMain.currentMove)
    }
}
