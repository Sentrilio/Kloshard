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

public class LoginScreen implements Screen {
    public Skin skin;
    private Viewport viewport;
    private Stage stage;
    private Game game;
    private AssetManager manager;
    private TextureAtlas atlas;



    private TextField emailField;
    private TextField passwordField;
    private FireBaseManager fireBaseManager;
    private Dialog attemptToSignInDialog;
    private long start;
    private long end;
    private TextButton loginButton;
    private Dialog loginFailedDialog;
    private Dialog invalidEmailOrPsswdDialog;
    private boolean invalidEmailOrPsswd;


    public LoginScreen(Game game) {
        this.manager = ((KloshardGame) game).manager;
        fireBaseManager = FireBaseManager.instance();
        this.game = game;
        viewport = new FitViewport(KloshardGame.V_WIDTH, KloshardGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((KloshardGame) game).batch);
        Gdx.input.setInputProcessor(stage);
        this.skin = ((KloshardGame) game).skin;
        prepareUI();
    }

    private void prepareUI() {
        Table table = new Table();
//        table.setDebug(true);
        table.defaults().pad(20);
        table.top();
        table.setFillParent(true);

        skin.getFont("default-font").getData().setScale(3);
        Label emailLabel = new Label("email:", skin);
        emailLabel.setFontScale(3);

        emailField = new TextField("", skin);
        Label passwordLabel = new Label("Password:", skin);
        passwordLabel.setFontScale(3);
        passwordField = new TextField("", skin);
        passwordField.setPasswordCharacter('*');
        passwordField.setPasswordMode(true);
        //
        table.right();
        table.padRight(480);
        table.add(emailLabel).size(200, 50);
        table.add(emailField).size(700, 100);
        table.row();
        table.add(passwordLabel).size(200, 50);
        table.add(passwordField).size(700, 100);
        table.row();

        loginButton = new TextButton("Log in", skin);
        loginButton.getLabel().setFontScale(4);
        loginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("Log in button", "pressed");
                loginButton.setDisabled(true);
                FireBaseManager.attemptToSignIn = true;
                attemptToSignInDialog.show(stage);
                start = System.currentTimeMillis();
                String email = emailField.getText();
                String password = passwordField.getText();
                if (isValidEmailAddress(email) && isValidPassword(password)) {
                    fireBaseManager.signInUser(email, password.toCharArray());
                } else {
                    if (!isValidEmailAddress(email)) {
                        Gdx.app.log("Login Acc: email field", "invalid email");
                    }
                    if (!isValidPassword(password)) {
                        Gdx.app.log("Login Acc: password field", "password must contain at least 6 characters");
                    }
                    invalidEmailOrPsswd = true;
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

        TextButton exitButton = new TextButton("Exit", skin);
        exitButton.getLabel().setFontScale(2.5f);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("exitButton", "pressed");
                manager.clear();
                manager.dispose();
                Gdx.app.exit();
            }
        });
        table.add();
        table.add(exitButton).size(300, 80).padTop(40);

        stage.addActor(table);
        attemptToSignInDialog = new Dialog("", skin, "dialog");
        attemptToSignInDialog.text("Logging in...");

        loginFailedDialog = new Dialog("", skin, "dialog") {
            public void result(Object obj) {

            }
        };
        loginFailedDialog.text("Incorrect email or password ");
        loginFailedDialog.button("Try again");

        invalidEmailOrPsswdDialog = new Dialog("", skin, "dialog") {
            public void result(Object obj) {

            }
        };
        invalidEmailOrPsswdDialog.text("Invalid email or password has less than 6 characters!");
        invalidEmailOrPsswdDialog.button("Try again");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(36 / 255f, 123 / 255f, 160 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (fireBaseManager != null) {
            if (fireBaseManager.loginFail) {
                attemptToSignInDialog.hide();
                fireBaseManager.loginFail = false;
                loginButton.setDisabled(false);
                loginFailedDialog.show(stage);
            }
            if(invalidEmailOrPsswd){
                attemptToSignInDialog.hide();
                invalidEmailOrPsswd=false;
                loginButton.setDisabled(false);
                invalidEmailOrPsswdDialog.show(stage);
            }
            if (fireBaseManager.loggedIn) {
                attemptToSignInDialog.hide();
                fireBaseManager.loggedIn=false;
                game.setScreen(new MenuScreen(game, this));
            }
            end = System.currentTimeMillis();
            if (end - start > 8000) {
                start = 0;
                attemptToSignInDialog.hide();
                loginButton.setDisabled(false);
            }

        }
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

    public void setEmailFieldText(String email) {
        this.emailField.setText(email);
    }

    public void setPasswordFieldText(String password) {
        this.passwordField.setText(password);
    }
}

