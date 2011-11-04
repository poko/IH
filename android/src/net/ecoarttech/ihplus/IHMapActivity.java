package net.ecoarttech.ihplus;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import net.ecoarttech.ihplus.gps.CurrentLocationOverlay;
import net.ecoarttech.ihplus.gps.DirectionPathOverlay;
import net.ecoarttech.ihplus.gps.SingleVistaOverlay;
import net.ecoarttech.ihplus.model.ActionType;
import net.ecoarttech.ihplus.model.Hike;
import net.ecoarttech.ihplus.model.ScenicVista;
import net.ecoarttech.ihplus.network.DirectionCompletionListener;
import net.ecoarttech.ihplus.network.DirectionsAsyncTask;
import net.ecoarttech.ihplus.network.StartCoordsAsyncTask;
import net.ecoarttech.ihplus.network.VistaDownloadTask;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class IHMapActivity extends MapActivity {
	private static final String TAG = "IHMapView";
	public static final String BUNDLE_RANDOM = "random";
	public static final String BUNDLE_START = "start";
	public static final String BUNDLE_END = "end";
	public static final String PROXIMITY_INTENT = "net.ecoarttech.ihplus.ProximityIntent";
	public final static int TAKE_PHOTO_ACTIVITY = 0;
	private Context mContext;
	private MapView mMapView;
	private MapController mMapController;
	private GeoPoint geoPoint;
	private CurrentLocationOverlay mCurrentLocationOverlay;
	private boolean mRouteShown = false;
	private Hike mHike;
	private boolean randomPoint = true;
	private int mPathCalls = 0;
	private LocationManager mLocMgr;
	private ScenicVista mPhotoVista;
	private Uri mPhotoUri;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		this.mContext = this;
		// setup map view & view elements
		mMapView = (MapView) findViewById(R.id.map_view);
		mMapView.setSatellite(false);

		mLocMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		mCurrentLocationOverlay = new CurrentLocationOverlay(this, mMapView);
		mCurrentLocationOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				mMapView.getController().animateTo(mCurrentLocationOverlay.getMyLocation());
			}
		});
		mMapView.getOverlays().add(mCurrentLocationOverlay);

		Bundle extras = getIntent().getExtras();
		final String start = URLEncoder.encode(extras.getString(BUNDLE_START));
		final String end = URLEncoder.encode(extras.getString(BUNDLE_END));
		randomPoint = extras.getBoolean(BUNDLE_RANDOM);
		// get start/end points from bundle
		Log.d(TAG, "randomizing?? " + randomPoint);
		if (randomPoint) {
			// get address for random geo points
			getRandomAddress(start, end);
		} else {
			getDirectionData(start, end);
		}

		// create new Hike object
		mHike = new Hike();
		Log.d(TAG, "hike hashcode: " + mHike.hashCode());
	}

	/*
	 * Return back to Task Entry Activity from taking a photo. Mark picture step as complete if the activity succeeded.
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Log.d(TAG, "result code: " + resultCode);
		if (requestCode == TAKE_PHOTO_ACTIVITY) {
			if (resultCode == Activity.RESULT_OK)
				mPhotoVista.setPhotoUri(mPhotoUri);
			if (mPhotoVista.isComplete()) {
				// allow user to move on to the next vista
				markVistaAsCompleted(mPhotoVista);
			}
		}
	}

	private void getRandomAddress(final String start, final String end) {
		StartCoordsAsyncTask startTask = new StartCoordsAsyncTask(this, start, new DirectionCompletionListener() {

			@Override
			public void onComplete(Document doc) {
				NodeList nl = doc.getElementsByTagName("coordinates");
				String coordsElm = nl.item(0).getFirstChild().getNodeValue();
				String[] coords = coordsElm.split(",");
				// long = 0, lat = 1
				double lat = Double.valueOf(coords[1]);
				double lng = Double.valueOf(coords[0]);
				// randomize offset
				Geocoder g = new Geocoder(mContext, Locale.getDefault());
				double randLat = lat - getRandomOffset();
				double randLong = lng - getRandomOffset();
				List<Address> myList;
				try {
					myList = g.getFromLocation(randLat, randLong, 1);
					Log.d(TAG, "XXXXXXresult:" + myList.get(0));
					String to = null;
					if (myList.size() > 0) {
						Address addy = myList.get(0);
						to = URLEncoder.encode(addy.getAddressLine(0));
						Log.d(TAG, "To: " + to);
					}

					getDirectionData(start, to);
					getDirectionData(to, end);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		startTask.execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mCurrentLocationOverlay.enableMyLocation();
		// enable all vista locationListeners - TODO
	}

	protected void onPause() {
		super.onPause();
		mCurrentLocationOverlay.disableMyLocation();
		// disable all vista locationListeners - TODO
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		for (ScenicVista vista : mHike.getVistas()) {
			vista.cancelIntent(mContext, mLocMgr);
		}
		// TODO - unregister all recievers unregisterReceiver(mBroadReceiver);
	}

	private void drawPath(String[] pairs) {
		String[] lngLat = pairs[0].split(",");
		// STARTING POINT
		GeoPoint startGP = new GeoPoint((int) (Double.parseDouble(lngLat[1]) * 1E6), (int) (Double
				.parseDouble(lngLat[0]) * 1E6));

		mHike.addPoint(startGP);
		mMapController = mMapView.getController();
		geoPoint = startGP;
		mMapController.setCenter(geoPoint); // TODO - center on user's location?
		mMapController.setZoom(16);
		mMapView.getOverlays().add(new DirectionPathOverlay(startGP, startGP));

		// NAVIGATE THE PATH
		GeoPoint gp1;
		GeoPoint gp2 = startGP;
		for (int i = 1; i < pairs.length; i++) {

			lngLat = pairs[i].split(",");
			gp1 = gp2;
			// watch out! For GeoPoint, first:latitude, second:longitude
			gp2 = new GeoPoint((int) (Double.parseDouble(lngLat[1]) * 1E6), (int) (Double.parseDouble(lngLat[0]) * 1E6));
			mMapView.getOverlays().add(new DirectionPathOverlay(gp1, gp2));
			Log.d("xxx", "pair:" + pairs[i]);
			mHike.addPoint(gp2);
		}

		// END POINT
		mMapView.getOverlays().add(new DirectionPathOverlay(gp2, gp2));

		mMapView.getController().animateTo(startGP);
		mMapView.setBuiltInZoomControls(true);
		mMapView.displayZoomControls(true);
		mRouteShown = true;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return mRouteShown;
	}

	private void getDirectionData(String srcPlace, String destPlace) {
		DirectionsAsyncTask task = new DirectionsAsyncTask(this, srcPlace, destPlace,
				new DirectionCompletionListener() {

					@Override
					public void onComplete(Document doc) {
						String pathConent = "";
						Log.d(TAG, "doc: " + doc);
						if (doc != null) {
							NodeList nl = doc.getElementsByTagName("LineString");
							for (int s = 0; s < nl.getLength(); s++) {
								Node rootNode = nl.item(s);
								NodeList configItems = rootNode.getChildNodes();
								for (int x = 0; x < configItems.getLength(); x++) {
									Node lineStringNode = configItems.item(x);
									NodeList path = lineStringNode.getChildNodes();
									pathConent = path.item(0).getNodeValue();
								}
							}
							String[] tempContent = pathConent.split(" ");
							generateScenicVistas(tempContent);
							drawPath(tempContent);
							if (randomPoint) {
								mPathCalls++;
								if (mPathCalls == 2) {
									drawVistas();
								}
							} else {
								// we're done!
								drawVistas();
							}
						}
					}
				});
		task.execute();
	}

	private void generateScenicVistas(String[] points) {
		Log.d(TAG, "pairs amount:" + points.length);
		ArrayList<Integer> existingVistas = new ArrayList<Integer>();
		if (points.length < 3) {
			// each point is a new scenic vista
			for (int i = 0; i < points.length; i++) {
				createNewVista(points[i]);
			}
		} else {
			// generate random number between 2-4 (for 4-8 total scenic vistas)
			Random rand = new Random();
			int vistaAmount = rand.nextInt(2) + 2;// random method (2,3, or 4);
			Log.d(TAG, "Vistas: " + vistaAmount);
			int i = 0;
			while (i < vistaAmount) {
				int r = rand.nextInt(points.length);
				Log.d(TAG, "trying vista : " + r);
				if (!existingVistas.contains(r)) {
					createNewVista(points[r]);
					existingVistas.add(r);
					i++;
				}
			}
		}
	}

	private void createNewVista(String coordsStr) {
		// create a new scenic vista here!
		String[] lngLat = coordsStr.split(",");
		ScenicVista vista = new ScenicVista(this, lngLat[1], lngLat[0]);
		mHike.addVista(vista);
		Log.d(TAG, "new vista!" + coordsStr);
	}

	private void drawVistas() {
		for (ScenicVista vista : mHike.getVistas()) {
			mMapView.getOverlays().add(new SingleVistaOverlay(mContext, vista.getPoint()));
		}
		// get vista 'tasks' from server
		new VistaDownloadTask(mHike.getVistas(), mActionHandler).execute();
		// TODO - if the server has error, should have some kind of vista info on the phone
	}

	private static final int VISTA_ENTERED = 1;

	private Handler mActionHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.d(TAG, "got message! " + msg.what);
			// enable all the Vista proximity alerts?
			for (int i = 0; i < mHike.getVistas().size(); i++) {// (ScenicVista vista : mHike.getVistas()) {

				ScenicVista vista = mHike.getVistas().get(i);
				// setup proximity alert
				Intent intent = new Intent(PROXIMITY_INTENT + i);
				Log.d(TAG, "intent action: " + intent.getAction());
				PendingIntent pi = PendingIntent.getBroadcast(mContext, VISTA_ENTERED, intent, 0);

				mLocMgr.addProximityAlert(vista.getLat(), vista.getLong(), 25, -1, pi);
				Log.d(TAG, "added alert for: " + vista.getLat() + " long: " + vista.getLong());

				// set up pending intent recievers
				IntentFilter filter = new IntentFilter(PROXIMITY_INTENT + i);
				VistaEnteredReceiver br = new VistaEnteredReceiver();
				registerReceiver(br, filter);
				vista.setPendingIntentAndReceiver(pi, br);
			}
		}
	};

	private static double getRandomOffset() {
		double num = Math.random() * (.009);
		double offset = Math.floor(num * 1000 + 0.5) / 1000;
		int i = (offset / .001) % 2 == 0 ? 1 : -1;
		return offset * i;
	}

	public void onSearchClick(View v) {
		Log.d(TAG, "search click");
		// TODO - Implement
	}

	public void onHikesClick(View v) {
		Log.d(TAG, "hike click");
		// TODO - Implement
	}

	public class VistaEnteredReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "IH broadcast, test: " + intent.getAction());
			Bundle extras = intent.getExtras();
			// int i = extras.getInt(BUNDLE_VISTA);
			String index = intent.getAction().split(PROXIMITY_INTENT)[1];
			Integer i = Integer.valueOf(index);
			Log.d(TAG, "I: " + i);
			boolean entering = extras.getBoolean(LocationManager.KEY_PROXIMITY_ENTERING);
			Log.d(TAG, "entering: " + entering);
			String enter = entering ? "Entering" : "Leaving";
			Toast.makeText(mContext, enter + " a scenic vista.", Toast.LENGTH_LONG).show();
			final ScenicVista enteredVista = mHike.getVistas().get(i);
			if (entering) {
				// display hike & vista info at top of screen
				findViewById(R.id.hike_layout).setVisibility(View.VISIBLE);
				findViewById(R.id.vista_layout).setVisibility(View.VISIBLE);
				TextView vistaInfo = (TextView) findViewById(R.id.vista_info);
				vistaInfo.setText(enteredVista.getAction());
				// Util.setBoldFont(mContext, findViewById(R.id.hike_name), findViewById(R.id.vista_label), vistaInfo,
				// vistaCont);
				// TODO - if this is not an original hike, display the gallery button
				// set click listeners
				View vistaCont = findViewById(R.id.vista_cont);
				View actionButton = findViewById(R.id.vista_task);
				setActionClickListeners(enteredVista, vistaCont, actionButton);

			} else {
				// if vista task is not completed, tsk tsk!
				// else, get rid of vista info bar
			}
		}
	}

	private void setActionClickListeners(final ScenicVista vista, View... views) {
		for (View view : views) {
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// depending on action type
					// open note dialog
					if (vista.getActionType() == ActionType.NOTE) {
						final View alertContent = getLayoutInflater().inflate(R.layout.note_dialog, null);
						new AlertDialog.Builder(mContext).setTitle("Take a Note").setIcon(0).setView(alertContent)
								.setPositiveButton("Done", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface d, int which) {
										EditText noteInput = (EditText) alertContent.findViewById(R.id.vista_note);
										vista.setNote(noteInput.getText().toString());
										if (vista.isComplete()) {
											// allow user to move on to the next vista
											markVistaAsCompleted(vista);
										}
									}
								}).create().show();
					} else if (vista.getActionType() == ActionType.PHOTO) {
						// open camera intent
						startCameraIntent(vista);
					}
				}
			});
		}
	}

	private void startCameraIntent(ScenicVista vista) {
		// TODO - check that sdcard is available
		// save the vista for which we are saving the photo
		mPhotoVista = vista;

		// create an intent for accessing the camera
		Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		ContentValues cameraValues = new ContentValues();
		cameraValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		cameraValues.put(MediaStore.Images.Media.TITLE, "IHPlus_" + vista.getLat() + "_" + vista.getLong());
		cameraValues.put(MediaStore.Images.Media.DESCRIPTION, mContext.getResources().getString(R.string.app_name));
		Uri uri = mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cameraValues);

		pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		mPhotoUri = uri;
		// take the picture by invoking the camera activity
		startActivityForResult(pictureIntent, TAKE_PHOTO_ACTIVITY);
	}

	private void markVistaAsCompleted(ScenicVista vista) {
		// remove vista info views
		findViewById(R.id.hike_layout).setVisibility(View.GONE);
		findViewById(R.id.vista_layout).setVisibility(View.GONE);
		// remove pending intent
		vista.cancelIntent(mContext, mLocMgr);
		// save vista info to db
		vista.save(this, mHike.hashCode());
	}
}
