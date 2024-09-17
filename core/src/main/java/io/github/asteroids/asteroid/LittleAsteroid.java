package io.github.asteroids.asteroid;

import io.github.asteroids.Asteroids;

public class LittleAsteroid extends IAsteroid {
    public LittleAsteroid(final Asteroids game) {
        super(game, 32f, 32f);
        this.vertices = new float[]{
            1f, 1f,
            11f, 7f,
            25f, 2f,
            29f, 7f,
            23f, 10f,
            29f, 16f,
            23f, 23f,
            30f, 30f,
            16f, 29f,
            4f, 23f,
            7f, 11f
        };
        //this.createAsteroidPolygonAroundPoint();
    }
}
