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

public class MenuScreen implements Screen {
    public static final String REPOLINK = "https://github.com/libgdx/gdx-pay";

    private Viewport viewport;
    private Stage stage;
    private Game game;
    private AssetManager manager;
    private Skin skin;
    private TextureAtlas atlas;


    public MenuScreen(Game game) {
        this.manager = ((KloshardGame) game).manager;
        this.game = game;
        viewport = new FitViewport(KloshardGame.V_WIDTH, KloshardGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((KloshardGame) game).batch);
        Gdx.input.setInputProcessor(stage);
        prepareSkin();
        prepareUI();
    }

    private void prepareSkin() {
        // A skin can be loaded via JSON or defined programmatically, either is fine. Using a skin is optional but
        // strongly
        // recommended solely for the convenience of getting a texture, region, etc as a drawable, tinted drawable, etc.
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
//        table.center();
        table.setFillParent(true);
        Button playButton = new TextButton("Play", skin);
        ((TextButton) playButton).getLabel().setFontScale(4);
//        playButton.setTransform(true);
//        playButton.setScale(4f);
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("playButton", "pressed");
                game.setScreen(new PlayScreen((KloshardGame) game));
                dispose();
            }
        });
        table.add(playButton).size(400, 150).center();

        table.row();
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
//        if (playButtonPressed) {
//            game.setScreen(new PlayScreen((KloshardGame) game));
//            dispose();
//        }
//        if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
////            KloshardGame.manager.get("audio/sounds/mariodie.wav", Sound.class).stop();
//        }
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void show() {

    }


    @Override
    public void resize(int width, int height) {

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

