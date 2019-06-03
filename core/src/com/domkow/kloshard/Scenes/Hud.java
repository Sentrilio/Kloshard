package com.domkow.kloshard.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.domkow.kloshard.KloshardGame;

public class Hud implements Disposable {
    public Stage stage;
    private Viewport viewport;
    private static Integer worldTimer;
    private float timeCount;
    private static Integer score;
    private static Integer coins;
    private Label countdownValueLabel;
    private static Label scoreValueLabel;
    private static Label coinValueLabel;
    private Label timeLabel;
    private Label kloshardLabel;
    private Label coinLabel;

    public Hud(SpriteBatch spriteBatch) {
        worldTimer = 300;
        timeCount = 0;
        score = 0;
        coins = 0;
        viewport = new FitViewport(KloshardGame.V_WIDTH, KloshardGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, spriteBatch);

        Table table = new Table();
        table.top();
        table.setFillParent(true);
        int scale =3;
        countdownValueLabel = new Label(String.format("%03d", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        countdownValueLabel.setFontScale(scale);
        coinValueLabel = new Label(String.format("%01d", coins), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        coinValueLabel.setFontScale(scale);
        scoreValueLabel = new Label(String.format("%06d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreValueLabel.setFontScale(scale);


        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timeLabel.setFontScale(scale);
        coinLabel = new Label("COINS", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        coinLabel.setFontScale(scale);

        kloshardLabel = new Label("SCORE", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        kloshardLabel.setFontScale(scale);

        table.add(kloshardLabel).expandX().padTop(10);
        table.add(coinLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        table.row();
        table.add(scoreValueLabel).expandX();
        table.add(coinValueLabel).expandX();
        table.add(countdownValueLabel).expandX();

        stage.addActor(table);

    }

    public void draw() {
        stage.draw();
    }

    public static int getWorldTimer() {
        return worldTimer;
    }

    public static void addCoin() {
        coins++;
        coinValueLabel.setText(coins);
    }

    public void update(float dt) {
        timeCount += dt;
        if (timeCount >= 1) {
            worldTimer--;
            countdownValueLabel.setText(String.format("%03d", worldTimer));
            timeCount = 0;
        }
    }

    public static void addScore(int value) {
        score += value;
        scoreValueLabel.setText(String.format("%06d", score));
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
