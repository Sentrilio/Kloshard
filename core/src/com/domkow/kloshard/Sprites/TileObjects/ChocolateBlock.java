package com.domkow.kloshard.Sprites.TileObjects;

import com.badlogic.gdx.maps.MapObject;
import com.domkow.kloshard.KloshardGame;
import com.domkow.kloshard.Screens.PlayScreen;
import com.domkow.kloshard.Sprites.Kloshard;

public class ChocolateBlock extends InteractiveTileObjectRectangle {

    public ChocolateBlock(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(KloshardGame.OBJECT_BIT);
    }

    @Override
    public void onHeadHit(Kloshard kloshard) {

    }

    @Override
    public void use(Kloshard kloshard) {

    }
}
