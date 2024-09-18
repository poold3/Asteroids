package io.github.asteroids.asteroid;

import io.github.asteroids.Asteroids;

public class SmallAsteroid extends IAsteroid {
    public SmallAsteroid(final Asteroids game, float x, float y) {
        super(game, 32f, 32f);
        this.vertices = new float[]{
            1f, 6f,
            8f, 1f,
            27f, 3f,
            30f, 22f,
            21f, 24f,
            11f, 30f,
            2f, 24f,
            4f, 15f
        };
        this.createAsteroidPolygonAroundPoint(x, y);
    }
}
