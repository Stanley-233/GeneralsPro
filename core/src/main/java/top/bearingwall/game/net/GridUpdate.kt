package top.bearingwall.game.net

import top.bearingwall.game.data.Grid
import java.io.Serializable

class GridUpdate(x: Int, y: Int, @JvmField val grids: List<Grid>) : Serializable {
    companion object {
        private val serialVersionUID = 1L
    }
}
