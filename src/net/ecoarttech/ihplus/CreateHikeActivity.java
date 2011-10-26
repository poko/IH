package net.ecoarttech.ihplus;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class CreateHikeActivity extends Activity {
	private static String TAG = "IH+ - CreateHikeActivity";
	private static int CREATE_HIKE = 1;
	private AnimationDrawable testd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_hike);
		ImageView test = (ImageView) findViewById(R.id.test_img);
		test.setBackgroundResource(R.drawable.gps_indicator);
		testd = (AnimationDrawable) test.getBackground();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		testd.start();
	}

	public void onSearchClick(View v) {
		// start the search activity
		Log.d(TAG, "search click");
	}

	public void onHitTrailClick(View v) {
		// make sure we have a start and end point
		String start = ((EditText) findViewById(R.id.start_address)).getText().toString();
		String end = ((EditText) findViewById(R.id.end_address)).getText().toString();
		if (start.length() == 0 || end.length() == 0) {
			Toast.makeText(this, "Please enter a start and end address", Toast.LENGTH_LONG).show();
			return;
		}
		// start map Activity
		Intent i = new Intent(this, IHMapActivity.class);
		i.putExtra(IHMapActivity.BUNDLE_START, start);
		i.putExtra(IHMapActivity.BUNDLE_END, end);
		startActivityForResult(i, CREATE_HIKE);
	}
}
