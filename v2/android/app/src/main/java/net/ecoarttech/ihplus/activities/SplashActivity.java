package net.ecoarttech.ihplus.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import net.ecoarttech.ihplus.R;


public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		// setup crashlytics
		// TODO

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// start create hike activity
				Intent i = new Intent(SplashActivity.this, IntroActivity.class);
				startActivity(i);
				finish();
			}
		}, 3000);
	}
}
