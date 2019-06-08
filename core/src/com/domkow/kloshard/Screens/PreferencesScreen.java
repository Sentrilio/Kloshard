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
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.domkow.kloshard.KloshardGame;
import com.domkow.kloshard.Utils.FireBaseManager;

public class PreferencesScreen implements Screen {
    public static final String REPOLINK = "https://github.com/libgdx/gdx-pay";

    private Viewport viewport;
    private Stage stage;
    private Game game;
    private AssetManager manager;
    public Skin skin;
    private TextureAtlas atlas;
    private MenuScreen parent;
    private ButtonGroup<ImageButton> buttonGroup;
    private ImageButton skin1Button;
    private ImageButton skin2Button;
    private ImageButton skin3Button;
    private FireBaseManager fireBaseManager;

    public PreferencesScreen(Game game, MenuScreen parent) {
        this.fireBaseManager = FireBaseManager.instance();
        fireBaseManager.getUserData();
        this.parent = parent;
        this.manager = ((KloshardGame) game).manager;
        this.game = game;
        viewport = new FitViewport(KloshardGame.V_WIDTH, KloshardGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((KloshardGame) game).batch);
        Gdx.input.setInputProcessor(stage);
        this.skin=parent.skin;
        prepareUI();
    }



    private void prepareUI() {
        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        font.font.getData().setScale(5);
        Table table = new Table();
//        table.setDebug(true);
//        table.defaults().pad(50);
        table.setFillParent(true);
        buttonGroup = new ButtonGroup<ImageButton>();

        //button 1
        skin1Button = new ImageButton(new TextureRegionDrawable(new Texture("textures/Player/p1_front.png")));
        skin1Button.getStyle().imageChecked = new TextureRegionDrawable(new Texture("textures/Player/p1_front_checked.png"));
        skin1Button.getImage().setScale(2);
        buttonGroup.add(skin1Button);
        skin1Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("skin 1", "toggled");
                parent.setKloshardSkin(1);
                buttonGroup.setUncheckLast(true);
            }
        });
        table.add(skin1Button).size(200, 100).uniform();

        if (fireBaseManager.skin2) {
            //button 2
            skin2Button = new ImageButton(new TextureRegionDrawable(new Texture("textures/Player/p2_front.png")));
            skin2Button.getStyle().imageChecked = new TextureRegionDrawable(new Texture("textures/Player/p2_front_checked.png"));
            skin2Button.getImage().setScale(2);
            buttonGroup.add(skin2Button);
            skin2Button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.app.log("skin 2", "toggled");
                    parent.setKloshardSkin(2);
                    buttonGroup.setUncheckLast(true);

                }
            });
        } else {
            //button 2
            skin2Button = new ImageButton(new TextureRegionDrawable(new Texture("textures/Player/p2_front_unavailable.png")));
            skin2Button.getImage().setScale(2);
            skin2Button.setDisabled(true);
        }

        table.add(skin2Button).size(200, 100).uniform();

        if (fireBaseManager.skin3) {
            //button 3
            skin3Button = new ImageButton(new TextureRegionDrawable(new Texture("textures/Player/p3_front.png")));
            skin3Button.getStyle().imageChecked = new TextureRegionDrawable(new Texture("textures/Player/p3_front_checked.png"));
            skin3Button.getImage().setScale(2);
            buttonGroup.add(skin3Button);
            skin3Button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.app.log("skin 3", "toggled");
                    parent.setKloshardSkin(3);
                    buttonGroup.setUncheckLast(true);
                }
            });
        } else {
            //button 3
            skin3Button = new ImageButton(new TextureRegionDrawable(new Texture("textures/Player/p3_front_unavailable.png")));
            skin3Button.getImage().setScale(2);
        }

        table.add(skin3Button).size(200, 100).uniform();
        table.row();

        //go back button
        Button backButton = new TextButton("Back", skin);
        ((TextButton) backButton).getLabel().setFontScale(4);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("backButton", "pressed");
                game.setScreen(parent);
//                dispose();
            }
        });
        table.add().uniform();
        table.add(backButton).size(400, 150).center();
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(36/255f, 123/255f, 160/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void show() {
        int kloshardSkin = parent.getKloshardSkin();
        if (kloshardSkin == 1) {
            buttonGroup.setUncheckLast(true);
            skin1Button.setChecked(true);
        } else if (kloshardSkin == 2) {
            buttonGroup.setUncheckLast(true);
            skin2Button.setChecked(true);
        } else if (kloshardSkin == 3) {
            buttonGroup.setUncheckLast(true);
            skin3Button.setChecked(true);
        }
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

