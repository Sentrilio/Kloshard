package com.domkow.kloshard.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.domkow.kloshard.KloshardGame;
import com.domkow.kloshard.Sprites.Enemies.Enemy;
import com.domkow.kloshard.Sprites.Items.Item;
import com.domkow.kloshard.Sprites.Kloshard;
import com.domkow.kloshard.Sprites.TileObjects.Door;
import com.domkow.kloshard.Sprites.TileObjects.InteractiveTileObject;

public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;


        switch (cDef) {
            case KloshardGame.MARIO_HEAD_BIT | KloshardGame.BRICK_BIT:
            case KloshardGame.MARIO_HEAD_BIT | KloshardGame.COIN_BIT:
                if (fixA.getFilterData().categoryBits == KloshardGame.MARIO_HEAD_BIT) {
                    ((InteractiveTileObject) fixB.getUserData()).onHeadHit((Kloshard) fixA.getUserData());
                } else {
                    ((InteractiveTileObject) fixA.getUserData()).onHeadHit((Kloshard) fixB.getUserData());
                }
                break;
            case KloshardGame.ENEMY_HEAD_BIT | KloshardGame.MARIO_BIT:
                if (fixA.getFilterData().categoryBits == KloshardGame.ENEMY_HEAD_BIT) {
                    ((Enemy) fixA.getUserData()).hitOnHead((Kloshard) fixB.getUserData());
                } else {
                    ((Enemy) fixB.getUserData()).hitOnHead((Kloshard) fixA.getUserData());
                }
                Gdx.app.log("Collision", "enemy head");
                break;
            case KloshardGame.ENEMY_BIT | KloshardGame.OBJECT_BIT:
                if (fixA.getFilterData().categoryBits == KloshardGame.ENEMY_BIT) {
                    ((Enemy) fixA.getUserData()).reverseVelocity(true, false);
                } else {
                    ((Enemy) fixB.getUserData()).reverseVelocity(true, false);
                }
                break;
            case KloshardGame.ENEMY_BIT | KloshardGame.ENEMY_BIT:
                ((Enemy) fixA.getUserData()).onEnemyHit((Enemy) fixB.getUserData());
                ((Enemy) fixB.getUserData()).onEnemyHit((Enemy) fixA.getUserData());
                break;
            case KloshardGame.MARIO_BIT | KloshardGame.ENEMY_BIT: {
                if (fixA.getFilterData().categoryBits == KloshardGame.MARIO_BIT) {
                    ((Kloshard) fixA.getUserData()).hit((Enemy) fixB.getUserData());
                } else {
                    ((Kloshard) fixB.getUserData()).hit((Enemy) fixA.getUserData());
                }
                Gdx.app.log("Collision", "enemy");
                break;
            }
            case KloshardGame.ITEM_BIT | KloshardGame.OBJECT_BIT:
                if (fixA.getFilterData().categoryBits == KloshardGame.ITEM_BIT) {
                    ((Item) fixA.getUserData()).reverseVelocity(true, false);
                } else {
                    ((Item) fixB.getUserData()).reverseVelocity(true, false);
                }
                break;
            case KloshardGame.ITEM_BIT | KloshardGame.MARIO_BIT:
                if (fixA.getFilterData().categoryBits == KloshardGame.ITEM_BIT) {
                    ((Item) fixA.getUserData()).use((Kloshard) fixB.getUserData());
                } else {
                    ((Item) fixB.getUserData()).use((Kloshard) fixA.getUserData());
                }
                break;
            case KloshardGame.DOOR_BIT | KloshardGame.MARIO_BIT:
                if (fixA.getFilterData().categoryBits == KloshardGame.DOOR_BIT) {
                    ((Door) fixA.getUserData()).use((Kloshard) fixB.getUserData());
                } else {
                    ((Door) fixB.getUserData()).use((Kloshard) fixA.getUserData());
                }
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
