package com.domkow.kloshard.Sprites.TileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.domkow.kloshard.KloshardGame;
import com.domkow.kloshard.Scenes.Hud;
import com.domkow.kloshard.Screens.PlayScreen;
import com.domkow.kloshard.Sprites.Kloshard;

public class Coin extends InteractiveTileObjectCircle {

    public Coin(PlayScreen screen, MapObject object) {
        super(screen, object);
        Gdx.app.log("properties:",object.getProperties().toString());
        fixture.setSensor(true);
        fixture.setUserData(this);
        setCategoryFilter(KloshardGame.COIN_BIT);
    }

    @Override
    public void onHeadHit(Kloshard kloshard) {

    }

    @Override
    public void use(Kloshard kloshard) {
        setCategoryFilter(KloshardGame.DESTROYED_BIT);
        getCell().setTile(null);
        Gdx.app.log("Coin", "Collision");
        Gdx.app.log("Coin position", "x: "+ellipse.x+", y: "+ellipse.y);
        Gdx.app.log("Coin cell", "x: "+ellipse.x+", y: "+ellipse.y);
//        Gdx.app.log("Cell tile", getCell().toString());

//        world.destroyBody(body);
        Hud.addCoin();
    }

}
