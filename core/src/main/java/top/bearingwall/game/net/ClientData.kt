package top.bearingwall.game.net

import top.bearingwall.game.data.Player
import java.io.Serializable

data class ClientData(var isReady: Boolean, var clientPlayer: Player) : Serializable
