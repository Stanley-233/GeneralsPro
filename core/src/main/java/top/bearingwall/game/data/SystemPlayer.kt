package top.bearingwall.game.data

object SystemPlayer : Player("System",-1) {
    private fun readResolve(): Any = SystemPlayer
}
