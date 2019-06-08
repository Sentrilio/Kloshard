package com.domkow.kloshard.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.domkow.kloshard.Controllers.AndroidScreenController;
import com.domkow.kloshard.KloshardGame;
import com.domkow.kloshard.Scenes.Hud;
import com.domkow.kloshard.Sprites.Enemies.Enemy;
import com.domkow.kloshard.Sprites.Items.Item;
import com.domkow.kloshard.Sprites.Items.ItemDef;
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
    private AndroidScreenController controller;
    public AssetManager manager;
    public MenuScreen menuScreen;
    private Skin skin;


    public PlayScreen(KloshardGame game, MenuScreen menuScreen) {
        this.menuScreen = menuScreen;
        this.manager = game.manager;
        this.game = game;

        gamecam = new OrthographicCamera();
        gamePort = new FitViewport(KloshardGame.V_WIDTH / KloshardGame.PPM,
                KloshardGame.V_HEIGHT / KloshardGame.PPM, gamecam);

        atlas = new TextureAtlas("textures/Kloshard_and_Enemies/Kloshard_and_Enemies/Kloshard_and_Enemies.pack");
        controller = new AndroidScreenController(game);
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
        int skinNumber = menuScreen.getKloshardSkin();
        if (skinNumber == 1 || skinNumber == 2 || skinNumber == 3) {
            player = new Kloshard(this);
        } else {
            Gdx.app.log("Kloshard skin is invalid:", skinNumber + "");
            dispose();
        }

        world.setContactListener(new WorldContactListener());

        this.skin = menuScreen.skin;
        prepareUI();
    }


    private void prepareUI() {

    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    @Override
    public void show() {

    }

    public void update(float dt) {
        handleInput(dt);
//        handleSpawningItems();


        world.step(1 / 60f, 6, 2);

        player.update(dt);
        for (Enemy enemy : creator.getEnemies()) {
            enemy.update(dt);

            if (enemy.b2body != null) {
                if (enemy.getX() < player.getX() + 5000 / KloshardGame.PPM) {
                    enemy.b2body.setActive(true);
                }
            }
        }
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
        player.draw(game.batch);

        for (Enemy enemy : creator.getEnemies()) {
            if (enemy.removeFlag) {
                creator.remove(enemy);
                Gdx.app.log("Enemy", "removed from list");
            } else {
                enemy.draw(game.batch);
            }
        }

        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.draw();

        game.batch.setProjectionMatrix(controller.stage.getCamera().combined);

        controller.stage.draw();


        if (gameOver()) {
            game.setScreen(new GameOverScreen(game, this));
        }
        if (mapFinished()) {
            game.setScreen(new MapFinishedScreen(game, this));
        }

    }

    public boolean mapFinished() {
        if (player.finishedLevel && player.getStateTimer() > 2) {
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
            return true;
        }
        return false;
    }

    private void handleInput(float dt) {
        if (!player.finishedLevel && player.currentState != Kloshard.State.DEAD) {
            //computer keyboard
//            if (!Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
//                if (player.currentState == Kloshard.State.RUNNING) {
//                }
//            }
            boolean leftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT);
            boolean rightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
            boolean upPressed = Gdx.input.isKeyPressed(Input.Keys.UP);
            if (leftPressed || rightPressed || upPressed) {
                if (upPressed) {
//                    if (player.previousState != Kloshard.State.MOVING_UP) {
//                    if (player.touchingGround && player.currentState != Kloshard.State.MOVING_UP && player.currentState!=Kloshard.State.MOVING_DOWN) {
//                    if (player.currentState != Kloshard.State.MOVING_UP && player.currentState!=Kloshard.State.MOVING_DOWN) {
//                        player.touchingGround =false;
//                        Gdx.app.log("Kloshard jump","disabled");
                    if (player.touchingGround) {
                        player.touchingGround = false;
//                        if(player.currentState!=Kloshard.State.MOVING_UP && player.currentState!=Kloshard.State.MOVING_DOWN){
                        if (player.currentState == Kloshard.State.MOVING_UP) {
                            jumpSlidingUp();
                        } else if (player.currentState == Kloshard.State.MOVING_DOWN) {
                            jumpSlidingDown();
                        } else {
                            jump();
                        }
                    }
                }
//                if (player.currentState == Kloshard.State.STANDING || player.currentState == Kloshard.State.RUNNING) {
//                    jump();
//                }

                if (rightPressed && player.b2body.getLinearVelocity().x <= 3.0) {
                    Gdx.app.log("linear velocity", player.b2body.getLinearVelocity().x + "");
                    goRight();
                }
                if (leftPressed && player.b2body.getLinearVelocity().x >= -3.0) {
                    goLeft();
                }
            } else {
                if (player.currentState == Kloshard.State.RUNNING) {
//                    Gdx.app.log("Kloshard", "stopping");
                    stop();
                }
            }
//        Gdx.app.log("Current state",player.currentState+"");


            //android controller
//            if (!Gdx.input.isTouched()) {
//                if (player.currentState == Kloshard.State.RUNNING) {
//                    stop();
//                }
//            }
//                if (controller.isUpClicked()) {
//                    if (player.currentState == Kloshard.State.STANDING || player.currentState == Kloshard.State.RUNNING) {
//                        jump();
//                    }
//                    controller.setUpClicked(false);
//                }
//                if (controller.isRightClicked() && player.b2body.getLinearVelocity().x <= 1.5) {
//                    goRight();
//                }
//                if (controller.isLeftClicked() && player.b2body.getLinearVelocity().x >= -1.5) {
//                    goLeft();
//                }
//        }
        }
    }

    private void jumpSlidingUp() {
        player.b2body.applyLinearImpulse(new Vector2(0, 4.0f), player.b2body.getWorldCenter(), true);
    }
    private void jumpSlidingDown() {
        player.b2body.applyLinearImpulse(new Vector2(0, 7.0f), player.b2body.getWorldCenter(), true);
    }


    private void stop() {
//        Gdx.app.log("b2body action", "stopping");
        player.b2body.applyLinearImpulse(new Vector2(-player.b2body.getLinearVelocity().x / 10, 0), player.b2body.getWorldCenter(), true);
//        player.b2body.setLinearVelocity(new Vector2(0, 0));
    }

    private void jump() {
        player.b2body.applyLinearImpulse(new Vector2(0, 7.0f), player.b2body.getWorldCenter(), true);
    }

    private void goRight() {
//        player.b2body.setLinearVelocity(3f, player.b2body.getLinearVelocity().y);
        player.b2body.applyLinearImpulse(new Vector2(0.60f, 0), player.b2body.getWorldCenter(), true);
    }

    private void goLeft() {
//        player.b2body.setLinearVelocity(-3f, player.b2body.getLinearVelocity().y);
        player.b2body.applyLinearImpulse(new Vector2(-0.60f, 0), player.b2body.getWorldCenter(), true);
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
