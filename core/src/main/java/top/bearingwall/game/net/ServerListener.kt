//package top.bearingwall.game.net
//
//import top.bearingwall.game.data.Player
//import top.bearingwall.game.net.ClientData
//import top.bearingwall.game.net.GridUpdate
//
//import java.io.*
//import java.net.Socket
//import java.util.*
//import javax.swing.JOptionPane
//
//class ServerListener : Thread() {
//    override fun run() {
//        try {
//            val connection = Socket("127.0.0.1", 13695)
//            val oos = ObjectOutputStream(connection.getOutputStream())
//            val ois = ObjectInputStream(connection.getInputStream())
//            val mainFrame = MainFrame.getSingleton()
//            oos.writeObject(Objects.requireNonNull(player)?.let { ClientData(true, it) })
//            println("已发送玩家信息!")
//            oos.flush()
//            while (true) {
//                JOptionPane.showMessageDialog(null, "已完成匹配！")
//                val frame = MainFrame.getSingleton()
//                frame.isVisible = true
//                val data = ois.readObject()
//                if (data is GridUpdate) {
//                    for (i in data.grids) {
//                        mainFrame.addToDraw(i)
//                    }
//                    mainFrame.repaint()
//                }
//            }
//        } catch (e: Exception) {
//            JOptionPane.showMessageDialog(null, e.localizedMessage.toString(), "错误", JOptionPane.ERROR_MESSAGE)
//        }
//    }
//}
