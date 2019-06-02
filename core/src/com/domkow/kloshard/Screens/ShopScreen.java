package com.domkow.kloshard.Screens;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.pay.Information;
import com.badlogic.gdx.pay.Offer;
import com.badlogic.gdx.pay.OfferType;
import com.badlogic.gdx.pay.PurchaseManager;
import com.badlogic.gdx.pay.PurchaseManagerConfig;
import com.badlogic.gdx.pay.PurchaseObserver;
import com.badlogic.gdx.pay.Transaction;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.domkow.kloshard.KloshardGame;
import com.domkow.kloshard.Utils.FireBaseManager;

import java.util.HashMap;

public class ShopScreen implements Screen {

    public static final String SKIN2 = "buy blue";
    public static final String SKIN3 = "buy pink";

    private IapButton buySkin2Button;
    private IapButton buySkin3Button;
    private Viewport viewport;
    private Stage stage;
    private Game game;
    private AssetManager manager;
    private Skin skin;
    private TextureAtlas atlas;
    private MenuScreen parent;
    private ImageButton skin2Button;
    private ImageButton skin3Button;
    private PurchaseManager purchaseManager;
    private TextButton restoreButton;
    private boolean restorePressed;
    private FireBaseManager fireBaseManager;


    public ShopScreen(Game game, MenuScreen parent) {
        this.purchaseManager = ((KloshardGame) game).purchaseManager;
        this.parent = parent;
        this.fireBaseManager = FireBaseManager.instance();
        this.manager = ((KloshardGame) game).manager;
        this.game = game;
        viewport = new FitViewport(KloshardGame.V_WIDTH, KloshardGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((KloshardGame) game).batch);
        Gdx.input.setInputProcessor(stage);
        prepareSkin();
        prepareUI();
        initPurchaseManager();
    }

    private void prepareSkin() {
        // A skin can be loaded via JSON or defined programmatically, either is fine. Using a skin is optional but
        // strongly
        // recommended solely for the convenience of getting a texture, region, etc as a drawable, tinted drawable, etc.
        skin = new Skin();
        atlas = new TextureAtlas(Gdx.files.internal("skin/uiskin.atlas"));
        skin.addRegions(atlas);
        skin.load(Gdx.files.internal("skin/uiskin.json"));

    }

    private void prepareUI() {
        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        font.font.getData().setScale(5);
        Table table = new Table();
//        table.setDebug(true);
        table.defaults().pad(50);
        table.setFillParent(true);

        //button 2
        skin2Button = new ImageButton(new TextureRegionDrawable(new Texture("textures/Player/p2_front.png")));
        skin2Button.getImage().setScale(2);
        table.add(skin2Button).size(200, 100).uniform();

        //button 3
        skin3Button = new ImageButton(new TextureRegionDrawable(new Texture("textures/Player/p3_front.png")));
        skin3Button.getImage().setScale(2);

        table.add(skin3Button).size(200, 100).uniform();
        table.row();

        buySkin2Button = new IapButton(SKIN2, 179);
        buySkin2Button.getLabel().setFontScale(4);
        table.add(buySkin2Button).size(400, 150);
        buySkin3Button = new IapButton(SKIN3, 349);
        buySkin3Button.getLabel().setFontScale(4);
        table.add(buySkin3Button).size(400, 150);

        table.row();
//        //go buy button
//        Button buyButton = new TextButton("Buy", skin);
//        ((TextButton) buyButton).getLabel().setFontScale(4);
//        buyButton.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                Gdx.app.log("Buy Button", "pressed");
//                //buy something
//            }
//        });
//        table.add(buyButton).size(400, 150).colspan(2);
//        table.row();
        //go back button
        Button backButton = new TextButton("Back", skin);
        ((TextButton) backButton).getLabel().setFontScale(4);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("backButton", "pressed");
                game.setScreen(parent);
            }
        });
        table.add(backButton).size(400, 150).colspan(2);
        table.row();

        this.restoreButton = new TextButton("Restore", skin);
        restoreButton.setDisabled(true);
        restoreButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                restorePressed = true;
                restoreButton.setDisabled(true);
                purchaseManager.purchaseRestore();
            }
        });
        table.row();
        stage.addActor(table);

    }

    private void initPurchaseManager() {
        // the purchase manager config here in the core project works if your SKUs are the same in every
        // payment system. If this is not the case, inject them like the PurchaseManager is injected
        PurchaseManagerConfig pmc = new PurchaseManagerConfig();
        pmc.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier(SKIN2));
        pmc.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier(SKIN3));

        purchaseManager.install(new MyPurchaseObserver(), pmc, true);
    }

    private void updateGuiWhenPurchaseManInstalled(String errorMessage) {
        // einfüllen der Infos
        buySkin2Button.updateFromManager();
        buySkin3Button.updateFromManager();

        if (purchaseManager.installed() && errorMessage == null) {
            restoreButton.setDisabled(false);
        } else {
            errorMessage = (errorMessage == null ? "Error instantiating the purchase system" : errorMessage);
            //TODO show dialog here (happens when no internet connection available)
        }

    }

    private class IapButton extends TextButton {
        private final String skinEntitlement;
        private final int usdCents;

        public IapButton(String skinEntitlement, int usdCents) {
            super(skinEntitlement, skin);
            this.skinEntitlement = skinEntitlement;
            this.usdCents = usdCents;

            addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    buyItem();
                }
            });
        }

        private void buyItem() {
            HashMap<String, Object> map = new HashMap<String, Object>();

            if (skinEntitlement.equals(SKIN2)) {
                map.put("skin1", true);
                map.put("skin2", true);
                map.put("skin3", false);
                fireBaseManager.updateUserCredentials(map);
            }
            if (skinEntitlement.equals(SKIN3)) {
                map.put("skin1", true);
                map.put("skin2", false);
                map.put("skin3", true);
                fireBaseManager.updateUserCredentials(map);
            }
            purchaseManager.purchase(skinEntitlement);

        }

        public void setBought(boolean fromRestore) {
            setDisabled(true);
        }

        public void updateFromManager() {
            Information skuInfo = purchaseManager.getInformation(skinEntitlement);

            if (skuInfo == null || skuInfo.equals(Information.UNAVAILABLE)) {
                setDisabled(true);
                setText("Not available");
            } else {
                setText(skuInfo.getLocalName() + " " + skuInfo.getLocalPricing());
            }
        }
    }

    private class MyPurchaseObserver implements PurchaseObserver {

        @Override
        public void handleInstall() {
            Gdx.app.log("IAP", "Installed");

            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    updateGuiWhenPurchaseManInstalled(null);
                }
            });
        }

        @Override
        public void handleInstallError(final Throwable e) {
            Gdx.app.error("IAP", "Error when trying to install PurchaseManager", e);
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    updateGuiWhenPurchaseManInstalled(e.getMessage());
                }
            });
        }

        @Override
        public void handleRestore(final Transaction[] transactions) {
            if (transactions != null && transactions.length > 0)
                for (Transaction t : transactions) {
                    handlePurchase(t, true);
                }
            else if (restorePressed)
                showErrorOnMainThread("Nothing to restore");
        }

        @Override
        public void handleRestoreError(Throwable e) {
            if (restorePressed)
                showErrorOnMainThread("Error restoring purchases: " + e.getMessage());
        }

        @Override
        public void handlePurchase(final Transaction transaction) {
            handlePurchase(transaction, false);
        }

        protected void handlePurchase(final Transaction transaction, final boolean fromRestore) {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    if (transaction.isPurchased()) {
                        if (transaction.getIdentifier().equals(SKIN2))
                            buySkin2Button.setBought(fromRestore);
                        else if (transaction.getIdentifier().equals(SKIN3))
                            buySkin3Button.setBought(fromRestore);
                    }
                }
            });
        }

        @Override
        public void handlePurchaseError(Throwable e) {
            showErrorOnMainThread("Error on buying:\n" + e.getMessage());
        }

        @Override
        public void handlePurchaseCanceled() {

        }

        private void showErrorOnMainThread(final String message) {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    // show a dialog here...
                }
            });
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void show() {

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

