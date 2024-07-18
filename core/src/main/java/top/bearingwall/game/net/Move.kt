package top.bearingwall.game.net

import top.bearingwall.game.data.Player
import java.io.Serializable

class Move : Serializable {
    lateinit var player: Player
    val type: Int = 0
    val originX: Int = 0
    val originY: Int = 0
}
