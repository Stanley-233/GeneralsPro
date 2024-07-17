package top.bearingwall.game

import top.bearingwall.game.data.GameMap
import top.bearingwall.game.data.Player
import top.bearingwall.game.net.ClientThread

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
}
