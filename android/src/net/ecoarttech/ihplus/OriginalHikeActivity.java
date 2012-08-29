package net.ecoarttech.ihplus;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import net.ecoarttech.ihplus.db.DBHelper;
import net.ecoarttech.ihplus.model.ScenicVista;
import net.ecoarttech.ihplus.network.DirectionCompletionListener;
import net.ecoarttech.ihplus.network.DirectionsAsyncTask;
import net.ecoarttech.ihplus.network.DownloadVistaActionsTask;
import net.ecoarttech.ihplus.network.NetworkConstants;
import net.ecoarttech.ihplus.network.StartCoordsAsyncTask;
import net.ecoarttech.ihplus.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.android.maps.GeoPoint;

public class OriginalHikeActivity extends IHMapActivity {
	private static final String TAG = "IH+ - OriginalHikeActivity";
	private boolean randomPoint = true;
	private int mPathCalls = 0;
	private String mStart;
	private String mEnd;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// generate hike
		mDialog = new ProgressDialog(this);
		mDialog.setMessage("generating hike");
		mDialog.show();
		Bundle extras = getIntent().getExtras();
		mStart = URLEncoder.encode(extras.getString(BUNDLE_START));
		mEnd = URLEncoder.encode(extras.getString(BUNDLE_END));
		// if start is current location -- get current location
		if (mStart.equals("Current+Location")) {
			Log.d(TAG, "current location being used.");
			// wait for callback from MyLocationListener
			mCurrentLocationOverlay.setCallback(currentLocationFixHandler);
		} else { // business as usual
			// get start/end points from bundle
			if (randomPoint) {
				// get address for random geo points
				getRandomAddress();
			} else {
				getDirectionData(mStart, mEnd);
			}
		}
	}

	private void getRandomAddress() {
		StartCoordsAsyncTask startTask = new StartCoordsAsyncTask(this, mStart, coordsListener);
		startTask.execute();
	}

	private void getRandomAddress(GeoPoint currentLocation) {
		// reverse GeoCode current location
		randomizePoints(currentLocation.getLatitudeE6() / 1E6, currentLocation.getLongitudeE6() / 1E6, true);
	}

	private Handler currentLocationFixHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			GeoPoint currentLocation = (GeoPoint) msg.obj;
			mCurrentLocationOverlay.callbackReceived();
			getRandomAddress(currentLocation);
		}
	};

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
							randomizePoints(lat, lng, false);
						} else {
							displayRetryDialog();
						}
					} else {
						displayRetryDialog();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					displayRetryDialog();
				}
			} else {
				// display retry dialog.
				displayRetryDialog();
			}
		}
	};

	private void randomizePoints(double lat, double lng, boolean isStart) {
		Geocoder g = new Geocoder(mContext, Locale.getDefault());
		double randLat = lat - getRandomOffset();
		double randLong = lng - getRandomOffset();
		List<Address> myList;
		try {
			if (isStart) {
				// reverse geocode start location, too
				List<Address> startAddy = g.getFromLocation(lat, lng, 1);
				if (startAddy.size() > 0) {
					Address start = startAddy.get(0);
					mStart = URLEncoder.encode(start.getAddressLine(0) + " " + start.getAddressLine(1));
				}
			}
			myList = g.getFromLocation(randLat, randLong, 1);
			Log.d(TAG, "XXXXXXresult:" + myList.get(0));
			String to = null;
			if (myList.size() > 0) {
				Address addy = myList.get(0);
				to = URLEncoder.encode(addy.getAddressLine(0) + " " + addy.getAddressLine(1));
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
					new StartCoordsAsyncTask(mContext, mStart, coordsListener).execute();
				}
			}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					finish(); // TODO - Clear out endpoints
				}
			}).show();
		}
	}

	private void getDirectionData(String srcPlace, String destPlace) {
		DirectionsAsyncTask task = new DirectionsAsyncTask(this, srcPlace, destPlace, new DirectionCompletionListener() {

			@Override
			public void onComplete(String result) {
				ArrayList<GeoPoint> pathPoints = new ArrayList<GeoPoint>();
				if (result != null) {
					try {
						JSONObject jsonResult = new JSONObject(result);
						JSONArray jsonRoutes = jsonResult.optJSONArray("routes");
						if (jsonRoutes != null && jsonRoutes.length() > 0) {
							JSONObject jsonRoute = jsonRoutes.getJSONObject(0);
							JSONObject legJson = jsonRoute.getJSONArray("legs").getJSONObject(0);
							JSONArray steps = legJson.getJSONArray("steps");
							for (int k = 0; k < steps.length(); k++) {
								// get poly line points
								String polyLineString = steps.getJSONObject(k).getJSONObject("polyline").getString("points");
								Log.d(TAG, "polyline: " + polyLineString);
								pathPoints.addAll(Util.decodePoly(polyLineString));
							}
							if (pathPoints.size() == 0) {
								displayRetryDialog();
								return;
							}

							generateScenicVistas(pathPoints);
							mHike.addPoints(pathPoints, mPathCalls);
							// drawPath(pathPoints);
							if (randomPoint) {
								mPathCalls++;
								if (mPathCalls == 1) {
									// add the random mid-point to scenic vistas (addVista() call will prevent double
									// vistas)
									createNewVista(pathPoints.get(pathPoints.size() - 1));
								}
								if (mPathCalls == 2) {
									createEndVista(pathPoints.get(pathPoints.size() - 1));
									drawVistasAndDownloadTasks();
								}
							} else {
								// we're done!
								drawVistasAndDownloadTasks();
							}

						}
					} catch (JSONException e) {
						e.printStackTrace();
						displayRetryDialog();
					}
				} else {
					displayRetryDialog();
				}
			}
		});
		task.execute();
	}

	private void generateScenicVistas(ArrayList<GeoPoint> points) {
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

	private void createNewVista(GeoPoint point) {
		// create a new scenic vista here!
		// String[] lngLat = coordsStr.split(",");
		ScenicVista vista = new ScenicVista(point);
		mHike.addVista(vista);
		Log.d(TAG, "new vista!" + point);
	}

	private void createEndVista(GeoPoint point) {
		// create a new scenic vista here!
		// String[] lngLat = coordsStr.split(",");
		ScenicVista vista = new ScenicVista(point);
		vista.setIsEnd();
		mHike.addEndVista(vista);
		Log.d(TAG, "new vista!" + point);
	}

	private void drawVistasAndDownloadTasks() {
		drawPath(mHike.getPoints());
		drawVistas();
		// get vista 'tasks' from server
		new DownloadVistaActionsTask(mHike.getVistas(), mDownloadActionsHandler).execute();
	}

	private Handler mDownloadActionsHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.d(TAG, "got message from vista action downloader! " + msg.what);
			mDialog.dismiss();
			if (msg.what != NetworkConstants.SUCCESS) {
				// if the server has error, will have some kind of vista info on the phone
				Cursor cursor = DBHelper.getVistaActions(mContext, mHike.getVistas().size());
				for (int i = 0; i < mHike.getVistas().size(); i++) {
					cursor.moveToPosition(i);
					ScenicVista v = mHike.getVistas().get(i);
					v.setActionId(cursor.getInt(cursor.getColumnIndex(NetworkConstants.RESPONSE_JSON_VISTAS_ID)));
					v.setAction(cursor.getString(cursor.getColumnIndex(NetworkConstants.RESPONSE_JSON_VISTAS_VERBIAGE)));
					v.setActionType(cursor.getString(cursor.getColumnIndex(NetworkConstants.RESPONSE_JSON_VISTAS_TYPE)));
				}
				cursor.close();
			}
			// enable all the Vista proximity alerts
			enableVistaProximityAlerts();
		}
	};

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
