package top.bearingwall.game.net

import top.bearingwall.game.ClientMain
import java.io.Serializable

class Move(type: Int, originX: Int, originY: Int) : Serializable {
    val playerName = ClientMain.playerName
    // 1=W 2=A 3=S 4=D
}
