package net.ecoarttech.ihplus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ShareHikeActivity extends Activity {
	private static String TAG = "IH+ - ShareHikeActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_hike);
	}

	public void onShareClick(View v) {
		Log.d(TAG, "share click");
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		shareIntent.setType("text/html");
		shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "I took a hike");
		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "View it <a href='http://www.ecoarttech.net'>here</a>");

		startActivity(Intent.createChooser(shareIntent, "Share your hike"));
	}
}
