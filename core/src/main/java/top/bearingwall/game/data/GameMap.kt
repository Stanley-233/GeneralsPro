package top.bearingwall.game.data

import java.io.Serializable
import kotlin.random.Random

object GameMap : Serializable {
    private fun readResolve(): Any = GameMap
    val gameMap: Array<Array<Grid>> = Array(18) { Array(18) { Grid(null, 0, it, it) } }

    fun createMap(playerList: List<Player>) {
        for (i in gameMap.indices) {
            for (j in gameMap[i].indices) {
                gameMap[i][j] = Blank(i,j)
            }
        }
        var randomNumbers = List(60) { Random.nextInt(0, 18) }
        for (i in 0..29) {
            val index1 = randomNumbers.listIterator().nextIndex()
            val index2 = randomNumbers.listIterator().nextIndex()
            gameMap[index1][index2] = Mountain(index1,index2)
        }
        randomNumbers = List(20) { Random.nextInt(0, 18) }
        for (i in 0..9) {
            val index1 = randomNumbers.listIterator().nextIndex()
            val index2 = randomNumbers.listIterator().nextIndex()
            gameMap[index1][index2] = Tower(SystemPlayer,Random.nextInt(20,50),index1,index2)
        }
        randomNumbers = List(playerList.size) { Random.nextInt(0, 18) }
        for (i in 0..playerList.size-1) {
            val index1 = randomNumbers.listIterator().nextIndex()
            val index2 = randomNumbers.listIterator().nextIndex()
            gameMap[index1][index2] = King(playerList.get(i),1,index1,index2)
        }
    }
}
