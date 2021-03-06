package net.ecoarttech.ihplus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class IntroActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intro);

		Toast.makeText(this, "make sure gps is enabled.", Toast.LENGTH_LONG).show();
	}
	
	public void onAboutClick(View v){
		startActivity(new Intent(this, AboutActivity.class));
	}
	
	public void onCreditsClick(View v){
		startActivity(new Intent(this, InfoActivity.class));
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
