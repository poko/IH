package net.ecoarttech.ihplus;

import net.ecoarttech.ihplus.util.Constants;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ViewOrHikeActivity extends Activity {
	private static String TAG = "IH+ - ViewOrHikeActivity";
	private int mHikeId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_or_hike);
		Bundle extras = getIntent().getExtras();
		if (extras != null)
			mHikeId = extras.getInt(Constants.BUNDLE_HIKE_ID);
	}

	/** onClickListener for 'View Hike' button.
	 * @param v
	 */
	public void onViewClick(View v) {
		Log.d(TAG, "view click");
		Intent intent = new Intent(this, ViewHikeActivity.class);
		intent.putExtra(Constants.BUNDLE_HIKE_ID, mHikeId);
		startActivity(intent);
	}

	/** onClickListnener for 'Hike' button.
	 * @param v
	 */
	public void onHikeClick(View v) {
		Log.d(TAG, "hike click");
		Intent intent = new Intent(this, WalkHikeActivity.class);
		intent.putExtra(Constants.BUNDLE_HIKE_ID, mHikeId);
		startActivity(intent);
	}
}
