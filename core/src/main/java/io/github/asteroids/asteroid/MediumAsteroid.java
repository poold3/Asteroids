package io.github.asteroids.asteroid;

import io.github.asteroids.Asteroids;

public class MediumAsteroid extends IAsteroid {

    public MediumAsteroid(final Asteroids game, BigAsteroid bigAsteroid) {
        super(game, 64f, 64f);
        this.vertices = new float[]{
            4f, 2f,
            29f, 10f,
            59f, 6f,
            62f, 36f,
            51f, 31f,
            58f, 58f,
            42f, 62f,
            36f, 56f,
            18f, 48f,
            1f, 62f,
            0f, 48f,
            18f, 30f
        };
        //this.createAsteroidPolygonAroundPoint();
    }
}
