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
import org.json.JSONObject;

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
import android.view.inputmethod.InputMethodManager;
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
	private EditText mSearchBar;
	private TextView mEmptyView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		this.mContext = this;

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
		mSearchBar = (EditText) findViewById(R.id.search_bar);
		mSearchBar.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if ((event == null && actionId == EditorInfo.IME_ACTION_SEARCH) || (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN)) {
					searchByText(v.getText().toString());
				}
				return true;
			}
		});
		mEmptyView = (TextView) getListView().getEmptyView();
		mEmptyView.setVisibility(View.GONE);
	}

	private void searchByText(String input) {
		// start progress dialog
		if (input.length() > 0) {
			// hide keyboard
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mSearchBar.getWindowToken(), 0);
			mDialog = ProgressDialog.show(mContext, "", "searching for hikes near " + input);
			mDialog.setCancelable(true);
			// reverse geocode the search term
			new StartCoordsAsyncTask(SearchActivity.this, URLEncoder.encode(input), coordsListener).execute();
		} else {
			Toast.makeText(mContext, "enter a search term", Toast.LENGTH_SHORT).show();
		}
	}

	public void searchForNearbyHikes(View v) {
		mDialog = ProgressDialog.show(mContext, "", "searching for hikes nearby");
		mDialog.setCancelable(true);
		// try to get last known location,
		mLocMgr = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		Location lastKnown = mLocMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (lastKnown == null || (System.currentTimeMillis() - lastKnown.getTime()) > 10 * 60 * 1000) {
			// if it is null, or more than 10 minutes old, fetch a new one instead.
			Log.d(TAG, "last known location is null or more than 10 mins old: " + lastKnown);
			mLocMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
			mLocMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		} else {
			searchHikes(lastKnown.getLatitude(), lastKnown.getLongitude());
		}
	}

	public void onSearchClick(View v) {
		searchByText(mSearchBar.getText().toString());
	}

	public void onHikesClick(View v) {
		Intent i = new Intent(this, CreateHikeActivity.class);
		startActivity(i);
	}

	private DirectionCompletionListener coordsListener = new DirectionCompletionListener() {

		@Override
		public void onComplete(String result) {
			if (result != null) {
				try {
					JSONObject jsonResp = new JSONObject(result);
					if (jsonResp != null && jsonResp.optString("status").equals("OK")) {
						JSONArray resultsJson = jsonResp.optJSONArray("results");
						if (resultsJson != null && resultsJson.length() > 0) {
							JSONObject resultJson = resultsJson.getJSONObject(0);
							JSONObject locJson = resultJson.optJSONObject("geometry").optJSONObject("location");
							double lat = locJson.optDouble("lat");
							double lng = locJson.optDouble("lng");
							// TODO - check for validity
							// send coords up to server. bam.
							searchHikes(lat, lng);
						} else {
							showError();
						}
					} else {
						showError();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					showError();
				}
			} else {
				showError();
			}
		}
	};

	@Override
	protected void onPause() {
		super.onPause();
		if (mLocMgr != null)
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
					mEmptyView.setVisibility(hikes.size() == 0 ? View.VISIBLE : View.INVISIBLE);

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
			Toast.makeText(mContext, "make sure gps is enabled.", Toast.LENGTH_LONG).show();
		}
	};

	private void searchHikes(double lat, double lng) {
		new DownloadHikesTask(downloadHikesHandler, lat, lng).execute();
	}

	private void showError() {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
		Toast.makeText(mContext, "oops, an error occurred searching for hikes", Toast.LENGTH_LONG).show();
	}
}
