package net.ecoarttech.ihplus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// start create hike activity
				Intent i = new Intent(SplashActivity.this, CreateHikeActivity.class);
				startActivity(i);
				finish();
			}
		}, 1000);
	}
}
