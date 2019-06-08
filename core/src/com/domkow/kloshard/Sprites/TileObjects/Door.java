package com.domkow.kloshard.Sprites.TileObjects;

import com.badlogic.gdx.maps.MapObject;
import com.domkow.kloshard.KloshardGame;
import com.domkow.kloshard.Screens.PlayScreen;
import com.domkow.kloshard.Sprites.Kloshard;

public class Door extends InteractiveTileObjectRectangle {

    public Door(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setSensor(true);
        fixture.setUserData(this);
        setCategoryFilter(KloshardGame.DOOR_BIT);
    }

    @Override
    public void onHeadHit(Kloshard kloshard) {
        use(kloshard);
    }

    public void use(Kloshard kloshard) {
        kloshard.b2body.setLinearVelocity(0,kloshard.b2body.getLinearVelocity().y);
        kloshard.finishedLevel();
    }
}
