package com.domkow.kloshard.Sprites.Enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.domkow.kloshard.KloshardGame;
import com.domkow.kloshard.Scenes.Hud;
import com.domkow.kloshard.Screens.PlayScreen;
import com.domkow.kloshard.Sprites.Kloshard;

public class Slime extends Enemy {

    private float stateTime;
    private Animation walkAnimation;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean destroyed;
    public AssetManager manager;


    public Slime(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        this.manager = screen.manager;

        frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("enemies_spritesheet"), 0, 125, 51, 26));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("enemies_spritesheet"), 0, 125, 50, 28));
        walkAnimation = new Animation(0.4f, frames);
        stateTime = 0;
        setBounds(getX(), getY(), 51 / KloshardGame.PPM, 28 / KloshardGame.PPM);
        setToDestroy = false;
        destroyed = false;
    }

    public void update(float dt) {
        stateTime += dt;
        if (setToDestroy && !destroyed) {
            try {
                Gdx.app.log("SLIME", "before destroying");
                world.destroyBody(b2body);
                //without this 2 lines games crashing after setting b2body.setActive because b2body is destroyed
                b2body.setUserData(null);
                b2body = null;
                //
                Gdx.app.log("SLIME", "after destroying");
            } catch (Exception e) {
                Gdx.app.log("Exception", e.getMessage());
            }
            destroyed = true;
            setRegion(new TextureRegion(screen.getAtlas().findRegion("enemies_spritesheet"), 0, 113, 59, 12));
            stateTime = 0;
        } else if (!destroyed) {

            b2body.setLinearVelocity(velocity);
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
//            setRegion((TextureRegion) walkAnimation.getKeyFrame(stateTime, true));
            setRegion(getFrame(dt));

            //
//            Vector2 position = b2body.getPosition();
//            setRegion( (TextureRegion) walkAnimation.getKeyFrame(stateTime, true) );
//            setRotation( MathUtils.radDeg * b2body.getAngle() );
//            size = new Vector2( getRegionWidth() / KloshardGame.PPM, getRegionHeight() / KloshardGame.PPM );
//            setBounds( position.x, position.y, size.x, size.y );
//            setOriginCenter();
//            setOrigin(getRegionWidth(),getRegionHeight());
        }

    }

    public TextureRegion getFrame(float dt) {
        TextureRegion region;
        region = (TextureRegion) walkAnimation.getKeyFrame(stateTime, true);
        if (velocity.x > 0 && !region.isFlipX()) {
            region.flip(true, false);
        }
        if (velocity.x < 0 && region.isFlipX() == true) {
            region.flip(true, false);
        }
        return region;
    }

    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        Vector2[] verticy1 = new Vector2[4];
        verticy1[0] = new Vector2(-25, 5).scl(1 / KloshardGame.PPM);
        verticy1[1] = new Vector2(25, 5).scl(1 / KloshardGame.PPM);
        verticy1[2] = new Vector2(-25, -10).scl(1 / KloshardGame.PPM);
        verticy1[3] = new Vector2(25, -10).scl(1 / KloshardGame.PPM);
        shape.set(verticy1);
        fdef.filter.categoryBits = KloshardGame.ENEMY_BIT;
        fdef.filter.maskBits = KloshardGame.GROUND_BIT |
                KloshardGame.COIN_BIT |
                KloshardGame.BRICK_BIT |
                KloshardGame.ENEMY_BIT |
                KloshardGame.OBJECT_BIT |
                KloshardGame.KLOSHARD_BIT |
                KloshardGame.ENEMY_SIDE_BOX_BIT |
                KloshardGame.ENEMY_GROUND_BOX_BIT;
        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        //Create the Head here:
        PolygonShape head = new PolygonShape();
        Vector2[] verticy = new Vector2[4];
        verticy[0] = new Vector2(-20, 18).scl(1 / KloshardGame.PPM);
        verticy[1] = new Vector2(20, 18).scl(1 / KloshardGame.PPM);
        verticy[2] = new Vector2(-15, 0).scl(1 / KloshardGame.PPM);
        verticy[3] = new Vector2(15, 0).scl(1 / KloshardGame.PPM);
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
        reverseVelocity(true, false);
    }

    @Override
    public void hitOnHead(Kloshard kloshard) {
        setToDestroy = true;
//        manager.get("audio/sounds/stomp.wav", Sound.class).play();
        Hud.addScore(100);
    }
}
