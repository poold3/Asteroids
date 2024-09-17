package io.github.asteroids;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;


public class Asteroids extends Game {

    public SpriteBatch spriteBatch;
    public BitmapFont bitmapFont;
    public AssetManager assetManager;
    public FitViewport viewport;

    public void create() {
        this.spriteBatch = new SpriteBatch();
        this.bitmapFont = new BitmapFont(); // use libGDX's default Arial font
        this.assetManager = new AssetManager();
        this.viewport = new FitViewport(800, 500);


        // Load assets
        this.assetManager.load("ship.png", Texture.class);
        this.assetManager.load("ship-thrusters.png", Texture.class);
        this.assetManager.finishLoading();

        // Set the game screen
        this.setScreen(new GameScreen(this));
    }

    public void render() {
        super.render(); // important!
    }

    public void dispose() {
        this.spriteBatch.dispose();
        this.bitmapFont.dispose();
        this.assetManager.dispose();
    }

    @Override
    public void resize (int width, int height) {
        this.viewport.update(width, height, true);
    }
}
