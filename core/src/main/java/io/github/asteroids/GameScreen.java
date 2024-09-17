package io.github.asteroids;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.asteroids.asteroid.BigAsteroid;
import io.github.asteroids.asteroid.IAsteroid;
import io.github.asteroids.ship.Ship;

import java.util.ArrayList;

public class GameScreen implements Screen {
    final Asteroids game;
    ShapeRenderer shapeRenderer;
    Ship ship;

    ArrayList<IAsteroid> asteroids = new ArrayList<>();
    static final float ASTEROID_SPAWN_TIME = 3f;
    static final int MAX_NUM_ASTEROIDS = 4;
    float asteroidTimer = 0f;

    public GameScreen(final Asteroids game) {
        this.game = game;
        this.shapeRenderer = new ShapeRenderer();
        this.ship = new Ship(game);
    }

    @Override
    public void render(float delta) {
        this.input();
        this.logic();
        this.draw();
    }

    private void input() {
        float delta = Gdx.graphics.getDeltaTime();
        float acceleration = Ship.ACCELERATION * delta;

        // Rotate ship
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            float degrees = Ship.ROTATION_SPEED * delta * -1;
            this.ship.shipSprite.rotate(degrees);
            this.ship.shipPolygon.rotate(degrees);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            float degrees = Ship.ROTATION_SPEED * delta;
            this.ship.shipSprite.rotate(degrees);
            this.ship.shipPolygon.rotate(degrees);
        }

        this.ship.thrustersOn = false;
        float currentSpeed = (float) Math.sqrt(Math.pow(this.ship.dx, 2) + Math.pow(this.ship.dy, 2));
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            this.ship.thrustersOn = true;
            // Accelerate
            float angleRadians = (float) Math.toRadians(this.ship.shipSprite.getRotation() + 90f);
            float deltaX = acceleration * (float) Math.cos(angleRadians);
            float deltaY = acceleration * (float) Math.sin(angleRadians);
            float newSpeed = (float) Math.sqrt(Math.pow(this.ship.dx + deltaX, 2) + Math.pow(this.ship.dy + deltaY, 2));
            if (newSpeed <= Ship.MAX_SHIP_SPEED || newSpeed < currentSpeed) {
                // Normal acceleration or deceleration
                this.ship.dx += deltaX;
                this.ship.dy += deltaY;
            } else {
                // Adjust flight angle at max speed
                float targetDx = Ship.MAX_SHIP_SPEED * (float) Math.cos(angleRadians);
                if (Math.abs(this.ship.dx - targetDx) <= acceleration) {
                    this.ship.dx = targetDx;
                } else {
                    this.ship.dx += (this.ship.dx < targetDx ? acceleration : acceleration * -1f);
                }

                float targetDy = Ship.MAX_SHIP_SPEED * (float) Math.sin(angleRadians);
                if (Math.abs(this.ship.dy - targetDy) <= acceleration) {
                    this.ship.dy = targetDy;
                } else {
                    this.ship.dy += (this.ship.dy < targetDy ? acceleration : acceleration * -1f);
                }
            }
        } else if (this.ship.dx != 0f || this.ship.dy != 0f) {
            // Slow down
            float currentShipSpeed = (float) Math.sqrt(Math.pow(this.ship.dx, 2) + Math.pow(this.ship.dy, 2));
            float newShipSpeed = currentShipSpeed - Ship.DECELERATION;
            if (newShipSpeed < 0f) {
                newShipSpeed = 0f;
            }

            // Get currentAngle. Account for undefined slope.
            float angleRadians;
            if (this.ship.dx != 0f) {
                angleRadians = (float) Math.atan(this.ship.dy / this.ship.dx);
            } else {
                angleRadians = (float) Math.atan(Float.MAX_VALUE);
            }

            // Account for atan management of angles (-pi/2 through pi/2)
            if (this.ship.dx <= 0f) {
                angleRadians += (float) Math.PI;
            }
            this.ship.dx = newShipSpeed * (float) Math.cos(angleRadians);
            this.ship.dy = newShipSpeed * (float) Math.sin(angleRadians);
        }
    }

    private void logic() {
        // Move ship
        this.ship.shipSprite.translate(this.ship.dx, this.ship.dy);
        this.ship.shipPolygon.translate(this.ship.dx, this.ship.dy);

        // Teleport ship polygon
        if (this.teleport(this.ship.shipPolygon)) {
            this.ship.shipSprite.setPosition(this.ship.shipPolygon.getX(), this.ship.shipPolygon.getY());
        }

        // Update thrusters if needed
        if (this.ship.thrustersOn) {
            this.ship.thrustersSprite.setPosition(this.ship.shipSprite.getX(), this.ship.shipSprite.getY());
            this.ship.thrustersSprite.setRotation(this.ship.shipSprite.getRotation());
        }

        // Spawn new big asteroid if enough time has passed and there are few enough asteroids
        this.asteroidTimer += Gdx.graphics.getDeltaTime();
        if (asteroidTimer > GameScreen.ASTEROID_SPAWN_TIME && this.asteroids.size() < GameScreen.MAX_NUM_ASTEROIDS) {
            this.asteroids.add(new BigAsteroid(this.game));
            this.asteroidTimer = 0f;
        }

        // Move and teleport asteroids
        for (IAsteroid asteroid : this.asteroids) {
            asteroid.asteroidPolygon.translate(asteroid.dx, asteroid.dy);
            this.teleport(asteroid.asteroidPolygon);
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        this.game.viewport.apply();
        this.game.spriteBatch.setProjectionMatrix(this.game.viewport.getCamera().combined);


        this.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        this.shapeRenderer.setColor(Color.WHITE);

        // Draw the border box
        this.shapeRenderer.rect(1f, 1f, this.game.viewport.getWorldWidth() - 1, this.game.viewport.getWorldHeight() - 1);

        // Draw the asteroids
        for (IAsteroid asteroid : this.asteroids) {
            this.shapeRenderer.polygon(asteroid.asteroidPolygon.getTransformedVertices());
        }

        this.shapeRenderer.end();

        // Draw the sprites
        this.game.spriteBatch.begin();

        this.ship.shipSprite.draw(this.game.spriteBatch);
        if (this.ship.thrustersOn) {
            this.ship.thrustersSprite.draw(this.game.spriteBatch);
        }

        this.game.spriteBatch.end();
    }

    public boolean teleport(Polygon poly) {
        float x = poly.getX();
        float y = poly.getY();
        float xOrigin = poly.getOriginX();
        float yOrigin = poly.getOriginY();
        float xCenter = x + xOrigin;
        float yCenter = y + yOrigin;
        float worldWidth = this.game.viewport.getWorldWidth();
        float worldHeight = this.game.viewport.getWorldHeight();
        boolean teleports = false;

        if (xCenter < 0f) {
            poly.setPosition(worldWidth - xOrigin, y);
            teleports = true;
        } else if (xCenter > worldWidth) {
            poly.setPosition(0f - xOrigin, y);
            teleports = true;
        }

        if (yCenter < 0f) {
            poly.setPosition(x, worldHeight - yOrigin);
            teleports = true;
        } else if (yCenter > worldHeight) {
            poly.setPosition(x, 0f - yOrigin);
            teleports = true;
        }

        return teleports;
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        this.shapeRenderer.dispose();
    }

}
