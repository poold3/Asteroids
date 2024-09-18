package io.github.asteroids.ship;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import io.github.asteroids.Asteroids;

public class Ship {
    public static final float MAX_SHIP_SPEED = 7f;
    public static final float ACCELERATION = 4f;
    public static final float ROTATION_SPEED = 300f;
    public static final float DECELERATION = 0.04f;

    final Asteroids game;
    public Polygon shipPolygon;
    public Sprite shipSprite;
    public Sprite thrustersSprite;
    public float dx = 0f;
    public float dy = 0f;
    public boolean thrustersOn;
    public int health;

    public Ship(final Asteroids game) {
        this.game = game;
        this.thrustersOn = false;
        this.health = 3;

        // Create our ship
        this.shipSprite = new Sprite(this.game.assetManager.get("ship.png", Texture.class));
        this.shipSprite.setOrigin(this.shipSprite.getWidth() / 2f, this.shipSprite.getHeight() / 2f);
        this.shipSprite.setCenter(this.game.viewport.getWorldWidth() / 2f, this.game.viewport.getWorldHeight() / 2f);

        // Create ship thrusters
        this.thrustersSprite = new Sprite(this.game.assetManager.get("ship-thrusters.png", Texture.class));
        this.thrustersSprite.setOrigin(this.shipSprite.getWidth() / 2f, this.shipSprite.getHeight() / 2f);
        this.thrustersSprite.setCenter(this.game.viewport.getWorldWidth() / 2f, this.game.viewport.getWorldHeight() / 2f);

        // Create ship polygon
        float[] shipVertices = {
            1f, 1f,
            1f, 2f,
            15.5f, 30f,
            30f, 2f,
            30f, 1f,
            29f, 1f,
            15.5f, 8f,
            2f, 1f
        };
        this.shipPolygon = new Polygon(shipVertices);
        this.shipPolygon.setOrigin(this.shipSprite.getWidth() / 2f, this.shipSprite.getHeight() / 2f);
        this.shipPolygon.setPosition((this.game.viewport.getWorldWidth() / 2f) - (this.shipSprite.getWidth() / 2f), (this.game.viewport.getWorldHeight() / 2f) - (this.shipSprite.getHeight() / 2f));
    }

    public boolean isAlive() {
        return this.health > 0;
    }
}
