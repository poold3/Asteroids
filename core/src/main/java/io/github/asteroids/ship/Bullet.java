package io.github.asteroids.ship;

import com.badlogic.gdx.math.Circle;

public class Bullet {
    public static final float RADIUS = 1.5f;
    public static final float BULLET_SPEED = 10f;

    public Circle circle;
    public float dx;
    public float dy;

    public Bullet(Ship ship) {
        this.circle = new Circle(
            ship.shipSprite.getX() + ship.shipSprite.getOriginX(),
            ship.shipSprite.getY() + ship.shipSprite.getOriginY(),
            Bullet.RADIUS
            );
        float angleRadians = (float) Math.toRadians(ship.shipSprite.getRotation() + 90f);
        this.dx = Bullet.BULLET_SPEED * (float) Math.cos(angleRadians);
        this.dy = Bullet.BULLET_SPEED * (float) Math.sin(angleRadians);
    }
}
