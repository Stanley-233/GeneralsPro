package top.bearingwall.game.util

import top.bearingwall.game.ClientMain
import top.bearingwall.game.data.*
import top.bearingwall.game.net.ClientThread
import top.bearingwall.game.net.Move

object ClientDataHandler {
    var player: Player? = null
    var isGameStarted = false
    var turnCounter = 0
    var id: Int = -2
    var isGameEnd = false
    var gameEndType = ""

    fun gameReady() {
        ClientThread().start()
    }

    fun calculateMap() {
        ClientMain.toDrawGridList.clear()
        if (isGameEnd) {
            for (i in 0..19) {
                for (j in 0..19) {
                    GameMap.grids[i][j]?.let { ClientMain.toDrawGridList.add(it) }
                }
            }
        } else {
            // 战争迷雾
            for (i in 0..19) {
                for (j in 0..19) {
                    val grid = GameMap.grids[i][j]
                    if (grid.player.name == player?.name) {
                        val lefti = if (i-1>=0) i-1 else 0
                        val righti = if (i+1<=19) i+1 else 19
                        val downj = if (j-1>=0) j-1 else 0
                        val upj = if (j+1<=19) j+1 else 19
                        ClientMain.toDrawGridList.add(GameMap.grids[lefti][downj])
                        ClientMain.toDrawGridList.add(GameMap.grids[i][downj])
                        ClientMain.toDrawGridList.add(GameMap.grids[righti][downj])
                        ClientMain.toDrawGridList.add(GameMap.grids[lefti][j])
                        ClientMain.toDrawGridList.add(grid)
                        ClientMain.toDrawGridList.add(GameMap.grids[righti][j])
                        ClientMain.toDrawGridList.add(GameMap.grids[lefti][upj])
                        ClientMain.toDrawGridList.add(GameMap.grids[i][upj])
                        ClientMain.toDrawGridList.add(GameMap.grids[righti][upj])
                    }
                    if (grid is Tower && grid.player == SystemPlayer) ClientMain.toDrawGridList.add(grid)
                    if (grid is Mountain) ClientMain.toDrawGridList.add(grid)
                }
            }
        }
        ClientMain.toDrawGridList.distinct()
        ClientMain.mapUpdateFlag = true
    }

    fun setMove(type: Int, x: Int, y: Int) {
        ClientMain.currentMove = Move(ClientMain.playerName,type, x, y)
        ClientThread.sendMove(ClientMain.currentMove)
    }
}
