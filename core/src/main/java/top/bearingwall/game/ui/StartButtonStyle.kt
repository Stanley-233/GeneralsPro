package top.bearingwall.game.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

object StartButtonStyle : Button.ButtonStyle() {
    init {
        val upTexture = Texture(Gdx.files.internal("assets/start_button_1.png"));
        up = TextureRegionDrawable(TextureRegion(upTexture));
        val downTexture = Texture(Gdx.files.internal("assets/start_button_2.png"));
        down = TextureRegionDrawable(TextureRegion(downTexture));
    }
}
