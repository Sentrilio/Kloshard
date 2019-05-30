package com.domkow.kloshard.Sprites.TileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.domkow.kloshard.KloshardGame;
import com.domkow.kloshard.Screens.PlayScreen;
import com.domkow.kloshard.Sprites.Kloshard;

public abstract class InteractiveTileObjectCircle {
    protected boolean toDestroy;
    protected boolean destroyed;
    protected World world;
    protected TiledMap map;
    protected TiledMapTile tile;
    protected Ellipse ellipse;
    protected Body body;
    protected Fixture fixture;
    protected PlayScreen screen;
    protected MapObject object;
    protected CircleShape shape;

    public InteractiveTileObjectCircle(PlayScreen screen, MapObject object) {
        this.screen = screen;
        this.world = screen.getWorld();
        this.map = screen.getMap();
        this.object = object;
        this.ellipse = ((EllipseMapObject) object).getEllipse();
        this.shape = new CircleShape();

        FixtureDef fdef = new FixtureDef();
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        shape.setPosition(new Vector2((ellipse.x + ellipse.width / 2) / KloshardGame.PPM,
                (ellipse.y + ellipse.height / 2) / KloshardGame.PPM));
//        bdef.position.set(shape.getPosition().x/4,shape.getPosition().y);
        shape.setRadius(ellipse.width / 2 / KloshardGame.PPM);

        body = world.createBody(bdef);
        fdef.shape = shape;
        fixture = body.createFixture(fdef);
        Gdx.app.log("position", body.getPosition() + "");
    }

    public abstract void onHeadHit(Kloshard kloshard);

    public abstract void use(Kloshard kloshard);

    public void setCategoryFilter(short filterBit) {
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }

    public TiledMapTileLayer.Cell getCell() {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);
        return layer.getCell((int) (shape.getPosition().x * KloshardGame.PPM / 70),
                (int) (shape.getPosition().y * KloshardGame.PPM / 70));
    }
}
