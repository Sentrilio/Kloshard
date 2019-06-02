package com.domkow.kloshard;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.pay.android.amazon.PurchaseManagerAndroidAmazon;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		KloshardGame game = new KloshardGame();
		game.purchaseManager = new PurchaseManagerAndroidAmazon(this, 0);
		initialize(game, config);
	}
}
