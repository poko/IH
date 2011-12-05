package net.ecoarttech.ihplus;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ShareHikeActivity extends Activity {
	private static String TAG = "IH+ - ShareHikeActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_hike);
		// Bundle extras = getIntent().getExtras();
		// if (extras != null)
		// mHikeId = extras.getInt(Constants.BUNDLE_HIKE_ID);
	}

	public void onFacebookClick(View v) {
		Log.d(TAG, "facebook click");
	}

	public void onTwitterClick(View v) {
		Log.d(TAG, "twitter click");
	}
}
