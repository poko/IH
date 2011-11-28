package net.ecoarttech.ihplus;

import java.util.ArrayList;

import net.ecoarttech.ihplus.adapter.SearchListAdapter;
import net.ecoarttech.ihplus.model.Hike;
import net.ecoarttech.ihplus.network.DownloadHikesTask;
import net.ecoarttech.ihplus.network.NetworkConstants;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class SearchActivity extends ListActivity {
	private static String TAG = "IH+ - SearchActivity";
	private Context mContext;
	private SearchListAdapter mAdapter;
	private ProgressDialog mDialog;
	private LocationManager mLocMgr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		this.mContext = this;

		mDialog = ProgressDialog.show(mContext, "", "Searching for hikes nearby");
		mDialog.setCancelable(true);
		// try to get last known location,
		mLocMgr = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		Location lastKnown = mLocMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (lastKnown == null) {// TODO || (System.currentTimeMillis() - lastKnown.getTime()) > 10 * 60 * 1000) {
			// if it is null, or more than 10 minutes old, fetch a new one instead.
			Log.d(TAG, "last known location is null or more than 10 mins old: " + lastKnown);
			mLocMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		} else {
			searchHikes(lastKnown.getLatitude(), lastKnown.getLongitude());
		}

		mAdapter = new SearchListAdapter(this, null);
		setListAdapter(mAdapter);
	}

	private Handler downloadHikesHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mDialog.dismiss();
			Log.d(TAG, "got hikes! " + msg.what);
			if (msg.what == NetworkConstants.SUCCESS) {
				ArrayList<Hike> hikes = new ArrayList<Hike>();
				JSONArray hikesJson;
				try {
					hikesJson = new JSONArray(msg.getData().getString(NetworkConstants.HIKES_JSON_KEY));
					for (int i = 0; i < hikesJson.length(); i++) {
						hikes.add(Hike.fromJson(hikesJson.getJSONObject(i)));
					}
					mAdapter.setHikes(hikes);
				} catch (JSONException e) {
					e.printStackTrace();
					showError();
				}
			} else {
				showError();
			}
		}
	};

	private LocationListener locationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			// start search call,
			searchHikes(location.getLatitude(), location.getLongitude());
			// remove request for updates.
			mLocMgr.removeUpdates(this);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}
	};

	private void searchHikes(double lat, double lng) {
		new DownloadHikesTask(downloadHikesHandler, lat, lng).execute();
	}

	private void showError() {
		Toast.makeText(mContext, "sorry, an error occured searching for hikes", Toast.LENGTH_LONG).show();
	}
}
