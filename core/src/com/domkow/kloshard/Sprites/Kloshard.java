package com.domkow.kloshard.Sprites;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.domkow.kloshard.KloshardGame;
import com.domkow.kloshard.Screens.PlayScreen;
import com.domkow.kloshard.Sprites.Enemies.Enemy;

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
    public AssetManager manager;

    private float stateTimer;
    private boolean runningRight;
    public boolean deadFromCollision;
    public boolean fell;
    private boolean kloshardDieSoundExecuted = false;
    public boolean finishedLevel = false;


    public Kloshard(PlayScreen screen) {
        this.manager = screen.manager;
        gamecam = screen.gamecam;
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;
        int skinNumber = screen.menuScreen.getKloshardSkin();
        prepareSkin(skinNumber, screen.getAtlas());


        defineKloshard();
        setBounds(0, 0, 66 / KloshardGame.PPM, 92 / KloshardGame.PPM);
        setRegion(kloshardStand);
    }

    private void prepareSkin(int skinNumber, TextureAtlas atlas) {
        String playerSheet = "p" + skinNumber + "_spritesheet";
        Array<TextureRegion> frames = new Array<TextureRegion>();
        //kloshard stand animation
        if (skinNumber == 1 || skinNumber == 3) {
            kloshardStand = new TextureRegion(atlas.findRegion(playerSheet), 0, 192, 66, 92);

            //kloshard dead animation
            kloshardDead = new TextureRegion(atlas.findRegion(playerSheet), 437, 0, 69, 92);

            //kloshard run animation
            frames.add(new TextureRegion(atlas.findRegion(playerSheet), 0, 0, 72, 97));
            frames.add(new TextureRegion(atlas.findRegion(playerSheet), 73, 0, 72, 97));
            frames.add(new TextureRegion(atlas.findRegion(playerSheet), 146, 0, 72, 97));
            frames.add(new TextureRegion(atlas.findRegion(playerSheet), 0, 97, 72, 97));
            frames.add(new TextureRegion(atlas.findRegion(playerSheet), 73, 97, 72, 97));
            frames.add(new TextureRegion(atlas.findRegion(playerSheet), 146, 97, 72, 97));
            kloshardRun = new Animation(0.1f, frames);
//            frames.clear();
            //kloshard jump animation
            kloshardJump = new TextureRegion(atlas.findRegion(playerSheet), 437, 92, 66, 94);

        } else if (skinNumber == 2) {
            kloshardStand = new TextureRegion(atlas.findRegion(playerSheet), 0, 192, 66, 92);

            //kloshard dead animation
            kloshardDead = new TextureRegion(atlas.findRegion(playerSheet), 425, 0, 67, 92);

            //kloshard run animation
            frames.add(new TextureRegion(atlas.findRegion(playerSheet), 0, 0, 70, 94));
            frames.add(new TextureRegion(atlas.findRegion(playerSheet), 70, 0, 70, 94));
            frames.add(new TextureRegion(atlas.findRegion(playerSheet), 140, 0, 70, 94));
            frames.add(new TextureRegion(atlas.findRegion(playerSheet), 0, 94, 70, 94));
            frames.add(new TextureRegion(atlas.findRegion(playerSheet), 70, 94, 70, 94));
            frames.add(new TextureRegion(atlas.findRegion(playerSheet), 140, 94, 70, 94));
            kloshardRun = new Animation(0.1f, frames);
//            frames.clear();
            //kloshard jump animation
            kloshardJump = new TextureRegion(atlas.findRegion(playerSheet), 422, 92, 67, 94);
        }


    }

    public void update(float dt) {

        if (b2body.getPosition().y < gamecam.position.y - gamecam.viewportHeight / 2) {
            if (!kloshardDieSoundExecuted && !deadFromCollision) {
                kloshardDieSoundExecuted = true;
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
        bdef.position.set(70 / KloshardGame.PPM, 210 / KloshardGame.PPM); //original start
//        bdef.position.set(11000 / KloshardGame.PPM, 210 / KloshardGame.PPM); //testing start
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        //kloshard circle shape
//        CircleShape shape = new CircleShape();
//        shape.setRadius(42 / KloshardGame.PPM);
        //kloshard polygon shape
        PolygonShape shape = new PolygonShape();
        Vector2[] verticy = new Vector2[4];
        verticy[0] = new Vector2(-20, 42).scl(1 / KloshardGame.PPM);
        verticy[1] = new Vector2(20, 42).scl(1 / KloshardGame.PPM);
        verticy[2] = new Vector2(-20, -42).scl(1 / KloshardGame.PPM);
        verticy[3] = new Vector2(20, -42).scl(1 / KloshardGame.PPM);
        shape.set(verticy);

        fdef.filter.categoryBits = KloshardGame.KLOSHARD_BIT;
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
        fdef.filter.categoryBits = KloshardGame.KLOSHARD_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);
    }


    public void hit(Enemy enemy) {
//        if (enemy instanceof Turtle && ((Turtle) enemy).getCurrentState() == Turtle.State.STANDING_SHELL) {
//            ((Turtle) enemy).kick(this.getX() <= enemy.getX() ? Turtle.KICK_RIGHT_SPEED : Turtle.KICK_LEFT_SPEED);
//        } else {
        deadFromCollision = true;
        Filter filter = new Filter();
        filter.maskBits = KloshardGame.NOTHING_BIT;
        for (Fixture fixture : b2body.getFixtureList()) {
            fixture.setFilterData(filter);
        }
        b2body.setLinearVelocity(new Vector2(0, 0));
        b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
//        }
    }


    public float getStateTimer() {
        return stateTimer;
    }

}