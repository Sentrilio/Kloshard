package com.domkow.kloshard.Sprites;

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


    public enum State {FALLING, JUMPING, STANDING, RUNNING, GROWING, DEAD;}

    public State currentState;
    public State previousState;
    public World world;
    public Body b2body;
    private TextureRegion marioStand;
    private Animation marioRun;
    private TextureRegion marioJump;
    private TextureRegion marioDead;
    private TextureRegion bigMarioStand;
    private TextureRegion bigMarioJump;
    private Animation bigMarioRun;
    private Animation growMario;
    private OrthographicCamera gamecam;

    private float stateTimer;
    private boolean runningRight;
    private boolean marioIsBig;
    private boolean runGrowAnimation;
    private boolean timeToDefineBigMario;
    private boolean timeToRedefineMario;
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

        Array<TextureRegion> frames = new Array<TextureRegion>();

        //mario stand animation
        marioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario")
                , 0, 0, 16, 16);
        bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32);
        //mario dead animation
        marioDead = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 96, 0, 16, 16);
        //mario run animation
        for (int i = 1; i < 4; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario")
                    , i * 16, 0, 16, 16));
        }
        marioRun = new Animation(0.1f, frames);
        frames.clear();
        for (int i = 1; i < 4; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), i * 16, 0, 16, 32));
        }
        bigMarioRun = new Animation(0.1f, frames);
        frames.clear();

        //mario jump animation
        marioJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 80, 0, 16, 16);
        bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 80, 0, 16, 32);

        //mario growing animation
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        growMario = new Animation(0.2f, frames);

        defineMario();
        setBounds(0, 0, 16 / KloshardGame.PPM, 16 / KloshardGame.PPM);
        setRegion(marioStand);
//        grow();
    }

    public void update(float dt) {

        if (b2body.getPosition().y < gamecam.position.y - gamecam.viewportHeight / 2) {
            if (!mariodieSoundExecuted && !deadFromCollision) {
                KloshardGame.manager.get("audio/music/mario_music.ogg", Music.class).stop();
                KloshardGame.manager.get("audio/sounds/mariodie.wav", Sound.class).play();
                mariodieSoundExecuted = true;
            }
            fell = true;
        }

        if ((b2body.getPosition().x - getWidth() / 2 <= gamecam.position.x - gamecam.viewportWidth / 2)) {
            b2body.setTransform(new Vector2(gamecam.position.x - (gamecam.viewportWidth / 2) + getWidth() / 2, b2body.getPosition().y), 0);
            b2body.setLinearVelocity(new Vector2(0, b2body.getLinearVelocity().y));
        }
        if (marioIsBig) {
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 - 6 / KloshardGame.PPM);
        } else {
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        }
        if (finishedLevel) {
            float barrier = 64 / KloshardGame.PPM;
            float b2bodyPosition_Y = b2body.getPosition().y;

            if (currentState == State.STANDING && b2bodyPosition_Y < barrier) {
                b2body.setActive(false);
            }

        }
        setRegion(getFrame(dt));

        if (timeToDefineBigMario) {
            defineBigMario();
        }
        if (timeToRedefineMario) {
            redefineMario();
        }

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
                    region = marioDead;
                } else {
                    region = marioStand;
                }
                break;
            case GROWING:
                region = (TextureRegion) growMario.getKeyFrame(stateTimer);
                if (growMario.isAnimationFinished(stateTimer)) {
                    runGrowAnimation = false;
                }
                break;
            case JUMPING:
                region = marioIsBig ? bigMarioJump : marioJump;
                break;
            case RUNNING:
                region = marioIsBig ? (TextureRegion) bigMarioRun.getKeyFrame(stateTimer, true) :
                        (TextureRegion) marioRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = marioIsBig ? bigMarioStand : marioStand;
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
        } else if (runGrowAnimation) {
            return State.GROWING;
        } else if (b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING)) {
            return State.JUMPING;
        } else if (b2body.getLinearVelocity().y < 0) {
            return State.FALLING;
        } else if (b2body.getLinearVelocity().x != 0) {
            return State.RUNNING;
        } else {
            return State.STANDING;
        }


    }

    private void defineMario() {
        BodyDef bdef = new BodyDef();
//        bdef.position.set(1666 / MarioBros.PPM, 16 / MarioBros.PPM);
        bdef.position.set(16 / KloshardGame.PPM, 64 / KloshardGame.PPM); //original start
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / KloshardGame.PPM);
        fdef.filter.categoryBits = KloshardGame.MARIO_BIT;
        fdef.filter.maskBits = KloshardGame.GROUND_BIT |
                KloshardGame.COIN_BIT |
                KloshardGame.BRICK_BIT |
                KloshardGame.ENEMY_BIT |
                KloshardGame.OBJECT_BIT |
                KloshardGame.ENEMY_HEAD_BIT |
                KloshardGame.ITEM_BIT |
                KloshardGame.DOOR_BIT ;

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

    private void redefineMario() {
        Vector2 position = b2body.getPosition();
        world.destroyBody(b2body);
        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / KloshardGame.PPM);
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
        timeToRedefineMario = false;
    }


    private void defineBigMario() {
        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(currentPosition.add(0, 10 / KloshardGame.PPM));
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / KloshardGame.PPM);
        fdef.filter.categoryBits = KloshardGame.MARIO_BIT;
        fdef.filter.maskBits = KloshardGame.GROUND_BIT |
                KloshardGame.COIN_BIT |
                KloshardGame.BRICK_BIT |
                KloshardGame.ENEMY_BIT |
                KloshardGame.OBJECT_BIT |
                KloshardGame.ENEMY_HEAD_BIT |
                KloshardGame.ITEM_BIT |
                KloshardGame.DOOR_BIT ;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        shape.setPosition(new Vector2(0, -14 / KloshardGame.PPM));
        b2body.createFixture(fdef).setUserData(this);
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / KloshardGame.PPM, 6 / KloshardGame.PPM),
                new Vector2(2 / KloshardGame.PPM, 6 / KloshardGame.PPM));
        fdef.filter.categoryBits = KloshardGame.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);
        timeToDefineBigMario = false;
    }

    public void grow() {
        if (!isBig()) {
            runGrowAnimation = true;
            marioIsBig = true;
            timeToDefineBigMario = true;
            setBounds(getX(), getY(), getWidth(), getHeight() * 2);
        }
        KloshardGame.manager.get("audio/sounds/powerup.wav", Sound.class).play();
    }

    public void hit(Enemy enemy) {
        if (enemy instanceof Turtle && ((Turtle) enemy).getCurrentState() == Turtle.State.STANDING_SHELL) {
            ((Turtle) enemy).kick(this.getX() <= enemy.getX() ? Turtle.KICK_RIGHT_SPEED : Turtle.KICK_LEFT_SPEED);
        } else {
            if (marioIsBig) {
                marioIsBig = false;
                timeToRedefineMario = true;
                setBounds(getX(), getY(), getWidth(), getHeight() / 2);
                KloshardGame.manager.get("audio/sounds/powerdown.wav", Sound.class).play();
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
    }


    public float getStateTimer() {
        return stateTimer;
    }

    public boolean isBig() {
        return marioIsBig;
    }
}