package com.domkow.kloshard;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.pay.PurchaseManager;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.domkow.kloshard.Screens.LoginScreen;
import com.domkow.kloshard.Screens.MenuScreen;
import com.domkow.kloshard.Screens.PlayScreen;


public class KloshardGame extends Game {
	public static final int V_WIDTH = 1700;
	public static final int V_HEIGHT = 900;
	public static final float PPM = 100;

	public static final short NOTHING_BIT = 0;
	public static final short GROUND_BIT = 1;
	public static final short KLOSHARD_BIT = 2;
	public static final short BRICK_BIT = 4;
	public static final short COIN_BIT = 8;
	public static final short DESTROYED_BIT = 16;
	public static final short OBJECT_BIT = 32;
	public static final short ENEMY_HEAD_BIT = 64;
	public static final short ENEMY_BIT = 128;
	public static final short ITEM_BIT = 256;
	public static final short KLOSHARD_HEAD_BIT = 512;
	public static final short DOOR_BIT = 1024;
	public static final short ENEMY_SIDE_BOX_BIT = 2048;
	public static final short ENEMY_GROUND_BOX_BIT = 4096;
	public static final short KLOSHARD_FEET_BIT = 8192;
	public Skin skin;
	public AssetManager manager;
	public PurchaseManager purchaseManager;
	public SpriteBatch batch;
	public TextureAtlas atlas;

	@Override
	public void create() {
		prepareSkin();
		batch = new SpriteBatch();
		manager = new AssetManager();
		manager.finishLoading();
//		setScreen(new PlayScreen(this,new MenuScreen(this,new LoginScreen(this))));
		setScreen(new LoginScreen(this));
	}
	private void prepareSkin() {
		skin = new Skin();
		atlas = new TextureAtlas(Gdx.files.internal("skin/uiskin.atlas"));
		skin.addRegions(atlas);
		skin.load(Gdx.files.internal("skin/uiskin.json"));
	}

	@Override
	public void dispose() {
		super.dispose();
		manager.dispose();
		batch.dispose();
	}

	@Override
	public void render() {
		super.render();

	}
}
