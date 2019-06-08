package com.domkow.kloshard.Screens;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.pay.PurchaseManager;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.domkow.kloshard.KloshardGame;
import com.domkow.kloshard.Sprites.Kloshard;
import com.domkow.kloshard.Utils.FireBaseManager;

import mk.gdx.firebase.callbacks.SignOutCallback;

public class MenuScreen implements Screen {
    public static final String REPOLINK = "https://github.com/libgdx/gdx-pay";
    private Viewport viewport;
    private Stage stage;
    public KloshardGame game;
    private AssetManager manager;
    public Skin skin;
    private TextureAtlas atlas;
    private int kloshardSkin = 1;
    private FireBaseManager fireBaseManager;
    private Screen parent;
    public Texture backgroundTexture;
    public Sprite backgroundSprite;

    public MenuScreen(Game game, Screen screen) {
        this.parent = screen;
        this.manager = ((KloshardGame) game).manager;
        this.fireBaseManager = FireBaseManager.instance();
        this.game = (KloshardGame) game;
        viewport = new FitViewport(KloshardGame.V_WIDTH, KloshardGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((KloshardGame) game).batch);
        Gdx.input.setInputProcessor(stage);
        this.skin= ((KloshardGame) game).skin;
        this.backgroundTexture = this.game.backgroundTexture;
        this.backgroundSprite = this.game.backgroundTextureRegion;
        prepareUI();
    }

    private void prepareUI() {
        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        font.font.getData().setScale(5);
        Table table = new Table();
        table.defaults().pad(20);
        table.top();
        table.setFillParent(true);

        //play button
        Button playButton = new TextButton("Play", skin);
        ((TextButton) playButton).getLabel().setFontScale(4);
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("playButton", "pressed");
                game.setScreen(new PlayScreen(game, MenuScreen.this));
            }
        });
        table.add(playButton).size(400, 150).center();
        table.row();

        //preferences button
        Button preferencesButton = new TextButton("Preferences", skin);
        ((TextButton) preferencesButton).getLabel().setFontScale(4);
        preferencesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("preferencesButton", "pressed");
                game.setScreen(new PreferencesScreen(game, MenuScreen.this));
            }
        });
        table.add(preferencesButton).size(400, 150).center();
        table.row();

        //shop button
        if (game.purchaseManager != null) {
            Button openShopButton = new TextButton("Open shop", skin);
            ((TextButton) openShopButton).getLabel().setFontScale(4);
            openShopButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.app.log("Shop Button", "pressed");
                    game.setScreen(new ShopScreen(game, MenuScreen.this));
                }
            });
            table.add(openShopButton).size(400, 150);
        } else {
            Label label = new Label("No purchase manager set.", skin);
            label.setFontScale(4);
            table.add(label);
        }
        table.row();

        //exit button
        Button exitButton = new TextButton("Logout", skin);
        ((TextButton) exitButton).getLabel().setFontScale(4);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                fireBaseManager.auth.signOut(new SignOutCallback() {
                    @Override
                    public void onSuccess() {
                        Gdx.app.log("Logout ", "successful");
                        fireBaseManager.loggedIn = false;
                        game.setScreen(parent);
                    }

                    @Override
                    public void onFail(Exception e) {
                        Gdx.app.log("Logout", "unsuccessful");

                    }
                });
            }
        });

        table.add(exitButton).size(400, 150).center();
        stage.addActor(table);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        backgroundSprite.draw(game.batch);
        game.batch.end();
        stage.draw();
    }

    public int getKloshardSkin() {
        return kloshardSkin;
    }

    public void setKloshardSkin(int kloshardSkin) {
        this.kloshardSkin = kloshardSkin;
    }

    @Override
    public void show() {
        Gdx.app.log("skin: ", kloshardSkin + "");
        Gdx.input.setInputProcessor(stage);
        stage.draw();
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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
        stage.dispose();
        skin.dispose();
        atlas.dispose();
    }
}

