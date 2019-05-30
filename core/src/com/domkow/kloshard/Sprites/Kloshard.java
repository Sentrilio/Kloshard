package com.domkow.kloshard.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.domkow.kloshard.KloshardGame;
import com.domkow.kloshard.Screens.PlayScreen;
import com.domkow.kloshard.Sprites.Enemies.Enemy;
import com.domkow.kloshard.Sprites.Enemies.Turtle;

public class Kloshard extends Sprite {


    public enum State {FALLING, JUMPING, STANDING, RUNNING, DEAD}

    public State currentState;
    public State previousState;
    public World world;
    public Body b2body;
    private TextureRegion kloshardStand;
    private Animation kloshardRun;
    private TextureRegion kloshardJump;
    private TextureRegion kloshardDead;
    private OrthographicCamera gamecam;

    private float stateTimer;
    private boolean runningRight;
    public boolean deadFromCollision;
    public boolean fell;
    private boolean mariodieSoundExecuted = false;
    public boolean finishedLevel = false;


    public Kloshard(PlayScreen screen) {
        gamecam = screen.gamecam;
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;
        String playerSheet = "p1_spritesheet";

        Array<TextureRegion> frames = new Array<TextureRegion>();

        //kloshard stand animation
        kloshardStand = new TextureRegion(screen.getAtlas().findRegion(playerSheet)
                , 0, 192, 66, 92);
//                , 0, 192, 66, 97);
//                kloshardStand = new TextureRegion()
        Gdx.app.log("info:", screen.getAtlas().getRegions().toString());
//        Gdx.app.log("info:", screen.getAtlas());

        //kloshard dead animation
        kloshardDead = new TextureRegion(screen.getAtlas().findRegion(playerSheet), 443, 0, 69, 92);

        frames.add(new TextureRegion(screen.getAtlas().findRegion(playerSheet), 0, 0, 73, 97));
        frames.add(new TextureRegion(screen.getAtlas().findRegion(playerSheet), 73, 0, 73, 97));
        frames.add(new TextureRegion(screen.getAtlas().findRegion(playerSheet), 146, 0, 73, 97));
        frames.add(new TextureRegion(screen.getAtlas().findRegion(playerSheet), 0, 97, 73, 97));
        frames.add(new TextureRegion(screen.getAtlas().findRegion(playerSheet), 73, 97, 73, 97));
        frames.add(new TextureRegion(screen.getAtlas().findRegion(playerSheet), 146, 97, 73, 97));

        //        for (int i = 0; i < 2; i++) {
//            for (int k = 4; k < 7; k++) {
//                if(i!=1 && k !=6){
//                    frames.add(new TextureRegion(screen.getAtlas().findRegion(playerSheet)
//                            , k * 72, i * 97, 72, 97));
//                }
//            }
//        }

        kloshardRun = new Animation(0.1f, frames);
        frames.clear();
//
        //kloshard jump animation
        kloshardJump = new TextureRegion(screen.getAtlas().findRegion(playerSheet), 437, 92, 67, 94);

        defineKloshard();
        setBounds(0, 0, 66 / KloshardGame.PPM, 92 / KloshardGame.PPM);
        setRegion(kloshardStand);
    }

    public void update(float dt) {

        if (b2body.getPosition().y < gamecam.position.y - gamecam.viewportHeight / 2) {
            if (!mariodieSoundExecuted && !deadFromCollision) {
                mariodieSoundExecuted = true;
            }
            fell = true;
        }
        if ((b2body.getPosition().x - getWidth() / 2 <= gamecam.position.x - gamecam.viewportWidth / 2)) {
            b2body.setTransform(new Vector2(gamecam.position.x - (gamecam.viewportWidth / 2) + getWidth() / 2, b2body.getPosition().y), 0);
            b2body.setLinearVelocity(new Vector2(0, b2body.getLinearVelocity().y));
        }
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);

        if (finishedLevel) {
            float barrier = 64 / KloshardGame.PPM;
            float b2bodyPosition_Y = b2body.getPosition().y;

            if (currentState == State.STANDING && b2bodyPosition_Y < barrier) {
                b2body.setActive(false);
            }
        }
        setRegion(getFrame(dt));
    }

    public void finishedLevel() {
        finishedLevel = true;
    }

    private TextureRegion getFrame(float dt) {
        currentState = getState();

        TextureRegion region;
        switch (currentState) {
            case DEAD:
                if (deadFromCollision) {
                    region = kloshardDead;
                } else {
                    region = kloshardStand;
                }
                break;
            case JUMPING:
            case FALLING:
                region = kloshardJump;
                break;
            case RUNNING:
                region = (TextureRegion) kloshardRun.getKeyFrame(stateTimer, true);
                break;

            case STANDING:
            default:
                region = kloshardStand;
                break;
        }
        if ((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        } else if ((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }
        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    private State getState() {
        if (deadFromCollision || fell) {
            return State.DEAD;
        } else if (b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING)) {
//        } else if (b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y < 0 )) {
            return State.JUMPING;
        } else if (b2body.getLinearVelocity().y < 0) {
            return State.FALLING;
        } else if (b2body.getLinearVelocity().x != 0) {
            return State.RUNNING;
        } else {
            return State.STANDING;
        }
    }

    private void defineKloshard() {
        BodyDef bdef = new BodyDef();
//        bdef.position.set(1666 / MarioBros.PPM, 16 / MarioBros.PPM);
        bdef.position.set(70 / KloshardGame.PPM, 210 / KloshardGame.PPM); //original start
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(42 / KloshardGame.PPM);
        fdef.filter.categoryBits = KloshardGame.MARIO_BIT;
        fdef.filter.maskBits = KloshardGame.GROUND_BIT |
                KloshardGame.COIN_BIT |
                KloshardGame.BRICK_BIT |
                KloshardGame.ENEMY_BIT |
                KloshardGame.OBJECT_BIT |
                KloshardGame.ENEMY_HEAD_BIT |
                KloshardGame.ITEM_BIT |
                KloshardGame.DOOR_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / KloshardGame.PPM, 6 / KloshardGame.PPM),
                new Vector2(2 / KloshardGame.PPM, 6 / KloshardGame.PPM));
        fdef.filter.categoryBits = KloshardGame.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);
    }


    public void hit(Enemy enemy) {
        if (enemy instanceof Turtle && ((Turtle) enemy).getCurrentState() == Turtle.State.STANDING_SHELL) {
            ((Turtle) enemy).kick(this.getX() <= enemy.getX() ? Turtle.KICK_RIGHT_SPEED : Turtle.KICK_LEFT_SPEED);
        } else {
            KloshardGame.manager.get("audio/music/mario_music.ogg", Music.class).stop();
            KloshardGame.manager.get("audio/sounds/mariodie.wav", Sound.class).play();
            deadFromCollision = true;
            Filter filter = new Filter();
            filter.maskBits = KloshardGame.NOTHING_BIT;
            for (Fixture fixture : b2body.getFixtureList()) {
                fixture.setFilterData(filter);
            }
            b2body.setLinearVelocity(new Vector2(0, 0));
            b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
        }
    }


    public float getStateTimer() {
        return stateTimer;
    }

}