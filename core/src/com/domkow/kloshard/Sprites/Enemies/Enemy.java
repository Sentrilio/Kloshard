package com.domkow.kloshard.Sprites.Enemies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.domkow.kloshard.Screens.PlayScreen;
import com.domkow.kloshard.Sprites.Kloshard;

public abstract class Enemy extends Sprite {

    protected PlayScreen screen;
    protected World world;
    public Body b2body;
    public Vector2 velocity;
    protected Vector2 position;
    //
    protected Vector2 size;


    public Enemy(PlayScreen screen, float x, float y) {
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x, y);
        defineEnemy();
        velocity = new Vector2(-1, -2);
        b2body.setActive(false);
    }


    protected abstract void defineEnemy();
    public abstract void update(float dt);
    public abstract void hitOnHead(Kloshard mario);
    public abstract void onEnemyHit(Enemy enemy);

    public void reverseVelocity(boolean x, boolean y) {
        if (x) {
            velocity.x = -velocity.x;
        }
        if (y) {
            velocity.y = -velocity.y;
        }
    }

}