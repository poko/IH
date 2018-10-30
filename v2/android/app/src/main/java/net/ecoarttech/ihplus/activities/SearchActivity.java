package net.ecoarttech.ihplus.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import net.ecoarttech.ihplus.R;
import net.ecoarttech.ihplus.adapter.SearchListAdapter;
import net.ecoarttech.ihplus.api.GeocodeResponse;
import net.ecoarttech.ihplus.api.GeocodeServiceInstanceKt;
import net.ecoarttech.ihplus.api.HikesResponse;
import net.ecoarttech.ihplus.api.IHApiServiceInstanceKt;
import net.ecoarttech.ihplus.model.HikeV2;
import net.ecoarttech.ihplus.util.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import net.ecoarttech.ihplus.api.DirectionCompletionListener;
//import net.ecoarttech.ihplus.api.DownloadHikesTask;
//import net.ecoarttech.ihplus.api.StartCoordsAsyncTask;

public class SearchActivity extends ListActivity {
	private static String TAG = "IH+ - SearchActivity";
	private Context mContext;
	private SearchListAdapter mAdapter;
	private ProgressDialog mDialog;
	//	private LocationManager mLocMgr;
	private EditText mSearchBar;
	private TextView mEmptyView;
	private FusedLocationProviderClient fusedLocationClient;

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
				HikeV2 hike = (HikeV2) mAdapter.getItem(position);
				i.putExtra(Constants.BUNDLE_HIKE_ID, hike.getHike_id());
				i.putExtra(Constants.BUNDLE_HIKE_DESC_LINE1, hike.getName() + ". " + hike.getDescription());
				i.putExtra(Constants.BUNDLE_HIKE_DESC_LINE2, "created by " + hike.getUsername() + ", " + hike.getDate());
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
			//todo get key from manifest
			GeocodeServiceInstanceKt.getGeocodeService().geocode(input, GeocodeServiceInstanceKt.getApiKey()).enqueue(geocodeCallback);
		} else {
			Toast.makeText(mContext, "enter a search term", Toast.LENGTH_SHORT).show();
		}
	}

	public void searchForNearbyHikes(View v) {
		mDialog = ProgressDialog.show(mContext, "", "searching for hikes nearby");
		mDialog.setCancelable(true);
		// try to get last known location,
		fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}

		fusedLocationClient.getLastLocation()
				.addOnSuccessListener(this, new OnSuccessListener<Location>() {
					@SuppressLint("MissingPermission")
					@Override
					public void onSuccess(Location location) {
						// Got last known location. In some rare situations this can be null.
						if (location == null || (System.currentTimeMillis() - location.getTime()) > 10 * 60 * 1000) {
							// request location update
							fusedLocationClient.requestLocationUpdates(getLocationRequest(), locationCallback, null);
						}
						else {
							searchHikes(location.getLatitude(), location.getLongitude());
						}
					}
				});
	}

	public void onSearchClick(View v) {
		searchByText(mSearchBar.getText().toString());
	}

	public void onHikesClick(View v) {
		Intent i = new Intent(this, CreateHikeActivity.class);
		startActivity(i);
	}

	private Callback<GeocodeResponse> geocodeCallback = new Callback<GeocodeResponse>() {
		@Override
		public void onResponse(Call<GeocodeResponse> call, Response<GeocodeResponse> response) {
			GeocodeResponse geoRes = response.body();
			Log.d(TAG, "got geo response: " + geoRes);
			if (geoRes.getError_message() != null){
				showError();
			}
			else {
				if (geoRes.getLocation() != null){
					Location loc = geoRes.getLocation();
					searchHikes(loc.getLatitude(), loc.getLongitude());
				}
				else {
					showError(); //todo perhaps show a diff error message
				}
			}

		}

		@Override
		public void onFailure(Call<GeocodeResponse> call, Throwable t) {
			Log.d(TAG, "failed geo response: " + t);
			showError();
		}
	};
//	private DirectionCompletionListener coordsListener = new DirectionCompletionListener() {
//
//		@Override
//		public void onComplete(String result) {
//			if (result != null) {
//				try {
//					JSONObject jsonResp = new JSONObject(result);
//					if (jsonResp != null && jsonResp.optString("status").equals("OK")) {
//						JSONArray resultsJson = jsonResp.optJSONArray("results");
//						if (resultsJson != null && resultsJson.length() > 0) {
//							JSONObject resultJson = resultsJson.getJSONObject(0);
//							JSONObject locJson = resultJson.optJSONObject("geometry").optJSONObject("location");
//							double lat = locJson.optDouble("lat");
//							double lng = locJson.optDouble("lng");
//							// TODO - check for validity
//							// send coords up to server. bam.
//							searchHikes(lat, lng);
//						} else {
//							showError();
//						}
//					} else {
//						showError();
//					}
//				} catch (JSONException e) {
//					e.printStackTrace();
//					showError();
//				}
//			} else {
//				showError();
//			}
//		}
//	};

	@Override
	protected void onPause() {
		super.onPause();
		if (fusedLocationClient != null)
			fusedLocationClient.removeLocationUpdates(locationCallback);
	}

	private LocationCallback locationCallback = new LocationCallback(){
		@Override
		public void onLocationResult(LocationResult locationResult) {
			if (locationResult == null) {
				return;
			}
			for (Location location : locationResult.getLocations()) {
				// start search call,
				searchHikes(location.getLatitude(), location.getLongitude());
				// remove request for updates.
				fusedLocationClient.removeLocationUpdates(this);
			}
		};
	};

	private LocationRequest getLocationRequest() {
		LocationRequest locationRequest = LocationRequest.create();

		return locationRequest
				.setInterval(1000)
				.setFastestInterval(1000)
				.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
				.setNumUpdates(1);
	}


	private void searchHikes(double lat, double lng) {
		Log.d(TAG, "Searching for hikes near " + lat + " " + lng);
		IHApiServiceInstanceKt.get().searchHikes(lat, lng).enqueue(searchHikesCallback);
	}

	private Callback<HikesResponse> searchHikesCallback = new Callback<HikesResponse>() {

		@Override
		public void onResponse(Call<HikesResponse> call, Response<HikesResponse> response) {
			mDialog.dismiss();
			List<HikeV2> hikes = response.body().getHikes();
			Log.d(TAG, "got hikes! " + hikes);
			mAdapter.setHikes(hikes);
			mEmptyView.setVisibility(hikes.size() == 0 ? View.VISIBLE : View.INVISIBLE);
			//todo this could use some improvement ^ it's small
		}

		@Override
		public void onFailure(Call<HikesResponse> call, Throwable t) {
			mDialog.dismiss();
			showError();
		}
	};

	private void showError() {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
		Toast.makeText(mContext, "oops, an error occurred searching for hikes", Toast.LENGTH_LONG).show();
	}
}
