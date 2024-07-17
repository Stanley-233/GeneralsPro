package top.bearingwall.game.data

import java.io.Serializable

open class Grid(player: Player?, power: Int, x: Int, y: Int) : Serializable {
    var owner: Player? = null
    var power: Int = 0
    val x: Int = 0
    val y: Int = 0
}
