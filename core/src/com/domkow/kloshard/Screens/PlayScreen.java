package com.domkow.kloshard.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.domkow.kloshard.Controllers.AndroidController;
import com.domkow.kloshard.KloshardGame;
import com.domkow.kloshard.Scenes.Hud;
import com.domkow.kloshard.Sprites.Enemies.Enemy;
import com.domkow.kloshard.Sprites.Items.Item;
import com.domkow.kloshard.Sprites.Items.ItemDef;
import com.domkow.kloshard.Sprites.Items.Mushroom;
import com.domkow.kloshard.Sprites.Kloshard;
import com.domkow.kloshard.Tools.B2WorldCreator;
import com.domkow.kloshard.Tools.WorldContactListener;

import java.util.concurrent.LinkedBlockingQueue;

public class PlayScreen implements Screen {
    private KloshardGame game;

    private TextureAtlas atlas;
    public OrthographicCamera gamecam;
    private Viewport gamePort;
    private Hud hud;

    //Tiled map variables
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    //Box2d variables
    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;

    private Kloshard player;
    private Music music;
    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;
    private AndroidController controller;


    public PlayScreen(KloshardGame game) {
        atlas = new TextureAtlas("textures/Kloshard_and_Enemies/Kloshard_and_Enemies/Kloshard_and_Enemies.pack");
//        atlas = new TextureAtlas()
        this.game = game;
        gamecam = new OrthographicCamera();
        gamePort = new FitViewport(KloshardGame.V_WIDTH / KloshardGame.PPM,
                KloshardGame.V_HEIGHT / KloshardGame.PPM, gamecam);
        controller = new AndroidController(game);

        hud = new Hud(game.batch);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("lvl1.tmx");

        renderer = new OrthogonalTiledMapRenderer(map, 1 / KloshardGame.PPM);

        //setting camera to be centered correctly at the start of map
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0, -10), true);
        b2dr = new Box2DDebugRenderer();
//        b2dr.setDrawBodies(false);
        creator = new B2WorldCreator(this);
        player = new Kloshard(this);

        world.setContactListener(new WorldContactListener());
//        music = KloshardGame.manager.get("audio/music/mario_music.ogg", Music.class);
//        music.setLooping(true);
//        music.play();
        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();
    }


    public void spawnItem(ItemDef idef) {
        itemsToSpawn.add(idef);
    }

    public void handleSpawningItems() {
        if (!itemsToSpawn.isEmpty()) {
            ItemDef idef = itemsToSpawn.poll();
            if (idef.type == Mushroom.class) {
                items.add(new Mushroom(this, idef.position.x, idef.position.y));
            }
        }
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    @Override
    public void show() {

    }

    public void update(float dt) {
        handleInput(dt);
        handleSpawningItems();

        world.step(1 / 60f, 6, 2);

        player.update(dt);
//        for (Enemy enemy : creator.getEnemies()) {
//            enemy.update(dt);
//            if (enemy.getX() < player.getX() + 224 / KloshardGame.PPM) {
//                enemy.b2body.setActive(true);
//            }
//        }

//        for (Item item : items) {
//            item.update(dt);
//        }

        hud.update(dt);
        if (player.currentState != Kloshard.State.DEAD) {
            if (player.b2body.getPosition().x >= gamecam.position.x) {
                gamecam.position.x = player.b2body.getPosition().x;
            }
        }

        gamecam.update();
        renderer.setView(gamecam);

    }


    @Override
    public void render(float delta) {

        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //render our game map
        renderer.render();

        //renderer our Box2DDebugLines

        b2dr.render(world, gamecam.combined);

        game.batch.setProjectionMatrix(gamecam.combined);

        game.batch.begin();
//        if (!(player.finishedLevel && player.getStateTimer() > 1)) {
            player.draw(game.batch);
//        }
//        for (Enemy enemy : creator.getEnemies()) {
//            enemy.draw(game.batch);
//        }
        for (Item item : items) {
            item.draw(game.batch);
        }

        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.draw();

        game.batch.setProjectionMatrix(controller.stage.getCamera().combined);

        controller.stage.draw();


        if (gameOver()) {
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
        if (mapFinished()) {
            game.setScreen(new GameOverScreen(game));
            dispose();
        }


    }

    public boolean mapFinished() {
        if (player.finishedLevel && player.getStateTimer() > 6) {
            Gdx.app.log("game", "finished");
            return true;
        }
        return false;
    }

    public boolean gameOver() {

        if (player.currentState == Kloshard.State.DEAD && player.deadFromCollision && player.getStateTimer() > 3) {
            return true;
        } else if (player.currentState == Kloshard.State.DEAD && player.fell && player.getStateTimer() > 0.5) {
            return true;
        } else if (Hud.getWorldTimer() <= 0) {
            KloshardGame.manager.get("audio/music/mario_music.ogg", Music.class).stop();
            KloshardGame.manager.get("audio/sounds/mariodie.wav", Sound.class).play();
            return true;
        }
        return false;
    }

    private void handleInput(float dt) {
        if (!player.finishedLevel && player.currentState != Kloshard.State.DEAD) {
            //computer keyboard
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                if (player.currentState == Kloshard.State.STANDING || player.currentState == Kloshard.State.RUNNING) {
                    player.b2body.applyLinearImpulse(new Vector2(0, 7.0f), player.b2body.getWorldCenter(), true);
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 1.5) {
                player.b2body.applyLinearImpulse(new Vector2(1.00f, 0), player.b2body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -1.5) {
                player.b2body.applyLinearImpulse(new Vector2(-1.00f, 0), player.b2body.getWorldCenter(), true);
            }

            //android controller
            if (controller.isUpClicked()) {
                if (player.currentState == Kloshard.State.STANDING || player.currentState == Kloshard.State.RUNNING) {
                    player.b2body.applyLinearImpulse(new Vector2(0, 3.9f), player.b2body.getWorldCenter(), true);
                }
                controller.setUpClicked(false);
            }
            if (controller.isRightClicked() && player.b2body.getLinearVelocity().x <= 1) {
                player.b2body.applyLinearImpulse(new Vector2(0.07f, 0), player.b2body.getWorldCenter(), true);
            }
            if (controller.isLeftClicked() && player.b2body.getLinearVelocity().x >= -1) {
                player.b2body.applyLinearImpulse(new Vector2(-0.07f, 0), player.b2body.getWorldCenter(), true);
            }

        }

    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
        controller.resize(width, height);
    }

    public TiledMap getMap() {
        return map;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}
