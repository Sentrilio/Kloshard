package com.domkow.kloshard.Sprites.TileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.domkow.kloshard.KloshardGame;
import com.domkow.kloshard.Scenes.Hud;
import com.domkow.kloshard.Screens.PlayScreen;
import com.domkow.kloshard.Sprites.Items.Item;
import com.domkow.kloshard.Sprites.Items.ItemDef;
import com.domkow.kloshard.Sprites.Items.Mushroom;
import com.domkow.kloshard.Sprites.Kloshard;

public class Coin extends InteractiveTileObject {
//    private static TiledMapTileSet tileSet;
//    private final int BLANK_COIN = 28;

    public Coin(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(KloshardGame.COIN_BIT);
//        fixture.setSensor(false);
        fixture.setFriction(1.0f);
        fixture.setRestitution(0.0f);
    }

    @Override
    public void onHeadHit(Kloshard kloshard) {

    }

    @Override
    public void use(Kloshard kloshard) {
        setCategoryFilter(KloshardGame.DESTROYED_BIT);
        getCell().setTile(null);
        Gdx.app.log("Coin","Collision");
        Gdx.app.log("Cell tile",getCell().toString());

//        world.destroyBody(body);
        Hud.addCoin();
    }

}
