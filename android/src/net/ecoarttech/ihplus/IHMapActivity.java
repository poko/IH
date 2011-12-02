package net.ecoarttech.ihplus;

import net.ecoarttech.ihplus.gps.CurrentLocationOverlay;
import net.ecoarttech.ihplus.gps.DirectionPathOverlay;
import net.ecoarttech.ihplus.gps.SingleVistaOverlay;
import net.ecoarttech.ihplus.model.ActionType;
import net.ecoarttech.ihplus.model.Hike;
import net.ecoarttech.ihplus.model.ScenicVista;
import net.ecoarttech.ihplus.network.NetworkConstants;
import net.ecoarttech.ihplus.util.PhotoProvider;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
	public static final String BUNDLE_START = "start";
	public static final String BUNDLE_END = "end";
	public static final String PROXIMITY_INTENT = "net.ecoarttech.ihplus.ProximityIntent";
	public final static int TAKE_PHOTO_ACTIVITY = 0;
	protected Context mContext;
	protected MapView mMapView;
	protected MapController mMapController;
	protected GeoPoint geoPoint;
	protected CurrentLocationOverlay mCurrentLocationOverlay;
	protected boolean mRouteShown = false;
	protected Hike mHike = new Hike();
	protected LocationManager mLocMgr;
	protected ScenicVista mPhotoVista;
	protected Uri mPhotoUri;
	protected ProgressDialog mDialog;

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
		mMapView.getOverlays().add(mCurrentLocationOverlay);	}

	/*
	 * Return back to Hike Activity from taking a photo. Mark vista as complete if the activity succeeded.
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

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		mCurrentLocationOverlay.enableMyLocation();
		// enable all vista locationListeners - TODO
		if (!mHike.isComplete()) {
			Log.d(TAG, "hike isn't done yet! Let's re-enable the vista listeners");
			for (ScenicVista vista : mHike.getVistas()) {
				Log.d(TAG, "we have a vista!");
				vista.reenableIntent(mContext, mLocMgr);
			}
		}
	}

	protected void onPause() {
		super.onPause();
		mCurrentLocationOverlay.disableMyLocation();
		for (ScenicVista vista : mHike.getVistas()) {
			vista.pauseIntent(mContext, mLocMgr);
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		for (ScenicVista vista : mHike.getVistas()) {
			vista.cancelIntent();
		}
	}

//	private void getRandomAddress(final String start, final String end) {
//		StartCoordsAsyncTask startTask = new StartCoordsAsyncTask(this, start, new DirectionCompletionListener() {
//
//			@Override
//			public void onComplete(Document doc) {
//				NodeList nl = doc.getElementsByTagName("coordinates");
//				String coordsElm = nl.item(0).getFirstChild().getNodeValue();
//				String[] coords = coordsElm.split(",");
//				// long = 0, lat = 1
//				double lat = Double.valueOf(coords[1]);
//				double lng = Double.valueOf(coords[0]);
//				// randomize offset
//				Geocoder g = new Geocoder(mContext, Locale.getDefault());
//				double randLat = lat - getRandomOffset();
//				double randLong = lng - getRandomOffset();
//				List<Address> myList;
//				try {
//					myList = g.getFromLocation(randLat, randLong, 1);
//					Log.d(TAG, "XXXXXXresult:" + myList.get(0));
//					String to = null;
//					if (myList.size() > 0) {
//						Address addy = myList.get(0);
//						to = URLEncoder.encode(addy.getAddressLine(0));
//						Log.d(TAG, "To: " + to);
//					}
//
//					getDirectionData(start, to);
//					getDirectionData(to, end);
//				} catch (IOException e) {
//					e.printStackTrace();
//					//TODO - display retry dialog. 
//				}
//			}
//		});
//		startTask.execute();
//	}

	protected void drawPath(String[] pairs) {
		String[] lngLat = pairs[0].split(",");
		// STARTING POINT
		double startLat = Double.parseDouble(lngLat[1]);
		double startLng = Double.parseDouble(lngLat[0]);
		GeoPoint startGP = new GeoPoint((int) (startLat * 1E6), (int) (startLng * 1E6));

		mHike.addPoint(startGP);
		mHike.setStartPoints(startLat, startLng);
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

//	private void getDirectionData(String srcPlace, String destPlace) {
//		DirectionsAsyncTask task = new DirectionsAsyncTask(this, srcPlace, destPlace,
//				new DirectionCompletionListener() {
//
//					@Override
//					public void onComplete(Document doc) {
//						String pathConent = "";
//						Log.d(TAG, "doc: " + doc);
//						if (doc != null) {
//							NodeList nl = doc.getElementsByTagName("LineString");
//							for (int s = 0; s < nl.getLength(); s++) {
//								Node rootNode = nl.item(s);
//								NodeList configItems = rootNode.getChildNodes();
//								for (int x = 0; x < configItems.getLength(); x++) {
//									Node lineStringNode = configItems.item(x);
//									NodeList path = lineStringNode.getChildNodes();
//									pathConent = path.item(0).getNodeValue();
//								}
//							}
//							String[] tempContent = pathConent.split(" ");
//							generateScenicVistas(tempContent);
//							drawPath(tempContent);
//							if (randomPoint) {
//								mPathCalls++;
//								if (mPathCalls == 2) {
//									drawVistas();
//								}
//							} else {
//								// we're done!
//								drawVistas();
//							}
//						}
//					}
//				});
//		task.execute();
//	}

//	private void generateScenicVistas(String[] points) {
//		Log.d(TAG, "pairs amount:" + points.length);
//		ArrayList<Integer> existingVistas = new ArrayList<Integer>();
//		// if (points.length < 3) {
//		// // each point is a new scenic vista
//		// for (int i = 0; i < points.length; i++) {
//		// createNewVista(points[i]);
//		// }
//		// } else {
//		// generate random number between 2-4 (for 4-8 total scenic vistas)
//		Random rand = new Random();
//		int vistaAmount = 1;// rand.nextInt(2) + 2;// random method (2,3, or 4);
//		Log.d(TAG, "Vistas: " + vistaAmount);
//		int i = 0;
//		while (i < vistaAmount) {
//			int r = rand.nextInt(points.length);
//			Log.d(TAG, "trying vista : " + r);
//			if (!existingVistas.contains(r)) {
//				createNewVista(points[r]);
//				existingVistas.add(r);
//				i++;
//			}
//			// }
//		}
//	}
//
//	private void createNewVista(String coordsStr) {
//		// create a new scenic vista here!
//		String[] lngLat = coordsStr.split(",");
//		ScenicVista vista = new ScenicVista(this, lngLat[1], lngLat[0]);
//		mHike.addVista(vista);
//		Log.d(TAG, "new vista!" + coordsStr);
//	}

	protected void drawVistas() {
		for (ScenicVista vista : mHike.getVistas()) {
			mMapView.getOverlays().add(new SingleVistaOverlay(mContext, vista.getPoint()));
		}
	}

	private static final int VISTA_ENTERED = 1;

	protected void enableVistaProximityAlerts(){
		for (ScenicVista vista : mHike.getVistas()) {
			// setup proximity alert
			Intent intent = new Intent(PROXIMITY_INTENT + vista.hashCode());
			Log.d(TAG, "intent action: " + intent.getAction());
			PendingIntent pi = PendingIntent.getBroadcast(mContext, VISTA_ENTERED, intent, 0);

			mLocMgr.addProximityAlert(vista.getLat(), vista.getLong(), 25, -1, pi);
			Log.d(TAG, "added alert for: " + vista.getLat() + " long: " + vista.getLong());

			// set up pending intent recievers
			IntentFilter filter = new IntentFilter(PROXIMITY_INTENT + vista.hashCode());
			VistaEnteredReceiver br = new VistaEnteredReceiver();
			registerReceiver(br, filter);
			vista.setPendingIntentAndReceiver(pi, br);
		}
	}

	public void onSearchClick(View v) {
		Intent i = new Intent(this, SearchActivity.class);
		startActivity(i);
	}

	public void onHikesClick(View v) {
		Log.d(TAG, "hike click");
		// TODO - Implement (warn that current hike will be lost)
	}

	public class VistaEnteredReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "IH broadcast, test: " + intent.getAction());
			Bundle extras = intent.getExtras();
			String index = intent.getAction().split(PROXIMITY_INTENT)[1];
			Integer i = Integer.valueOf(index);
			boolean entering = extras.getBoolean(LocationManager.KEY_PROXIMITY_ENTERING);
			String enter = entering ? "Entering" : "Leaving";
			Toast.makeText(mContext, enter + " a scenic vista.", Toast.LENGTH_LONG).show();
			final ScenicVista enteredVista = mHike.getVistaByHashCode(i);
			if (entering) {
				// display hike & vista info at top of screen
				findViewById(R.id.hike_layout).setVisibility(View.VISIBLE);
				findViewById(R.id.vista_layout).setVisibility(View.VISIBLE);
				TextView vistaInfo = (TextView) findViewById(R.id.vista_info);
				vistaInfo.setText(enteredVista.getAction());
				// Util.setBoldFont(mContext, findViewById(R.id.hike_name), findViewById(R.id.vista_label), vistaInfo,
				// vistaCont);
				// set click listeners
				View vistaCont = findViewById(R.id.vista_cont);
				setActionClickListeners(enteredVista, vistaCont);
			} else {
				// TODO - if vista task is not completed, tsk tsk!
				// else, get rid of vista info bar
				findViewById(R.id.hike_layout).setVisibility(View.GONE);
				findViewById(R.id.vista_layout).setVisibility(View.GONE);
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
		// cameraValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		// cameraValues.put(MediaStore.Images.Media.TITLE, vista.getPhotoTitle());
		// cameraValues.put(MediaStore.Images.Media.DESCRIPTION, mContext.getResources().getString(R.string.app_name));
		// Uri uri = mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cameraValues);

		cameraValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		cameraValues.put(MediaStore.Images.Media.TITLE, vista.getPhotoTitle());
		cameraValues.put(MediaStore.Images.Media.DESCRIPTION, mContext.getResources().getString(R.string.app_name));
		Uri uri = mContext.getContentResolver().insert(Uri.parse("content://" + PhotoProvider.AUTHORITY), cameraValues);

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
		vista.pauseIntent(mContext, mLocMgr);
		vista.cancelIntent();
		// save vista info to db
		vista.save(this, mHike.hashCode());
		// check if the hike is complete
		if (mHike.isComplete()) {
			completeHike();
		}
	}

	private void completeHike() {
		// inflate input dialog
		final View dialogView = getLayoutInflater().inflate(R.layout.complete_hike_dialog, null);
		new AlertDialog.Builder(this).setTitle("Complete Hike").setView(dialogView).setPositiveButton("Upload",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// get user inputs
						EditText username = (EditText) dialogView.findViewById(R.id.hike_username);
						EditText hikeName = (EditText) dialogView.findViewById(R.id.hike_name);
						EditText hikeDesc = (EditText) dialogView.findViewById(R.id.hike_desc);
						mHike.setUsername(username.getText().toString());
						mHike.setName(hikeName.getText().toString());
						mHike.setDescription(hikeDesc.getText().toString());
						mHike.upload(mContext, mUploadHandler);
					}
				}).create().show();
	}

	private Handler mUploadHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == NetworkConstants.FAILURE) {
				// retry
				showRetryDialog();
			} else { // success
				Toast.makeText(mContext, "Your hike was uploaded successfully", Toast.LENGTH_LONG).show();
			}
		}
	};

	private void showRetryDialog() {
		new AlertDialog.Builder(this).setTitle("Oh No!").setMessage(
				"Something went wrong during the upload.\nWould you like to try again?").setPositiveButton("Re-try",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mHike.upload(mContext, mUploadHandler);
					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).create().show();
	}
}
