package net.ecoarttech.ihplus.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import net.ecoarttech.ihplus.api.DirectionsResponse;
import net.ecoarttech.ihplus.api.GeocodeResponse;
import net.ecoarttech.ihplus.api.GeocodeServiceInstanceKt;
import net.ecoarttech.ihplus.model.MapPoint;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OriginalHikeActivity extends IHMapActivity {
	private static final String TAG = "IH+ - OriginalHikeAct";
	public static final String BUNDLE_START = "start";
	public static final String BUNDLE_END = "end";
	private ProgressDialog mDialog;
	private int mPathCalls = 0;
	private String mStart;
	private String mEnd;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showLoadingDialog();
		// generate hike
		Bundle extras = getIntent().getExtras();
		try {
			mStart = URLEncoder.encode(extras.getString(BUNDLE_START), "UTF-8");
			mEnd = URLEncoder.encode(extras.getString(BUNDLE_END), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Toast.makeText(this, "There was an error with your start/end points", Toast.LENGTH_LONG);
		}
		// if start is current location -- get current location
		if (mStart.equals("Current+Location")) {
			Log.d(TAG, "current location being used.");
			// wait for callback from MyLocationListener
//			createWithCurrentLocation();
		} else { // business as usual
			// geocode the start address
			geoCodeStartAddress();
		}
	}

	private void showLoadingDialog(){
		mDialog = new ProgressDialog(this);
		mDialog.setMessage("generating hike");
		mDialog.show();
	}

	private void geoCodeStartAddress() {
		GeocodeServiceInstanceKt.getGeocodeService().geocode(mStart, GeocodeServiceInstanceKt.getApiKey()).enqueue(geocodeCallback);
	}

//	private void getRandomAddress(GeoPoint currentLocation) {
//		// reverse GeoCode current location
//		randomizePoints(currentLocation.getLatitudeE6() / 1E6, currentLocation.getLongitudeE6() / 1E6, true);
//	}

//	private Handler currentLocationFixHandler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			GeoPoint currentLocation = (GeoPoint) msg.obj;
//			mCurrentLocationOverlay.callbackReceived();
//			getRandomAddress(currentLocation);
//		}
//	};

	private Callback<GeocodeResponse> geocodeCallback = new Callback<GeocodeResponse>() {
		@Override
		public void onResponse(Call<GeocodeResponse> call, Response<GeocodeResponse> response) {
			GeocodeResponse geoRes = response.body();
			Log.d(TAG, "got geo response: " + geoRes);
			if (geoRes.getError_message() != null){
				displayRetryDialog();
			}
			else {
				if (geoRes.getLocation() != null){
					Location loc = geoRes.getLocation();
					randomizePoints(loc.getLatitude(), loc.getLongitude(), false);
				}
				else {
					displayRetryDialog();
				}
			}
		}

		@Override
		public void onFailure(Call<GeocodeResponse> call, Throwable t) {
			Log.d(TAG, "failed geo response: " + t);
			displayRetryDialog();
		}
	};


	private void randomizePoints(double lat, double lng, boolean startIsCurrentLocation) {
		Geocoder g = new Geocoder(mContext, Locale.getDefault());
		double randLat = lat - getRandomOffset();
		double randLong = lng - getRandomOffset();
		List<Address> addyList;
		try {
			if (startIsCurrentLocation) {
				// need name of start location, too? maybe not we should just be able to send coords
				List<Address> startAddy = g.getFromLocation(lat, lng, 1);
				if (startAddy.size() > 0) {
					Address start = startAddy.get(0);
					mStart = URLEncoder.encode(start.getAddressLine(0) + " " + start.getAddressLine(1), "UTF-8");
				}
			}
			addyList = g.getFromLocation(randLat, randLong, 1);
			Log.d(TAG, "XXXXXXresult:" + addyList.get(0));
			String to = null;
			if (addyList.size() > 0) {
				Address addy = addyList.get(0);
				to = URLEncoder.encode(addy.getAddressLine(0) + " " + addy.getAddressLine(1), "UTF-8");
				Log.d(TAG, "To: " + to);
			}
			getDirectionData(mStart, to);
			// put in a tiny pause
			Thread.sleep(500);
			getDirectionData(to, mEnd);
		} catch (IOException e) {
			e.printStackTrace();
			displayRetryDialog();
		} catch (InterruptedException e) {
			e.printStackTrace();
			displayRetryDialog();
		}
	}

	private void displayRetryDialog() {
		if (!isFinishing()) {
			mDialog.dismiss();
			new AlertDialog.Builder(this).setTitle("oops").setMessage("there was an error generating your hike.\ntry again?").setPositiveButton("Retry", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					showLoadingDialog();
					geoCodeStartAddress();
				}
			}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					finish();
				}
			}).show();
		}
	}


	private Callback<DirectionsResponse> directionsCallback = new Callback<DirectionsResponse>() {
		@Override
		public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
			DirectionsResponse dirRes = response.body();
			Log.d(TAG, "got directions response: " + dirRes);
			if (dirRes.getError_message() != null){
				displayRetryDialog();
			}
			else {
				List<MapPoint> pathPoints = dirRes.getPathPoints();
				if (pathPoints.size() == 0) {
					displayRetryDialog();
					return;
				}
				generateScenicVistas(pathPoints);
				//mHike.addPoints(pathPoints, mPathCalls);
				mPathCalls++; //"countdownlatch" essentially
				if (mPathCalls == 1) {
					// add the random mid-point to scenic vistas (addVista() call will prevent double
					// vistas)
					createNewVista(pathPoints.get(pathPoints.size() - 1));
				}
				if (mPathCalls == 2) {
					//createEndVista(pathPoints.get(pathPoints.size() - 1));
					//drawVistasAndDownloadTasks();
				}
			}
		}

		@Override
		public void onFailure(Call<DirectionsResponse> call, Throwable t) {
			displayRetryDialog();
		}
	};

	private void getDirectionData(String srcPlace, String destPlace) {
		GeocodeServiceInstanceKt.getGeocodeService().directions(srcPlace, destPlace, GeocodeServiceInstanceKt.getApiKey()).enqueue(directionsCallback);
	}

	private void generateScenicVistas(List<MapPoint> points) {
		ArrayList<Integer> existingVistas = new ArrayList<Integer>();
		if (points.size() < 3) {
			// each point is a new scenic vista
			for (int i = 0; i < points.size(); i++) {
				createNewVista(points.get(i));
			}
		} else {
			// generate random number between 2-4 (for 4-8 total scenic vistas)
			Random rand = new Random();
			int vistaAmount = rand.nextInt(2) + 2;// random method (2,3, or 4);
			Log.d(TAG, "Vistas: " + vistaAmount);
			int i = 0;
			while (i < vistaAmount) {
				int r = rand.nextInt(points.size());
				Log.d(TAG, "trying vista : " + r);
				if (!existingVistas.contains(r)) {
					createNewVista(points.get(r));
					existingVistas.add(r);
					i++;
				}
			}
		}
	}

	private void createNewVista(MapPoint point) {
		// create a new scenic vista here!
		// String[] lngLat = coordsStr.split(",");
		//ScenicVistaV2 vista = new ScenicVistaV2(Double.parseDouble(point.getLatitude()), Double.parseDouble(point.getLongitude()));
		//mHike.addVista(vista);
		Log.d(TAG, "new vista!" + point);
	}

//	private void createEndVista(GeoPoint point) {
//		// create a new scenic vista here!
//		// String[] lngLat = coordsStr.split(",");
//		ScenicVista vista = new ScenicVista(point);
//		vista.setIsEnd();
//		mHike.addEndVista(vista);
//		Log.d(TAG, "new vista!" + point);
//	}
//
//	private void drawVistasAndDownloadTasks() {
//		drawPath(mHike.getPoints());
//		drawVistas();
//		// get vista 'tasks' from server
//		new DownloadVistaActionsTask(mHike.getVistas(), mDownloadActionsHandler).execute();
//	}
//
//	private Handler mDownloadActionsHandler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			Log.d(TAG, "got message from vista action downloader! " + msg.what);
//			mDialog.dismiss();
//			if (msg.what != NetworkConstants.SUCCESS) {
//				// if the server has error, will have some kind of vista info on the phone
//				Cursor cursor = DBHelper.getVistaActions(mContext, mHike.getVistas().size());
//				for (int i = 0; i < mHike.getVistas().size(); i++) {
//					cursor.moveToPosition(i);
//					ScenicVista v = mHike.getVistas().get(i);
//					v.setActionId(cursor.getInt(cursor.getColumnIndex(NetworkConstants.RESPONSE_JSON_VISTAS_ID)));
//					v.setAction(cursor.getString(cursor.getColumnIndex(NetworkConstants.RESPONSE_JSON_VISTAS_VERBIAGE)));
//					v.setActionType(cursor.getString(cursor.getColumnIndex(NetworkConstants.RESPONSE_JSON_VISTAS_TYPE)));
//				}
//				cursor.close();
//			}
//			// enable all the Vista proximity alerts
//			enableVistaProximityAlerts();
//		}
//	};
//
	private static double getRandomOffset() {
		float min = .0005f;
		float max = .005f;
		double num = Math.random() * (max - min);
		double offset = Math.floor(num * 10000 + 0.5) / 10000;
		Log.d(TAG, "offset:" + offset);
		int i = (offset / .0001) % 2 == 0 ? 1 : -1;
		return offset * i;
	}
}
