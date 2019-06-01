package com.domkow.kloshard.Screens;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
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
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import mk.gdx.firebase.GdxFIRAuth;
import mk.gdx.firebase.GdxFIRDatabase;
import mk.gdx.firebase.auth.GdxFirebaseUser;
import mk.gdx.firebase.callbacks.AuthCallback;
import mk.gdx.firebase.callbacks.CompleteCallback;
import mk.gdx.firebase.callbacks.DataCallback;

public class LoginScreen implements Screen {
    private Viewport viewport;
    private Stage stage;
    private Game game;
    private AssetManager manager;
    private GdxFIRAuth auth;
    private GdxFIRDatabase db;

    public LoginScreen(Game game) {
        this.auth = GdxFIRAuth.instance();
        this.db = GdxFIRDatabase.instance();

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

//        String email = "domkow6000@gmail.com";
//        String passwordString = "6charachters";
//        char[] psswd = passwordString.toCharArray();
//        createUser(email, psswd);
//        signInUser(email, psswd);
//        HashMap<String, Object> map = new HashMap<String, Object>();
//        map.put("skin1", true);
//        map.put("skin2", false);
//        map.put("skin3", true);
//        updateUser(map);
//        getUserData();
    }

    private void getUserData() {
        db.inReference("users")
                .readValue(HashMap.class, new DataCallback<HashMap>() {
                    @Override
                    public void onData(HashMap data) {
                        Gdx.app.log("onData:", "START");
                        Gson gson = new Gson();
                        String json = gson.toJson(data);
//                        User[] users = gson.fromJson(json, User[].class);
//                        ArrayList<User> usersList = new ArrayList<User>(Arrays.asList(users));
//                        for (User user : usersList) {
//                            Gdx.app.log("User:", user.toString());
//                        }
                        Gdx.app.log("values:", json);
                        Gdx.app.log("onData:", "STOP");
                    }

                    @Override
                    public void onError(Exception e) {
                        Gdx.app.log("read database result", e.getMessage());
                    }
                });
    }

    private void updateUser(HashMap<String, Object> data) {
        Gdx.app.log("Account Creation Result", "success");
        db.inReference("users/" + auth.getCurrentUser().getUserInfo().getUid())
                .updateChildren(data, new CompleteCallback() {
                    @Override
                    public void onSuccess() {
                        Gdx.app.log("Database:", "user skin field updated");
                    }

                    @Override
                    public void onError(Exception e) {
                        Gdx.app.log("Database", e.getMessage());
                    }
                });
    }

    private void signInUser(final String email, char[] psswd) {
        auth.signInWithEmailAndPassword(email, psswd, new AuthCallback() {
            @Override
            public void onSuccess(GdxFirebaseUser user) {
                Gdx.app.log("Login result", "success");
            }

            @Override
            public void onFail(Exception e) {
                Gdx.app.log("Login result", "fail");
            }
        });
    }

    private void createUser(String email, char[] psswd) {
        auth.createUserWithEmailAndPassword(email, psswd, new AuthCallback() {
            @Override
            public void onSuccess(GdxFirebaseUser user) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("skin1", true);
                map.put("skin2", false);
                map.put("skin3", false);
                Gdx.app.log("Account Creation Result", "success");
                db.inReference("users/" + user.getUserInfo().getUid())
                        .updateChildren(map, new CompleteCallback() {
                            @Override
                            public void onSuccess() {
                                Gdx.app.log("Database:", "user skin field created");
                            }

                            @Override
                            public void onError(Exception e) {
                                Gdx.app.log("Database", e.getMessage());
                            }
                        });
            }

            @Override
            public void onFail(Exception e) {
                Gdx.app.log("Creation account result", "fail");
                Gdx.app.log("Exception", e.getMessage());
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

