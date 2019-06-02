package com.domkow.kloshard.Screens;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.domkow.kloshard.KloshardGame;
import com.domkow.kloshard.Utils.FireBaseManager;
import com.domkow.kloshard.Utils.LoginUtil;

import static com.domkow.kloshard.Utils.LoginUtil.isValidEmailAddress;
import static com.domkow.kloshard.Utils.LoginUtil.isValidPassword;

public class LoginScreen implements Screen {
    private Viewport viewport;
    private Stage stage;
    private Game game;
    private AssetManager manager;
    private Skin skin;
    private TextureAtlas atlas;
    private TextField emailText;
    private TextField passwordText;
    private FireBaseManager fireBaseManager;


    public LoginScreen(Game game) {
        fireBaseManager = FireBaseManager.instance();
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
//        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
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
                String email = emailText.getText();
                String password = passwordText.getText();
                if(isValidEmailAddress(email) && isValidPassword(password)){
                    fireBaseManager.signInUser(email, password.toCharArray());
                }else{
                    if (!isValidEmailAddress(email)) {
                        Gdx.app.log("Login Acc: email field", "invalid email");
                    }
                    if (!isValidPassword(password)) {
                        Gdx.app.log("Login Acc: password field", "password must contain at least 6 characters");
                    }
                }
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

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        if (fireBaseManager.isUserLoggedIn()) {
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

