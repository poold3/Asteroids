package io.github.asteroids;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.asteroids.asteroid.BigAsteroid;
import io.github.asteroids.asteroid.IAsteroid;
import io.github.asteroids.asteroid.SmallAsteroid;
import io.github.asteroids.asteroid.MediumAsteroid;
import io.github.asteroids.ship.Bullet;
import io.github.asteroids.ship.Ship;

import java.util.ArrayList;

public class GameScreen implements Screen {
    final Asteroids game;
    ShapeRenderer shapeRenderer;
    Ship ship;

    ArrayList<IAsteroid> asteroids = new ArrayList<>();
    static final float ASTEROID_SPAWN_TIME = 3f;
    static final int MAX_NUM_ASTEROIDS = 10;
    float asteroidTimer = 0f;

    ArrayList<Bullet> bullets = new ArrayList<>();
    static final int MAX_NUM_BULLETS = 4;

    Music background;
    Music thrusters;
    Sound blaster;
    Sound explosion;
    Sound gameOver;

    int score = 0;

    public GameScreen(final Asteroids game) {
        this.game = game;
        this.shapeRenderer = new ShapeRenderer();
        this.ship = new Ship(game);

        this.blaster = this.game.assetManager.get("blaster.mp3", Sound.class);
        this.explosion = this.game.assetManager.get("explosion.mp3", Sound.class);
        this.gameOver = this.game.assetManager.get("game-over.mp3", Sound.class);

        this.thrusters = this.game.assetManager.get("thrusters.mp3", Music.class);
        this.thrusters.setLooping(true);
        this.thrusters.setVolume(.2f);

        this.background = this.game.assetManager.get("background.mp3", Music.class);
        this.background.setLooping(true);
        this.background.setVolume(.2f);
        this.background.play();
    }

    @Override
    public void render(float delta) {
        this.input();
        this.logic();
        this.draw();
    }

    private void input() {
        if (!this.ship.isAlive()) {
            return;
        }

        float delta = Gdx.graphics.getDeltaTime();
        float acceleration = Ship.ACCELERATION * delta;

        if (Gdx.input.isKeyJustPressed(Input.Keys.X) && this.bullets.size() < MAX_NUM_BULLETS) {
            this.blaster.play(.2f);
            this.bullets.add(new Bullet(this.ship));
        }

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
            if (!this.thrusters.isPlaying()) {
                this.thrusters.play();
            }

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
        } else if (this.thrusters.isPlaying()) {
            this.thrusters.pause();
        }

        FloatArray shipVertices = new FloatArray(this.ship.shipPolygon.getTransformedVertices());
        for (int i = 0; i < this.asteroids.size(); ++i) {
            IAsteroid asteroid = this.asteroids.get(i);
            // Detect collisions with asteroids
            float[] asteroidVertices = asteroid.asteroidPolygon.getTransformedVertices();
            FloatArray asteroidVerticesFloatArray = new FloatArray(asteroidVertices);
            if (this.ship.isAlive() && Intersector.intersectPolygonEdges(shipVertices, asteroidVerticesFloatArray)) {
                this.explodeShip();
                break;
            }

            // Detect collisions with bullets
            boolean collisionWithBullet = false;
            for (Bullet bullet : this.bullets) {
                if (Intersector.isPointInPolygon(asteroidVertices, 0, asteroidVertices.length, bullet.circle.x, bullet.circle.y)) {
                    this.bullets.remove(bullet);
                    collisionWithBullet = true;
                    break;
                }
            }
            if (collisionWithBullet) {
                this.explosion.play(.2f);

                // Spawn new asteroids
                if (asteroid instanceof SmallAsteroid) {
                    this.score += 15;
                } else {
                    float x = asteroid.asteroidPolygon.getX() + asteroid.asteroidPolygon.getOriginX();
                    float y = asteroid.asteroidPolygon.getY() + asteroid.asteroidPolygon.getOriginY();
                    if (asteroid instanceof BigAsteroid) {
                        this.score += 5;
                        for (int j = 0; j < 2; ++j) {
                            this.asteroids.add(new MediumAsteroid(this.game, x, y));
                        }
                    } else if (asteroid instanceof MediumAsteroid) {
                        this.score += 10;
                        for (int j = 0; j < 3; ++j) {
                            this.asteroids.add(new SmallAsteroid(this.game, x, y));
                        }
                    }
                }

                this.asteroids.remove(i);
                i -= 1;
                continue;
            }

            // Move and teleport asteroids
            asteroid.asteroidPolygon.translate(asteroid.dx, asteroid.dy);
            this.teleport(asteroid.asteroidPolygon);
        }

        // Move/Remove bullets
        for (int i = 0; i < this.bullets.size(); ++i) {
            Bullet bullet = this.bullets.get(i);
            float x = bullet.circle.x;
            float y = bullet.circle.y;
            if (x < 0f || x > this.game.viewport.getWorldWidth() || y < 0f || y > this.game.viewport.getWorldHeight()) {
                this.bullets.remove(i);
                i -= 1;
                continue;
            }
            bullet.circle.x += bullet.dx;
            bullet.circle.y += bullet.dy;
        }

        // Spawn new big asteroid if enough time has passed and there are few enough asteroids
        this.asteroidTimer += Gdx.graphics.getDeltaTime();
        if (asteroidTimer > GameScreen.ASTEROID_SPAWN_TIME && this.asteroids.size() < GameScreen.MAX_NUM_ASTEROIDS) {
            this.asteroids.add(new BigAsteroid(this.game));
            this.asteroidTimer = 0f;
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

        this.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw the bullets
        for (Bullet bullet : this.bullets) {
            this.shapeRenderer.circle(bullet.circle.x, bullet.circle.y, bullet.circle.radius);
        }

        this.shapeRenderer.end();

        // Draw the sprites
        this.game.spriteBatch.begin();

        // Draw text
        this.game.bitmapFont.draw(this.game.spriteBatch, "Score: " + this.score, 10f, 490f);
        this.game.bitmapFont.draw(this.game.spriteBatch, "Lives: " + this.ship.health, 10f, 470f);

        if (this.ship.isAlive()) {
            this.ship.shipSprite.draw(this.game.spriteBatch);
            if (this.ship.thrustersOn) {
                this.ship.thrustersSprite.draw(this.game.spriteBatch);
            }
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

    public void explodeShip() {
        this.explosion.play(.2f);
        this.ship.health -= 1;
        if (this.ship.isAlive()) {
            this.asteroids.clear();
            this.asteroidTimer = 0f;
        } else {
            this.background.pause();
            this.gameOver.play(1.5f);
        }

        this.ship.shipSprite.setCenter(this.game.viewport.getWorldWidth() / 2, this.game.viewport.getWorldHeight() / 2);
        this.ship.shipSprite.setRotation(0f);
        this.ship.dx = 0f;
        this.ship.dy = 0f;
        this.ship.thrustersOn = false;
        this.ship.shipPolygon.setPosition(this.ship.shipSprite.getX(), this.ship.shipSprite.getY());
        this.ship.shipPolygon.setRotation(0f);
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
        this.background.dispose();
        this.thrusters.dispose();
        this.blaster.dispose();
        this.explosion.dispose();
        this.gameOver.dispose();
    }

}
