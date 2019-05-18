package com.domkow.kloshard.Sprites.TileObjects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.domkow.kloshard.KloshardGame;
import com.domkow.kloshard.Scenes.Hud;
import com.domkow.kloshard.Screens.PlayScreen;
import com.domkow.kloshard.Sprites.Kloshard;

public class Brick extends InteractiveTileObject {

    public Brick(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(KloshardGame.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Kloshard kloshard) {
//        if (mario.isBig()) {
//            setCategoryFilter(KloshardGame.DESTROYED_BIT);
//            getCell().setTile(null);
//            Hud.addScore(50);
//            KloshardGame.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
//        } else {
//            KloshardGame.manager.get("audio/sounds/bump.wav", Sound.class).play();
//        }
    }

    @Override
    public void use(Kloshard kloshard) {

    }
}
