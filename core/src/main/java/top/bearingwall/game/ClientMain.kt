package top.bearingwall.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
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
import top.bearingwall.game.net.Move
import top.bearingwall.game.ui.InputTextFileStyle
import top.bearingwall.game.ui.StartButtonStyle
import top.bearingwall.game.util.ClientDataHandler
import top.bearingwall.game.util.MyInputProcessor
import java.util.*

object ClientMain : Game() {
    private val dataHandler = ClientDataHandler
    var currentMove: Move? = null
    var playerName = "Default"
    var toDrawGridList: LinkedList<Grid> = LinkedList()
    var onSelectX = 0
    var onSelectY = 0

    private lateinit var stage: Stage
    private lateinit var playerNameField: TextField
    private lateinit var startButton: Button
    private lateinit var font: BitmapFont

    private lateinit var batch: SpriteBatch
    private lateinit var sr: ShapeRenderer
    private lateinit var title: Texture
    private lateinit var background: Texture
    private lateinit var king: Texture
    private lateinit var mountain: Texture
    private lateinit var tower: Texture
    private lateinit var selection: Texture
    private lateinit var win: Texture
    private lateinit var lose: Texture
    private lateinit var replayButton: Texture

    lateinit var bgm: Music
    lateinit var readySound: Sound
    lateinit var loseSound: Sound
    lateinit var winSound: Sound
    var gameEndSoundPlayed: Boolean = false

    var mapUpdateFlag = false

    override fun create() {
        font = BitmapFont()
        font.setColor(1f, 1f, 1f, 1f)
        font.data.setScale(1.5f)

        stage = Stage()
        sr = ShapeRenderer()
        val multiplexer = InputMultiplexer()
        Gdx.input.inputProcessor = multiplexer
        multiplexer.addProcessor(stage)
        multiplexer.addProcessor(MyInputProcessor)
        playerNameField = TextField("", InputTextFileStyle)
        playerNameField.setSize(400f, 100f)
        playerNameField.setPosition(300f, 500f)
        playerNameField.alignment = Align.center
        playerNameField.messageText = "Please Enter Player Name"
        stage.addActor(playerNameField)
        startButton = Button(StartButtonStyle)
        startButton.setSize(400f, 100f)
        startButton.setPosition(300f, 200f)
        startButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                println("游戏开始按钮被点击")
                playerName = playerNameField.text
                dataHandler.player = Player(playerName,0)
                dataHandler.gameReady()
                startButton.touchable = Touchable.disabled
                playerNameField.touchable = Touchable.disabled
                playerNameField.isDisabled = true
            }
        })
        stage.addActor(startButton)

        batch = SpriteBatch()
        background = Texture("background.png")
        title = Texture("title.png")
        king = Texture("king.png")
        mountain = Texture("mountain.png")
        tower = Texture("tower.png")
        selection = Texture("selection.png")
        win = Texture("win.png")
        lose = Texture("lose.png")
        replayButton = Texture("replay_button_1.png")

        bgm = Gdx.audio.newMusic(Gdx.files.internal("bgm.ogg"))
        bgm.volume = 0.35f
        bgm.isLooping = true
        bgm.play()

        readySound = Gdx.audio.newSound(Gdx.files.internal("ready.ogg"))
        loseSound = Gdx.audio.newSound(Gdx.files.internal("lose.ogg"))
        winSound = Gdx.audio.newSound(Gdx.files.internal("win.ogg"))
    }

    override fun render() {
        ScreenUtils.clear(0.255f, 0.255f, 0.255f, 1f)
        if (dataHandler.isGameStarted) {
            if (!dataHandler.replayStarted) {
                batch.begin()
                batch.draw(background, 0f, 0f)
                batch.end()
                drawMap(mapUpdateFlag)
            } else {
                //TODO: replay
            }
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
            try {
                for (grid in toDrawGridList) {
                    val x: Float = grid.x * 50 + 1f
                    val y: Float = grid.y * 50 + 1f
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
                        if (grid.player.id == 0) {
                            sr.begin(ShapeRenderer.ShapeType.Filled)
                            sr.setColor(Color.BLUE)
                            sr.rect(x,y,48f,48f)
                            sr.end()
                        } else if (grid.player.id == 1) {
                            sr.begin(ShapeRenderer.ShapeType.Filled)
                            sr.setColor(Color.RED)
                            sr.rect(x,y,48f,48f)
                            sr.end()
                        } else if (grid.player.id == 2) {
                            sr.begin(ShapeRenderer.ShapeType.Filled)
                            sr.setColor(Color.FOREST)
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
                        } else if (grid.player.id == 0) {
                            sr.begin(ShapeRenderer.ShapeType.Filled)
                            sr.setColor(Color.BLUE)
                            sr.rect(x,y,48f,48f)
                            sr.end()
                        } else if (grid.player.id == 1) {
                            sr.begin(ShapeRenderer.ShapeType.Filled)
                            sr.setColor(Color.RED)
                            sr.rect(x,y,48f,48f)
                            sr.end()
                        } else if (grid.player.id == 2) {
                            sr.begin(ShapeRenderer.ShapeType.Filled)
                            sr.setColor(Color.FOREST)
                            sr.rect(x,y,48f,48f)
                            sr.end()
                        }
                        batch.begin()
                        batch.draw(tower, x, y)
                        font.draw(batch, grid.power.toString(), x+12, y+28)
                        batch.end()
                    } else {
                        if (grid.player.id == 0) {
                            sr.begin(ShapeRenderer.ShapeType.Filled)
                            sr.setColor(Color.BLUE)
                            sr.rect(x,y,48f,48f)
                            sr.end()
                            batch.begin()
                            font.draw(batch, grid.power.toString(), x+12, y+28)
                            batch.end()
                        } else if (grid.player.id == 1) {
                            sr.begin(ShapeRenderer.ShapeType.Filled)
                            sr.setColor(Color.RED)
                            sr.rect(x,y,48f,48f)
                            sr.end()
                            batch.begin()
                            font.draw(batch, grid.power.toString(), x+12, y+28)
                            batch.end()
                        } else if (grid.player.id == 2) {
                            sr.begin(ShapeRenderer.ShapeType.Filled)
                            sr.setColor(Color.FOREST)
                            sr.rect(x,y,48f,48f)
                            sr.end()
                            batch.begin()
                            font.draw(batch, grid.power.toString(), x+12, y+28)
                            batch.end()
                        }
                    }
                }
                // draw selection
                if (onSelectX < 0) onSelectX = 0
                if (onSelectX > 19) onSelectX = 19
                if (onSelectY < 0) onSelectY = 0
                if (onSelectY > 19) onSelectY = 19
                val x = onSelectX*50f+2
                val y = onSelectY*50f+2
                batch.begin()
                batch.draw(selection, x, y)
                batch.end()
                // if game end
                batch.begin()
                if (dataHandler.isGameEnd) {
                    playGameEndSound()
                    if (dataHandler.gameEndType == "win") {
                        batch.draw(win, 300f, 300f)
                    } else if (dataHandler.gameEndType == "lose") {
                        batch.draw(lose, 300f, 300f)
                    }
                    batch.draw(replayButton,300f,200f)
                }
                batch.end()
            } catch (e: RuntimeException) {
                System.err.println(e)
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
        selection.dispose()
        win.dispose()
        lose.dispose()
        bgm.dispose()
        readySound.dispose()
        winSound.dispose()
        loseSound.dispose()
        System.exit(0)
    }

    fun playGameEndSound() {
        if (gameEndSoundPlayed) return
        bgm.stop()
        if (dataHandler.gameEndType == "win") {
            winSound.play()
        } else {
            loseSound.play()
        }
        gameEndSoundPlayed = true
    }
}
