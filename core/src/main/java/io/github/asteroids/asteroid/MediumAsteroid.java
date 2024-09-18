package io.github.asteroids.asteroid;

import io.github.asteroids.Asteroids;

public class MediumAsteroid extends IAsteroid {

    public MediumAsteroid(final Asteroids game, float x, float y) {
        super(game, 64f, 64f);
        this.vertices = new float[]{
            3f, 2f,
            32f, 11f,
            60f, 4f,
            63f, 15f,
            56f, 34f,
            61f, 51f,
            53f, 61f,
            36f, 55f,
            15f, 59f,
            4f, 56f,
            10f, 43f,
            3f, 30f,
            12f, 15f
        };
        this.createAsteroidPolygonAroundPoint(x, y);
    }
}
