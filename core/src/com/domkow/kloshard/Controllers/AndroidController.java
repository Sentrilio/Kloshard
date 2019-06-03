package com.domkow.kloshard.Controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.domkow.kloshard.KloshardGame;

public class AndroidController {

    private int width = 180;
    private int height = 180;
    private Viewport viewport;
    public Stage stage;
    public Skin skin;
    private boolean upClicked, downClicked, leftClicked, rightClicked;

    public AndroidController(KloshardGame game) {
        this.skin = game.skin;
        viewport = new FitViewport(KloshardGame.V_WIDTH, KloshardGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, game.batch);
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setDebug(true);
        table.setFillParent(true);
        //guziki
        TextButton pauseButton = new TextButton("Options", skin);
        table.add();
        table.add();
        table.add();
        table.add();
        table.add();
        table.add(pauseButton).right().top();
        table.row();
        stage.addActor(table);

//        table.left().bottom();

        Image upImg = new Image(new Texture("buttons/up.png"));
        upImg.setSize(width, height);
        upImg.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                upClicked = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                upClicked = false;
            }
        });

        Image downImg = new Image(new Texture("buttons/down.png"));
        downImg.setSize(width, height);
        downImg.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                downClicked = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                downClicked = false;
            }
        });

        Image rightImg = new Image(new Texture("buttons/right.png"));
        rightImg.setSize(width, height);
        rightImg.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                rightClicked = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                rightClicked = false;
            }
        });

        Image leftImg = new Image(new Texture("buttons/left.png"));
        leftImg.setSize(width, height);
        leftImg.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                leftClicked = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                leftClicked = false;
            }
        });

        table.padBottom(4);
        table.add().padRight(width / 2);
        table.add(leftImg).size(leftImg.getWidth(), leftImg.getHeight());
        table.add().padRight(width / 2);
        table.add(rightImg).size(rightImg.getWidth(), rightImg.getHeight());
        table.add().padLeft((KloshardGame.V_WIDTH) - 4 * width - width / 2);
        table.add(upImg).size(upImg.getWidth(), upImg.getHeight()).right();
        stage.addActor(table);


    }

    public void draw() {
        stage.draw();
    }

    public boolean isUpClicked() {
        return upClicked;
    }

    public boolean isDownClicked() {
        return downClicked;
    }

    public boolean isLeftClicked() {
        return leftClicked;
    }

    public boolean isRightClicked() {
        return rightClicked;
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public void setUpClicked(boolean upClicked) {
        this.upClicked = upClicked;
    }

    public void setDownClicked(boolean downClicked) {
        this.downClicked = downClicked;
    }

    public void setLeftClicked(boolean leftClicked) {
        this.leftClicked = leftClicked;
    }

    public void setRightClicked(boolean rightClicked) {
        this.rightClicked = rightClicked;
    }

}
