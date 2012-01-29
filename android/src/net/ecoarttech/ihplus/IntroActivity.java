package net.ecoarttech.ihplus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class IntroActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intro);

		Toast.makeText(this, "make sure gps is enabled.", Toast.LENGTH_LONG).show();
	}

	public void onHikeClick(View v) {
		Intent i = new Intent(this, CreateHikeActivity.class);
		startActivity(i);
	}

	public void onSearchClick(View v) {
		Intent i = new Intent(this, SearchActivity.class);
		startActivity(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.intro_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.info:
			startActivity(new Intent(this, InfoActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
