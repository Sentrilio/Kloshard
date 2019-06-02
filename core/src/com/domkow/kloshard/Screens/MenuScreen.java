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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
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

public class MenuScreen implements Screen {
    public static final String REPOLINK = "https://github.com/libgdx/gdx-pay";
    private Viewport viewport;
    private Stage stage;
    private Game game;
    private AssetManager manager;
    private Skin skin;
    private TextureAtlas atlas;
    private int kloshardSkin = 1;

    public MenuScreen(Game game) {
        Gdx.app.log("MenuScreen","Contructor");
        this.manager = ((KloshardGame) game).manager;
        this.game = game;
        viewport = new FitViewport(KloshardGame.V_WIDTH, KloshardGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((KloshardGame) game).batch);
        Gdx.input.setInputProcessor(stage);
        prepareSkin();
        prepareUI();
    }

    private void prepareSkin() {
        skin = new Skin();
        atlas = new TextureAtlas(Gdx.files.internal("skin/uiskin.atlas"));
        skin.addRegions(atlas);
        skin.load(Gdx.files.internal("skin/uiskin.json"));
    }

    private void prepareUI() {
        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        font.font.getData().setScale(5);
        Table table = new Table();
        table.defaults().pad(50);
        table.setFillParent(true);
        //play button
        Button playButton = new TextButton("Play", skin);
        ((TextButton) playButton).getLabel().setFontScale(4);
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("playButton", "pressed");
                game.setScreen(new PlayScreen((KloshardGame) game, MenuScreen.this));
//                dispose();
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
//                dispose();
            }
        });
        table.add(preferencesButton).size(400, 150).center();
        table.row();

        //exit button
        Button exitButton = new TextButton("Exit", skin);
        ((TextButton) exitButton).getLabel().setFontScale(4);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("exitButton", "pressed");
                Gdx.app.exit();
                dispose();
            }
        });

        table.add(exitButton).size(400, 150).center();
        stage.addActor(table);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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
        stage.act();
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
    }
}

