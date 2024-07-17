package top.bearingwall.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.ScreenUtils
import top.bearingwall.game.ui.InputTextFileStyle
import top.bearingwall.game.ui.StartButtonStyle


class ClientMain : ApplicationAdapter() {
    private val isGameStarted = false
    private var batch: SpriteBatch? = null

    private var stage: Stage? = null
    private var playerName: TextField? = null
    private var startButton: Button? = null

    private var title: Texture? = null
    private var background: Texture? = null
    private var king: Texture? = null
    private var mountain: Texture? = null
    private var tower: Texture? = null

    override fun create() {
        stage = Stage()
        Gdx.input.inputProcessor = stage
        playerName = TextField("", InputTextFileStyle)
        playerName!!.setSize(400f, 100f)
        playerName!!.setPosition(300f, 500f)
        playerName!!.alignment = Align.center
        playerName!!.messageText = "Please Enter Player Name"
        stage!!.addActor(playerName)
        startButton = Button(StartButtonStyle)
        startButton!!.setSize(400f, 100f)
        startButton!!.setPosition(300f, 200f)
        startButton!!.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                println("按钮被点击")
            }
        })
        stage!!.addActor(startButton)

        batch = SpriteBatch()
        background = Texture("background.png")
        title = Texture("title.png")
        king = Texture("king.png")
        mountain = Texture("mountain.png")
        tower = Texture("tower.png")
    }

    override fun render() {
        ScreenUtils.clear(0.255f, 0.255f, 0.255f, 1f)
        if (isGameStarted) {
            batch!!.begin()
            batch!!.draw(background, 0f, 0f)
            batch!!.end()

            val sr = ShapeRenderer()
            sr.begin(ShapeRenderer.ShapeType.Filled)
            sr.color = Color.RED
            sr.rect((1 + 50 * 5).toFloat(), 1f, 48f, 48f)
            sr.end()

            batch!!.begin()
            batch!!.draw(king, 0f, 0f)
            batch!!.end()
        } else {
            batch!!.begin()
            batch!!.draw(title, 260f, 750f)
            batch!!.end()

            stage!!.act()
            stage!!.draw()
        }
    }

    override fun dispose() {
        batch!!.dispose()
        background!!.dispose()
        stage!!.dispose()
        title!!.dispose()
        mountain!!.dispose()
        tower!!.dispose()
        king!!.dispose()
    }
}
