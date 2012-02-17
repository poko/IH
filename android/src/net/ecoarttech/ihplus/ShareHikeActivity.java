package net.ecoarttech.ihplus;

import net.ecoarttech.ihplus.model.Hike;
import net.ecoarttech.ihplus.network.NetworkConstants;
import net.ecoarttech.ihplus.util.Constants;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ShareHikeActivity extends Activity {
	@SuppressWarnings("unused")
	private static String TAG = "IH+ - ShareHikeActivity";
	private Hike mHike;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_hike);
		
		mHike = (Hike) getIntent().getExtras().getSerializable(Constants.BUNDLE_HIKE);
		TextView hikeInfo = (TextView) findViewById(R.id.hike_info);
		hikeInfo.setText(String.format("%s. %s.\nCreated by %s, %s", mHike.getName(), mHike.getDescription(), mHike.getUsername(), mHike.getCreateDate()));
	}

	public void onShareClick(View v) {
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		shareIntent.setType("text/html");
		shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "I went on an Indeterminate Hike today");
		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, String.format("Click here see documentation from my Indeterminate Hike: %s/web/hike.php?id=%d", NetworkConstants.SERVER_URL, mHike.getId()));

		startActivity(Intent.createChooser(shareIntent, "Share your hike"));
	}
	
	public void onCancelClick(View v){
		startActivity(new Intent(this, IntroActivity.class));
		finish();
	}
	
	public void onSearchClick(View v){
		startActivity(new Intent(this, SearchActivity.class));
		finish();
	}
	
	public void onHikesClick(View v){
		startActivity(new Intent(this, CreateHikeActivity.class));
		finish();
	}
}
