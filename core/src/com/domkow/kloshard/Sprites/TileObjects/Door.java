package com.domkow.kloshard.Sprites.TileObjects;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.domkow.kloshard.KloshardGame;
import com.domkow.kloshard.Screens.PlayScreen;
import com.domkow.kloshard.Sprites.Kloshard;

public class Door extends InteractiveTileObjectRectangle {

    public Door(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(KloshardGame.DOOR_BIT);
    }

    @Override
    public void onHeadHit(Kloshard kloshard) {
        use(kloshard);
    }

    public void use(Kloshard mario) {
        manager.get("audio/music/mario_music.ogg", Music.class).stop();
        manager.get("audio/sounds/level_complete.mp3", Sound.class).play();
        mario.finishedLevel();
    }
}
