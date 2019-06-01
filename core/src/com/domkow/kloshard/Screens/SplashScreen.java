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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.domkow.kloshard.KloshardGame;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import mk.gdx.firebase.GdxFIRAnalytics;
import mk.gdx.firebase.GdxFIRApp;
import mk.gdx.firebase.GdxFIRAuth;
import mk.gdx.firebase.GdxFIRGoogleAuth;
import mk.gdx.firebase.GdxFIRLogger;
import mk.gdx.firebase.analytics.AnalyticsEvent;
import mk.gdx.firebase.analytics.AnalyticsParam;
import mk.gdx.firebase.auth.GdxFirebaseUser;
import mk.gdx.firebase.auth.UserInfo;
import mk.gdx.firebase.callbacks.AuthCallback;
import mk.gdx.firebase.functional.Consumer;

public class SplashScreen implements Screen {
    private Viewport viewport;
    private Stage stage;
    private Game game;
    private AssetManager manager;
    private GdxFIRAuth auth;

    public SplashScreen(Game game) {

        this.auth = GdxFIRAuth.instance();
        this.manager = ((KloshardGame) game).manager;
        this.game = game;
        viewport = new FitViewport(KloshardGame.V_WIDTH, KloshardGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((KloshardGame) game).batch);

        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        font.font.getData().setScale(4);
        Label playAgainLabel = new Label("Click to Play Kloshard!", font);
        table.row();
        table.add(playAgainLabel).expandX().padTop(10f);

        stage.addActor(table);

        String email = "domkow40002@gmail.com";
        String passwordString = "Julian96@";
        char[] psswd = passwordString.toCharArray();

        auth.createUserWithEmailAndPassword(email, psswd, new AuthCallback() {
            @Override
            public void onSuccess(GdxFirebaseUser user) {
                Gdx.app.log("Creation result","success");
            }

            @Override
            public void onFail(Exception e) {
                Gdx.app.log("Exception",e.getMessage());
                Gdx.app.log("Creation result","fail");

            }
        });
        auth.signInWithEmailAndPassword(email, psswd, new AuthCallback() {
            @Override
            public void onSuccess(GdxFirebaseUser user) {
                Gdx.app.log("Login result","success");
            }
            @Override
            public void onFail(Exception e) {
                Gdx.app.log("Login result","fail");
            }
        });

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            game.setScreen(new MenuScreen((KloshardGame) game));
            dispose();
        }
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
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

