package top.bearingwall.game.util

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import top.bearingwall.game.ClientMain
import top.bearingwall.game.data.GameMap

object MyInputProcessor : InputProcessor {
    var x : Int = 0
    var y : Int = 0
    fun isMoveValid(type: Int) : Boolean {
        when (type) {
            1 -> if (y == 19) return false
            2 -> if (x == 0) return false
            3 -> if (y == 0) return false
            4 -> if (x == 19) return false
        }
        val selectedGrid = GameMap.grids[x][y]
        if (selectedGrid.player.name == ClientMain.playerName) return true
        else return false
    }

    override fun keyDown(keycode: Int): Boolean {
        x = ClientMain.onSelectX
        y = ClientMain.onSelectY
        when (keycode) {
            Input.Keys.W -> {
                if (isMoveValid(1)) {
                    ClientMain.onSelectY++
                    ClientDataHandler.setMove(1,x,y)
                    return true
                }
                return false
            }
            Input.Keys.A -> {
                if (isMoveValid(2)) {
                    ClientMain.onSelectX--
                    ClientDataHandler.setMove(2,x,y)
                    return true
                }
                return false
            }
            Input.Keys.S -> {
                if (isMoveValid(3)) {
                    ClientMain.onSelectY--
                    ClientDataHandler.setMove(3,x,y)
                    return true
                }
                return false
            }
            Input.Keys.D -> {
                if (isMoveValid(4)) {
                    ClientMain.onSelectX++
                    ClientDataHandler.setMove(4,x,y)
                    return true
                }
                return false
            }
        }
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button == Input.Buttons.LEFT) {
//                println("x:" + screenX + "y:" +screenY)
            ClientMain.onSelectX = screenX / 50
            ClientMain.onSelectY = (1000 - screenY) / 50
            return true
        }
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return false
    }
}
