package io.github.asteroids.asteroid;

import com.badlogic.gdx.math.Polygon;
import io.github.asteroids.Asteroids;

import java.util.Random;

public abstract class IAsteroid {
    public static final float MAX_ASTEROID_SPEED = 1f;

    public final Asteroids game;
    public float[] vertices;
    public Polygon asteroidPolygon;
    public float width;
    public float height;
    public float dx;
    public float dy;
    public Random rand = new Random();

    protected IAsteroid(final Asteroids game, float width, float height) {
        this.game = game;
        this.width = width;
        this.height = height;

        // Create random speed and direction
        float speed = this.rand.nextFloat(MAX_ASTEROID_SPEED) + 0.5f;
        float direction = this.rand.nextFloat((float) (Math.PI * 2f));
        this.dx = speed * (float) Math.cos(direction);
        this.dy = speed * (float) Math.sin(direction);
    }

    protected void createAsteroidPolygonOnEdge() {
        this.asteroidPolygon = new Polygon(this.vertices);
        this.asteroidPolygon.setOrigin(this.width / 2f, this.height / 2f);

        // Set asteroid rotation
        this.asteroidPolygon.setRotation(this.rand.nextInt(360));

        // Randomly select a side to spawn from
        int side = this.rand.nextInt(4);
        if (side == 0) {
            // Top
            this.asteroidPolygon.setPosition(this.rand.nextFloat(this.game.viewport.getWorldWidth()), this.game.viewport.getWorldHeight());
        } else if (side == 1) {
            // Right
            this.asteroidPolygon.setPosition(this.game.viewport.getWorldWidth(), this.rand.nextFloat(this.game.viewport.getWorldHeight()));
        } else if (side == 2) {
            // Bottom
            this.asteroidPolygon.setPosition(this.rand.nextFloat(this.game.viewport.getWorldWidth()), 0f);
        } else {
            // Left
            this.asteroidPolygon.setPosition(0f, this.rand.nextFloat(this.game.viewport.getWorldHeight()));
        }
    }
}
