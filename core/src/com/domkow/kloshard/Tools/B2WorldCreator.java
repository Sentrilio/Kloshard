package com.domkow.kloshard.Tools;

import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.domkow.kloshard.KloshardGame;
import com.domkow.kloshard.Screens.PlayScreen;
import com.domkow.kloshard.Sprites.Enemies.Enemy;
import com.domkow.kloshard.Sprites.Enemies.Goomba;
import com.domkow.kloshard.Sprites.Enemies.Turtle;
import com.domkow.kloshard.Sprites.TileObjects.Brick;
import com.domkow.kloshard.Sprites.TileObjects.ChocolateBlock;
import com.domkow.kloshard.Sprites.TileObjects.Coin;
import com.domkow.kloshard.Sprites.TileObjects.Door;

public class B2WorldCreator {

    private Array<Goomba> goombas;
    private Array<Turtle> turtles;


    public B2WorldCreator(PlayScreen screen) {
        World world = screen.getWorld();
        Map map = screen.getMap();
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        //ground
        for (MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / KloshardGame.PPM, (rect.getY() + rect.getHeight() / 2) / KloshardGame.PPM);
            body = world.createBody(bdef);
            shape.setAsBox(rect.getWidth() / 2 / KloshardGame.PPM, rect.getHeight() / 2 / KloshardGame.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = KloshardGame.GROUND_BIT;
            body.createFixture(fdef);
        }
        //new coin
        for (MapObject object : map.getLayers().get(3).getObjects().getByType(EllipseMapObject.class)) {
            new Coin(screen,object);
        }

        //Coin
//        for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
//            Rectangle rect = ((RectangleMapObject) object).getRectangle();
//            bdef.type = BodyDef.BodyType.StaticBody;
//            bdef.position.set((rect.getX() + rect.getWidth() / 2) / KloshardGame.PPM, (rect.getY() + rect.getHeight() / 2) / KloshardGame.PPM);
//            body = world.createBody(bdef);
//            shape.setAsBox(rect.getWidth() / 2 / KloshardGame.PPM, rect.getHeight() / 2 / KloshardGame.PPM);
//            fdef.shape = shape;
//            fdef.filter.categoryBits = KloshardGame.OBJECT_BIT;
//            body.createFixture(fdef);
//        }

        //brick
//        for (MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
//            new Brick(screen, object);
//        }
//
//        //coins
//        for (MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
//            new Coin(screen, object);
//        }
//
//        //create all goombas
//        goombas = new Array<Goomba>();
//        for (MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)) {
//            Rectangle rect = ((RectangleMapObject) object).getRectangle();
//            goombas.add(new Goomba(screen, rect.getX() / KloshardGame.PPM, rect.getY() / KloshardGame.PPM));
//        }
//        //create all turtles
//        turtles = new Array<Turtle>();
//        for (MapObject object : map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)) {
//            Rectangle rect = ((RectangleMapObject) object).getRectangle();
//            turtles.add(new Turtle(screen, rect.getX() / KloshardGame.PPM, rect.getY() / KloshardGame.PPM));
//        }
//
//        for (MapObject object : map.getLayers().get(8).getObjects().getByType(RectangleMapObject.class)) {
//            new Door(screen, object);
//        }
//        for (MapObject object : map.getLayers().get(9).getObjects().getByType(RectangleMapObject.class)) {
//            new ChocolateBlock(screen, object);
//        }
    }

//    public Array<Enemy> getEnemies() {
//        Array<Enemy> enemies = new Array<Enemy>();
//        enemies.addAll(goombas);
//        enemies.addAll(turtles);
//        return enemies;
//    }

//    public static void removeTurtle(Turtle turtle) {
//        turtles.removeValue(turtle, true);
//    }

}
