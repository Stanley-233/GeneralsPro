package top.bearingwall.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.ScreenUtils
import top.bearingwall.game.data.*
import top.bearingwall.game.data.Player
import top.bearingwall.game.ui.InputTextFileStyle
import top.bearingwall.game.ui.StartButtonStyle


object ClientMain : ApplicationAdapter() {
    private val dataHandler = ClientDataHandler
    var toDrawGridList: ArrayList<Grid> = ArrayList()

    private lateinit var stage: Stage
    private lateinit var playerName: TextField
    private lateinit var startButton: Button
    private lateinit var font: BitmapFont

    private lateinit var batch: SpriteBatch
    private lateinit var title: Texture
    private lateinit var background: Texture
    private lateinit var king: Texture
    private lateinit var mountain: Texture
    private lateinit var tower: Texture

    var mapUpdateFlag = false

    override fun create() {
        font = BitmapFont()
        font.setColor(1f, 1f, 1f, 1f)
        font.data.setScale(1.5f)

        stage = Stage()
        Gdx.input.inputProcessor = stage
        playerName = TextField("", InputTextFileStyle)
        playerName.setSize(400f, 100f)
        playerName.setPosition(300f, 500f)
        playerName.alignment = Align.center
        playerName.messageText = "Please Enter Player Name"
        stage.addActor(playerName)
        startButton = Button(StartButtonStyle)
        startButton.setSize(400f, 100f)
        startButton.setPosition(300f, 200f)
        startButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                println("按钮被点击")
                dataHandler.player = Player(playerName.text.toString())
                dataHandler.gameReady()
                startButton.touchable = Touchable.disabled
                playerName.touchable = Touchable.disabled
            }
        })
        stage.addActor(startButton)

        batch = SpriteBatch()
        background = Texture("background.png")
        title = Texture("title.png")
        king = Texture("king.png")
        mountain = Texture("mountain.png")
        tower = Texture("tower.png")
    }

    override fun render() {
        ScreenUtils.clear(0.255f, 0.255f, 0.255f, 1f)
        if (dataHandler.isGameStarted) {
            batch.begin()
            batch.draw(background, 0f, 0f)
            batch.end()
            // TODO: Draw Map
            drawMap(mapUpdateFlag)
        } else {
            batch.begin()
            batch.draw(title, 260f, 750f)
            batch.end()
            drawMap(mapUpdateFlag)
            stage.act()
            stage.draw()
        }
    }

    fun drawMap(flag: Boolean) {
        if (flag) {
            for (grid in toDrawGridList) {
                val x: Float = grid.x * 50 + 1f
                val y: Float = grid.y * 50 + 1f
                val sr = ShapeRenderer()
                if (grid is Blank) {
                    sr.begin(ShapeRenderer.ShapeType.Filled)
                    sr.setColor(Color.LIGHT_GRAY)
                    sr.rect(x,y,48f,48f)
                    sr.end()
                } else if (grid is Mountain) {
                    batch.begin()
                    batch.draw(mountain, x, y)
                    batch.end()
                } else if (grid is King) {
                    if (grid.player.name == playerName.text) {
                        sr.begin(ShapeRenderer.ShapeType.Filled)
                        sr.setColor(Color.BLUE)
                        sr.rect(x,y,48f,48f)
                        sr.end()
                    } else {
                        sr.begin(ShapeRenderer.ShapeType.Filled)
                        sr.setColor(Color.RED)
                        sr.rect(x,y,48f,48f)
                        sr.end()
                    }
                    batch.begin()
                    batch.draw(king, x, y)
                    font.draw(batch, grid.power.toString(), x+12, y+28)
                    batch.end()
                } else if (grid is Tower) {
                    if (grid.player == SystemPlayer) {
                        sr.begin(ShapeRenderer.ShapeType.Filled)
                        sr.setColor(Color.LIGHT_GRAY)
                        sr.rect(x,y,48f,48f)
                        sr.end()
                    }
                    batch.begin()
                    batch.draw(tower, x, y)
                    font.draw(batch, grid.power.toString(), x+12, y+28)
                    batch.end()
                } else {
                    sr.begin(ShapeRenderer.ShapeType.Filled)
                    sr.setColor(Color.FOREST)
                    sr.rect(x,y,48f,48f)
                }
            }
        }
    }

    override fun dispose() {
        font.dispose()
        stage.dispose()
        batch.dispose()
        title.dispose()
        background.dispose()
        king.dispose()
        mountain.dispose()
        tower.dispose()
    }
}
