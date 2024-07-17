package top.bearingwall.game

import top.bearingwall.game.data.Player
import top.bearingwall.game.net.ClientThread

object ClientDataHandler {
    var player: Player? = null
    var isGameStarted = false
    var turnCounter = 0

    fun gameReady() {
        ClientThread.start()
        isGameStarted = true
    }
}
