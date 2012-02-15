package net.ecoarttech.ihplus;

import net.ecoarttech.ihplus.util.Util;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class CreateHikeActivity extends Activity {
	private static String TAG = "IH+ - CreateHikeActivity";
	private EditText mStart;
	private EditText mEnd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_hike);
		// set fonts
		Util.setFont(this, findViewById(R.id.start_address), findViewById(R.id.end_address));
		Util.setBoldFont(this, findViewById(R.id.start_hike));
		mStart = (EditText) findViewById(R.id.start_address);
		mEnd = (EditText) findViewById(R.id.end_address);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.d(TAG, "new intent");
		// clear out end & start addresses
		mStart.setText("");
		mEnd.setText("");
	}
	
	public void onCreditsClick(View v){
		startActivity(new Intent(this, InfoActivity.class));
	}

	public void onSearchClick(View v) {
		// start the search activity
		Log.d(TAG, "search click");
		startActivity(new Intent(this, SearchActivity.class));
	}

	public void onHitTrailClick(View v) {
		Log.d(TAG, "hit the trail click!");
		// make sure we have a start and end point
		String start = mStart.getText().toString();
		String end = mEnd.getText().toString();
		if (start.length() == 0 || end.length() == 0) {
			Toast.makeText(this, "please enter a start and end address", Toast.LENGTH_LONG).show();
			return;
		}
		// hide keyboard
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mEnd.getWindowToken(), 0);
		// start map Activity
		Intent i = new Intent(this, OriginalHikeActivity.class);
		i.putExtra(IHMapActivity.BUNDLE_START, start);
		i.putExtra(IHMapActivity.BUNDLE_END, end);
		startActivity(i);
	}

	public void onCurrentLocationClick(View v) {
		mStart.setText("Current Location");
	}
}
