package top.bearingwall.game.net

import java.io.Serializable

class Move(val playerName: String, val type: Int, val originX: Int, val originY: Int) : Serializable
