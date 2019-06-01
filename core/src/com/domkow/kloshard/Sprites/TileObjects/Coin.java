package com.domkow.kloshard.Sprites.TileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapObject;
import com.domkow.kloshard.KloshardGame;
import com.domkow.kloshard.Scenes.Hud;
import com.domkow.kloshard.Screens.PlayScreen;
import com.domkow.kloshard.Sprites.Kloshard;

public class Coin extends InteractiveTileObjectCircle {

    public Coin(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setSensor(true);
        fixture.setUserData(this);
        setCategoryFilter(KloshardGame.COIN_BIT);
    }

    @Override
    public void onHeadHit(Kloshard kloshard) {

    }

    @Override
    public void use(Kloshard kloshard) {
        getCell().setTile(null);
        setCategoryFilter(KloshardGame.DESTROYED_BIT);
//        destroy();
        Gdx.app.log("Coin", "Collision");
        Hud.addCoin();
    }


}
