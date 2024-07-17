package top.bearingwall.game.net

import top.bearingwall.game.ClientDataHandler

import java.io.*
import java.net.Socket
import java.util.*
import javax.swing.JOptionPane

object ClientThread : Thread() {
    override fun run() {
        try {
            val connection = Socket("127.0.0.1", 13695)
            val oos = ObjectOutputStream(connection.getOutputStream())
            val ois = ObjectInputStream(connection.getInputStream())
            oos.writeObject(Objects.requireNonNull(ClientDataHandler.player)?.let { ClientData(true, it) })
            println("已发送玩家信息!")
            oos.flush()
        } catch (e: Exception) {
            JOptionPane.showMessageDialog(null, e.localizedMessage.toString(), "错误", JOptionPane.ERROR_MESSAGE)
        }
    }
}
