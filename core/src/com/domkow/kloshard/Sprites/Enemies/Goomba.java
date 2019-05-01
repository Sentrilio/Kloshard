package com.domkow.kloshard.Sprites.Enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.domkow.kloshard.KloshardGame;
import com.domkow.kloshard.Scenes.Hud;
import com.domkow.kloshard.Screens.PlayScreen;
import com.domkow.kloshard.Sprites.Kloshard;

public class Goomba extends Enemy {

    private float stateTime;
    private Animation walkAnimation;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean destroyed;

    public Goomba(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        for (int i = 0; i < 2; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("goomba"), i * 16, 0, 16, 16));
        }
        walkAnimation = new Animation(0.4f, frames);
        stateTime = 0;
        setBounds(getX(), getY(), 16 / KloshardGame.PPM, 16 / KloshardGame.PPM);
        setToDestroy = false;
        destroyed = false;
    }

    public void update(float dt) {
        stateTime += dt;
        if (setToDestroy && !destroyed) {
            world.destroyBody(b2body);
            destroyed = true;
            setRegion(new TextureRegion(screen.getAtlas().findRegion("goomba"), 32, 0, 16, 16));
            stateTime = 0;
        } else if (!destroyed) {
            b2body.setLinearVelocity(velocity);
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            setRegion((TextureRegion) walkAnimation.getKeyFrame(stateTime, true));
        }
    }

    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(5 / KloshardGame.PPM);
        fdef.filter.categoryBits = KloshardGame.ENEMY_BIT;
        fdef.filter.maskBits = KloshardGame.GROUND_BIT |
                KloshardGame.COIN_BIT |
                KloshardGame.BRICK_BIT |
                KloshardGame.ENEMY_BIT |
                KloshardGame.OBJECT_BIT |
                KloshardGame.MARIO_BIT;
        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        //Create the Head here:
        PolygonShape head = new PolygonShape();
        Vector2[] verticy = new Vector2[4];
        verticy[0] = new Vector2(-4, 9).scl(1 / KloshardGame.PPM);
        verticy[1] = new Vector2(4, 9).scl(1 / KloshardGame.PPM);
        verticy[2] = new Vector2(0, 3).scl(1 / KloshardGame.PPM);
        verticy[3] = new Vector2(0, 3).scl(1 / KloshardGame.PPM);
        head.set(verticy);

        fdef.shape = head;
        fdef.restitution = 0.5f;
        fdef.filter.categoryBits = KloshardGame.ENEMY_HEAD_BIT;
        b2body.createFixture(fdef).setUserData(this);
    }

    public void draw(Batch batch) {
        if (!destroyed || stateTime < 1) {
            super.draw(batch);
        }
    }

    public void onEnemyHit(Enemy enemy) {
        if (enemy instanceof Turtle && ((Turtle) enemy).currentState == Turtle.State.MOVING_SHELL) {
            setToDestroy = true;
        } else {
            reverseVelocity(true, false);
        }
    }

    @Override
    public void hitOnHead(Kloshard mario) {
        setToDestroy = true;
        KloshardGame.manager.get("audio/sounds/stomp.wav", Sound.class).play();
        Hud.addScore(100);
    }
}
