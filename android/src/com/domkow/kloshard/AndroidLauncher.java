package com.domkow.kloshard;

import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.pay.android.amazon.PurchaseManagerAndroidAmazon;

public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        KloshardGame game = new KloshardGame();
        PurchaseManagerAndroidAmazon purchaseManagerAndroidAmazon = new PurchaseManagerAndroidAmazon(this, 0);
        // nie mozna tu nic logować xd
        if (purchaseManagerAndroidAmazon.getCurrentUserId() == null) {
            game.purchaseManager = purchaseManagerAndroidAmazon;
            initialize(game, config);
        } else {
            game.purchaseManager = purchaseManagerAndroidAmazon;
            initialize(game, config);
        }

    }
}
