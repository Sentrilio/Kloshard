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
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
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

import static com.domkow.kloshard.Utils.LoginUtil.isValidEmailAddress;
import static com.domkow.kloshard.Utils.LoginUtil.isValidPassword;

public class CreateAccScreen implements Screen {
    private Viewport viewport;
    private Stage stage;
    private Game game;
    private AssetManager manager;
    private Skin skin;
    private TextureAtlas atlas;
    private FireBaseManager fireBaseManager;
    private Dialog dialog;
    private LoginScreen parent;
    private TextField emailField;
    private TextField passwordField;
    private boolean accCreationSucessful=false;

    public CreateAccScreen(Game game, LoginScreen loginScreen) {
        this.parent = loginScreen;
        fireBaseManager = FireBaseManager.instance();
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
        Table table = new Table();
        table.defaults().pad(20);
        table.setFillParent(true);

        skin.getFont("default-font").getData().setScale(3);
        final Label emailLabel = new Label("email:", skin);
        emailLabel.setFontScale(3);

        emailField = new TextField("", skin);
        Label passwordLabel = new Label("Password:", skin);
        passwordLabel.setFontScale(3);
        passwordField = new TextField("", skin);
        passwordField.setPasswordCharacter('*');
        passwordField.setPasswordMode(true);

        table.right();
        table.padRight(480);
        table.add(emailLabel).size(200, 50);
        table.add(emailField).size(700, 100);
        table.row();
        table.add(passwordLabel).size(200, 50);
        table.add(passwordField).size(700, 100);
        table.row();

        TextButton createAccButton = new TextButton("Create Account", skin);
        createAccButton.getLabel().setFontScale(4);
        createAccButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("Create acc button", "pressed");
                String email = emailField.getText();
                String password = passwordField.getText();
                Gdx.app.log("email", email);
                Gdx.app.log("password", password);

                if (isValidEmailAddress(email) && isValidPassword(password)) {
                    fireBaseManager.createUser(email, password.toCharArray());
                } else {
                    if (!isValidEmailAddress(email)) {
                        Gdx.app.log("Create Account: email field", "invalid email");
                    }
                    if (!isValidPassword(password)) {
                        Gdx.app.log("Create Acc: password field", "password must contain at least 6 characters");
                    }
                }
            }
        });
        table.add();
        table.add(createAccButton).size(500, 100);
        table.row();

        TextButton backButton = new TextButton("Back", skin);
        backButton.getLabel().setFontScale(2.5f);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("backButton", "pressed");
                game.setScreen(parent);
            }
        });
        table.add();
        table.add(backButton).size(300, 80).padTop(100);
        table.row();

        stage.addActor(table);

        dialog = new Dialog("", skin, "dialog") {
            public void result(Object obj) {
                accCreationSucessful=true;
            }
        };
        dialog.text("Account creation success");
        dialog.button("back to login menu");
    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {

        if (fireBaseManager.accCreated) {
            fireBaseManager.accCreated=false;
            dialog.show(stage);
        }
        if(accCreationSucessful){
            game.setScreen(parent);
        }
        Gdx.gl.glClearColor(36/255f, 123/255f, 160/255f, 1);
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

