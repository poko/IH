package net.ecoarttech.ihplus;

import java.util.ArrayList;

import net.ecoarttech.ihplus.model.Hike;
import net.ecoarttech.ihplus.model.ScenicVista;
import net.ecoarttech.ihplus.network.DownloadByHikeId;
import net.ecoarttech.ihplus.network.NetworkConstants;
import net.ecoarttech.ihplus.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.android.maps.GeoPoint;

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
			new DownloadByHikeId(hikeDownloadHandler, mHikeId, NetworkConstants.GET_HIKE_URL, NetworkConstants.RESPONSE_JSON_HIKE).execute();
		}
	}

	private Handler hikeDownloadHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			Log.d(TAG, "got message:" + msg.what);
			if (msg.what == NetworkConstants.SUCCESS){
				// parse hike data out from server response
				try {
					JSONObject json = new JSONObject(msg.getData().getString(NetworkConstants.HIKE_JSON_KEY));
					// create hike object
					mHike = Hike.fromJson(json, false);
					JSONArray points = json.getJSONArray(NetworkConstants.RESPONSE_JSON_POINTS);
					for (int i = 0; i < points.length(); i++) {
						JSONObject point = points.getJSONObject(i);
						GeoPoint gp = new GeoPoint(point.getInt(NetworkConstants.RESPONSE_JSON_LAT), point.getInt(NetworkConstants.RESPONSE_JSON_LNG));
						mHike.addPoint(gp);
					}
					JSONArray vistas = json.getJSONArray(NetworkConstants.RESPONSE_JSON_VISTAS);
					for (int i = 0; i < vistas.length(); i++) {
						mHike.addVista(ScenicVista.newFromJson(vistas.getJSONObject(i)));
					}
					// draw path
					ArrayList<GeoPoint> geoPoints = mHike.getPoints();
					String[] pairs = new String[geoPoints.size()];
					for (int i = 0; i < geoPoints.size(); i++){
						GeoPoint gp = geoPoints.get(i);
						pairs[i] = String.format("%f,%f", (float)gp.getLongitudeE6()*.000001, (float)gp.getLatitudeE6()*.000001);
					}
					drawPath(pairs);
					// draw vistas
					drawVistas();
					// setup vista intents
					enableVistaProximityAlerts();
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
		new AlertDialog.Builder(this)
		.setTitle("uh oh something went wrong")
		.setMessage("Try again?")
		.setPositiveButton("Retry", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				new DownloadByHikeId(hikeDownloadHandler, mHikeId, NetworkConstants.GET_HIKE_URL, NetworkConstants.RESPONSE_JSON_HIKE).execute();				
			}
		})
		.setNegativeButton("Cancel", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).show();
	}
}
