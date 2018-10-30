package net.ecoarttech.ihplus.activities;

import net.ecoarttech.ihplus.api.HikeDetailsResponse;
import net.ecoarttech.ihplus.api.IHApiServiceInstanceKt;
import net.ecoarttech.ihplus.util.Constants;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalkHikeActivity extends IHMapActivity {
	private static final String TAG = "IH+ - WalkHikeActivity";
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
			downloadHikeDetails();
		}
	}

	private void downloadHikeDetails() {
		IHApiServiceInstanceKt.get().getHikeDetails(mHikeId).enqueue(getHikeCallback);
	}

	Callback<HikeDetailsResponse> getHikeCallback = new Callback<HikeDetailsResponse>(){

		@Override
		public void onResponse(Call<HikeDetailsResponse> call, Response<HikeDetailsResponse> response) {
			Log.d(TAG, "got hike:" + response.body());
			mHike = response.body().getHike();
			mHike.setOriginal(false); // set this as a re-hike
			//todo - do we need to mark the last vista as the end vista?
			updateUI();
		}

		@Override
		public void onFailure(Call<HikeDetailsResponse> call, Throwable t) {
			showFailureDialog();
		}
	};

	private void updateUI(){
		if (mMap != null){
			// draw path
			drawPath();
			// draw vistas
			drawVistas();
			// setup vista intents
			enableVistaProximityAlerts();
		}
		else{
			//todo handle this cae
		}
	}

	private void showFailureDialog() {
		new AlertDialog.Builder(this).setTitle("oops something went wrong").setMessage("try again?").setPositiveButton("Retry", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				downloadHikeDetails();
			}
		}).setNegativeButton("Cancel", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		}).show();
	}
}
