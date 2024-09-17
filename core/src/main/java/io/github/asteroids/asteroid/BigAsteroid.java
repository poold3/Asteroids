package io.github.asteroids.asteroid;

import io.github.asteroids.Asteroids;


public class BigAsteroid extends IAsteroid {

    public BigAsteroid(final Asteroids game) {
        super(game, 95f, 94f);
        this.vertices = new float[]{
            15f, 2f,
            1f, 41f,
            20f, 83f,
            45f, 67f,
            71f, 93f,
            94f, 58f,
            83f, 16f,
            87f, 7f,
            54f, 4f,
            34f, 24f
        };
        this.createAsteroidPolygonOnEdge();
    }
}
