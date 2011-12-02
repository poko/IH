package net.ecoarttech.ihplus;

import net.ecoarttech.ihplus.network.DownloadExistingHike;
import net.ecoarttech.ihplus.network.NetworkConstants;
import net.ecoarttech.ihplus.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class WalkHikeActivity extends IHMapActivity {
	private static final String TAG = "WalkHikeActivity";
	private int mHikeId;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// download hike
		// get hike id
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mHikeId = extras.getInt(Constants.BUNDLE_HIKE_ID);
			new DownloadExistingHike(hikeDownloadHanler, mHikeId).execute();
		}
	}

	
	private Handler hikeDownloadHanler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == NetworkConstants.SUCCESS){
				// parse hike data out from server response
				try {
					JSONObject json = new JSONObject(msg.getData().getString(NetworkConstants.HIKE_JSON_KEY));
					
				} catch (JSONException e) {
					e.printStackTrace();
					showFailureDialog();
				}
			}
			else{
				showFailureDialog();
			}
		}
	};
	
	private void showFailureDialog(){
		// TODO - improve
		new AlertDialog.Builder(this).setPositiveButton("Retry", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				new DownloadExistingHike(hikeDownloadHanler, mHikeId).execute();				
			}
		});
	}
}
