package net.ecoarttech.ihplus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class IntroActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intro);
	}

	public void onHikeClick(View v) {
		Intent i = new Intent(this, CreateHikeActivity.class);
		startActivity(i);
	}

	public void onSearchClick(View v) {
		Intent i = new Intent(this, SearchActivity.class);
		startActivity(i);
	}
}
