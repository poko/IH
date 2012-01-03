package net.ecoarttech.ihplus;

import java.net.URLEncoder;
import java.util.ArrayList;

import net.ecoarttech.ihplus.adapter.SearchListAdapter;
import net.ecoarttech.ihplus.model.Hike;
import net.ecoarttech.ihplus.network.DirectionCompletionListener;
import net.ecoarttech.ihplus.network.DownloadHikesTask;
import net.ecoarttech.ihplus.network.NetworkConstants;
import net.ecoarttech.ihplus.network.StartCoordsAsyncTask;
import net.ecoarttech.ihplus.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
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
			mLocMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		} else {
			searchHikes(lastKnown.getLatitude(), lastKnown.getLongitude());
		}

		mAdapter = new SearchListAdapter(this, null);
		setListAdapter(mAdapter);
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent i = new Intent(mContext, ViewOrHikeActivity.class);
				Hike hike = (Hike) mAdapter.getItem(position);
				i.putExtra(Constants.BUNDLE_HIKE_ID, hike.getId());
				i.putExtra(Constants.BUNDLE_HIKE_DESC_LINE1, hike.getName() + ". " + hike.getDescription());
				i.putExtra(Constants.BUNDLE_HIKE_DESC_LINE2, "created by " + hike.getUsername() + ", " + hike.getCreateDate());
				startActivity(i);
			}
		});
		
		// setup search bar listener
		EditText searchBar = (EditText) findViewById(R.id.search_bar);
		searchBar.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if ( (event == null && actionId == EditorInfo.IME_ACTION_SEARCH ) || (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN)){
				    // TODO make sure user has input something
				    // start progress dialog
				    mDialog = ProgressDialog.show(mContext, "", "Searching for hikes near " + v.getText().toString());
					mDialog.setCancelable(true);
				    // reverse geocode the search term
				    new StartCoordsAsyncTask(SearchActivity.this, URLEncoder.encode(v.getText().toString()), coordsListener).execute();
				}
				return true;
			}
		});
	}
	
	private DirectionCompletionListener coordsListener = new DirectionCompletionListener() {

		@Override
		public void onComplete(Document doc) {
			if (doc != null){
				NodeList nl = doc.getElementsByTagName("coordinates");
				if (nl != null){
					String coordsElm = nl.item(0).getFirstChild().getNodeValue();
					String[] coords = coordsElm.split(",");
					// long = 0, lat = 1
					double lat = Double.valueOf(coords[1]);
					double lng = Double.valueOf(coords[0]);
					//TODO - check for validity
				    // send coords up to server. bam.
					searchHikes(lat, lng);
				}
				else{
					showError();
				}
			}
			else{
				showError();
			}
		}
	};
	
	@Override
	protected void onPause() {
		super.onPause();
		mLocMgr.removeUpdates(locationListener); 
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
						hikes.add(Hike.fromJson(hikesJson.getJSONObject(i), true));
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
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText(mContext, "Please make sure GPS is enabled.", Toast.LENGTH_LONG).show();
		}
	};

	private void searchHikes(double lat, double lng) {
		new DownloadHikesTask(downloadHikesHandler, lat, lng).execute();
	}

	private void showError() {
		Toast.makeText(mContext, "sorry, an error occurred searching for hikes", Toast.LENGTH_LONG).show();
	}
}
