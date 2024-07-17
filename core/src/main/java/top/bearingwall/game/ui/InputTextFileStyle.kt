package top.bearingwall.game.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable


object InputTextFileStyle : TextFieldStyle() {
    init {
        background = TextureRegionDrawable(TextureRegion(createBackgroundTexture()))
        // 设置光标纹理区域
        cursor = TextureRegionDrawable(TextureRegion(createCursorTexture()))
        // 设置文本框显示文本的字体来源
        font = BitmapFont()
        font.data.setScale(2.0f)
        // 设置文本框字体颜色为白色
        fontColor = Color(1f, 1f, 1f, 1f)
    }
}

fun createBackgroundTexture(): Texture {
    val pixmap = Pixmap(300, 100, Pixmap.Format.RGBA8888)
    pixmap.setColor(0.65f, 0.65f, 0.65f, 0.3f)
    pixmap.drawRectangle(0, 0, pixmap.width, pixmap.height)
    pixmap.fill()
    val texture = Texture(pixmap)
    pixmap.dispose()
    return texture
}

fun createCursorTexture(): Texture {
    val pixmap = Pixmap(1, 100 - 5, Pixmap.Format.RGBA8888)
    pixmap.setColor(1f, 1f, 1f, 1f)
    pixmap.fill()
    val texture = Texture(pixmap)
    pixmap.dispose()
    return texture
}
