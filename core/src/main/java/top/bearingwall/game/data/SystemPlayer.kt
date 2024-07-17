package top.bearingwall.game.data

object SystemPlayer : Player("System") {
    private fun readResolve(): Any = SystemPlayer
}
