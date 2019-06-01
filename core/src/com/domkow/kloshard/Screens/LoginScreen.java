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
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
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
    private Skin skin;
    private TextureAtlas atlas;
    private GdxFIRAuth auth;
    private GdxFIRDatabase db;
    private TextField emailText;
    private TextField passwordText;
    private boolean loggedIn = false;


    public LoginScreen(Game game) {
        this.auth = GdxFIRAuth.instance();
        this.db = GdxFIRDatabase.instance();
        this.game = game;
        viewport = new FitViewport(KloshardGame.V_WIDTH, KloshardGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((KloshardGame) game).batch);
        Gdx.input.setInputProcessor(stage);
        prepareSkin();
        prepareUI();
//        game.setScreen(new MenuScreen(game));
//        dispose();
//        String email = "domkow7000@gmail.com";
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

    private void prepareSkin() {
        skin = new Skin();
        atlas = new TextureAtlas(Gdx.files.internal("skin/uiskin.atlas"));
        skin.addRegions(atlas);
        skin.load(Gdx.files.internal("skin/uiskin.json"));
    }

    private void prepareUI() {
        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        Table table = new Table();
//        table.setDebug(true);
        table.defaults().pad(20);
        table.setFillParent(true);

        skin.getFont("default-font").getData().setScale(3);
        Label emailLabel = new Label("email:", skin);
        emailLabel.setFontScale(3);

        emailText = new TextField("", skin);
        Label passwordLabel = new Label("Password:", skin);
        passwordLabel.setFontScale(3);
        passwordText = new TextField("", skin);

        //
        table.right();
        table.padRight(480);
        table.add(emailLabel).size(200, 50);
        table.add(emailText).size(700, 100);
        table.row();
        table.add(passwordLabel).size(200, 50);
        table.add(passwordText).size(700, 100);
        table.row();

        TextButton loginButton = new TextButton("Log in", skin);
        loginButton.getLabel().setFontScale(4);
        loginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("Log in button", "pressed");
                signInUser(emailText.getText(), passwordText.getText().toCharArray());
            }
        });
        table.add();
        table.add(loginButton).size(500, 100);
        table.row();

        TextButton createAccButton = new TextButton("Create account", skin);
        createAccButton.getLabel().setFontScale(2.5f);
        createAccButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("createAccButton", "pressed");
                game.setScreen(new CreateAccScreen(game, LoginScreen.this));
            }
        });
        table.add();
        table.add(createAccButton).size(300, 80).padTop(100);
        table.row();

        stage.addActor(table);


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
                loggedIn = true;
//                game.setScreen(new MenuScreen(game));
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
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        if (loggedIn) {
            game.setScreen(new MenuScreen(game));
        }
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
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

