package net.ecoarttech.ihplus;

import net.ecoarttech.ihplus.util.Util;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CreateHikeActivity extends Activity {
	private static String TAG = "IH+ - CreateHikeActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_hike);
		// set fonts
		Util.setFont(this, findViewById(R.id.start_address), findViewById(R.id.end_address));
		Util.setBoldFont(this, findViewById(R.id.start_hike));
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.d(TAG, "new intent");
		// clear out end & start addresses
		((TextView) findViewById(R.id.start_address)).setText("");
		((TextView) findViewById(R.id.end_address)).setText("");
	}

	public void onSearchClick(View v) {
		// start the search activity
		Log.d(TAG, "search click");
		startActivity(new Intent(this, SearchActivity.class));
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
		Intent i = new Intent(this, OriginalHikeActivity.class);
		i.putExtra(IHMapActivity.BUNDLE_START, start);
		i.putExtra(IHMapActivity.BUNDLE_END, end);
		startActivity(i);
	}
}
