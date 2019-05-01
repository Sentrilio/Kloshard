package com.domkow.kloshard.Sprites.TileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.domkow.kloshard.KloshardGame;
import com.domkow.kloshard.Scenes.Hud;
import com.domkow.kloshard.Screens.PlayScreen;
import com.domkow.kloshard.Sprites.Items.ItemDef;
import com.domkow.kloshard.Sprites.Items.Mushroom;
import com.domkow.kloshard.Sprites.Kloshard;

public class Coin extends InteractiveTileObject {
    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28;

    public Coin(PlayScreen screen, MapObject object) {
        super(screen, object);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(KloshardGame.COIN_BIT);
    }

    @Override
    public void onHeadHit(Kloshard mario) {
        Gdx.app.log("Coin", "Collision");
        if (getCell().getTile().getId() == BLANK_COIN) {
            KloshardGame.manager.get("audio/sounds/bump.wav", Sound.class).play();
        } else {
            if (object.getProperties().containsKey("mushroom")) {
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / KloshardGame.PPM)
                        , Mushroom.class));
                KloshardGame.manager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();
            } else {
                KloshardGame.manager.get("audio/sounds/coin.wav", Sound.class).play();
                Hud.addCoin();
                Hud.addScore(200);
            }
        }
        getCell().setTile(tileSet.getTile(BLANK_COIN));

    }
}
