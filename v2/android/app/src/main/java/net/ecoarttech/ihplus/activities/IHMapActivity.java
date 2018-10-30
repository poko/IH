package net.ecoarttech.ihplus.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import net.ecoarttech.ihplus.R;
import net.ecoarttech.ihplus.model.ActionType;
import net.ecoarttech.ihplus.model.HikeV2;
import net.ecoarttech.ihplus.model.MapPoint;
import net.ecoarttech.ihplus.model.ScenicVistaV2;
import net.ecoarttech.ihplus.util.PhotoProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IHMapActivity extends AppCompatActivity implements OnMapReadyCallback {
	private static final String TAG = "IH+ - IHMapView";
	public static final String PROXIMITY_INTENT = "net.ecoarttech.ihplus.ProximityIntent";
	public final static int TAKE_PHOTO_ACTIVITY = 0;
	protected Context mContext;
//	protected MapController mMapController;
	protected GoogleMap mMap;
	protected HikeV2 mHike; //todo instantiate new hike?
    private ScenicVistaV2 mCurrentVista; //vista variable - set when we enter
	protected LocationManager mLocMgr;
	protected static ScenicVistaV2 mPhotoVista;
	protected Uri mPhotoUri;
	protected HashMap<String, String> mContacts = new HashMap<String, String>();
	private GeofencingClient mGeofencingClient;
	private PendingIntent mGeofencePendingIntent;
	//todo make sure we have a prodcution API key for the map!

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		this.mContext = this;

		// Get the SupportMapFragment and request notification
		// when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		mLocMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mGeofencingClient = LocationServices.getGeofencingClient(this);

		// populate user's contact list for any text actions (to prevent db lookups every time this kind of action takes
		// place)
		//todo - get contacts permissions at the correct time
//		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
//		String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
//				ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE };
//
//		Cursor cur = getContentResolver().query(uri, projection, null, null, null);
//
//		int indexName = cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
//		int indexNumber = cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//		int indexType = cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
//
//		if (cur.moveToFirst()) {
//			do {
//				String name = cur.getString(indexName);
//				String number = cur.getString(indexNumber);
//				if (number != null) {
//					int type = cur.getInt(indexType);
//					String phoneType;
//					switch (type) {
//					case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE: {
//						phoneType = " (Mobile)";
//						break;
//					}
//					case ContactsContract.CommonDataKinds.Phone.TYPE_HOME: {
//						phoneType = " (Home)";
//						break;
//					}
//					case ContactsContract.CommonDataKinds.Phone.TYPE_WORK: {
//						phoneType = " (Work)";
//						break;
//					}
//					default: {
//						phoneType = " (Other)";
//						break;
//					}
//					}
//					mContacts.put(name + phoneType, number);
//				}
//				// Do work...
//			} while (cur.moveToNext());
//		}
//		cur.close();
	}

	/*
	 * Return back to Hike Activity from taking a photo. Mark vista as complete if the activity succeeded.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Log.d(TAG, "result code: " + resultCode);
		if (requestCode == TAKE_PHOTO_ACTIVITY) {
			if (resultCode == Activity.RESULT_OK)
//			todo	mPhotoVista.setPhotoUri(mPhotoUri);
			if (mPhotoVista.getComplete()) {
				// allow user to move on to the next vista
				markVistaAsCompleted(mPhotoVista);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		// enable all vista locationListeners
		if (mHike != null && !mHike.getComplete()) {
			Log.d(TAG, "hike isn't done yet! Let's re-enable the vista listeners");
			for (ScenicVistaV2 vista : mHike.getVistas()) {
				Log.d(TAG, "we have a vista!");
				vista.reenableIntent(mContext, mLocMgr);
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mHike != null) {
			for (ScenicVistaV2 vista : mHike.getVistas()) {
				vista.pauseIntent(mContext, mLocMgr);
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		for (ScenicVistaV2 vista : mHike.getVistas()) {
			vista.cancelIntent();
		}
	}

	@Override
	public void onBackPressed() {
		if (mHike.isPartiallyComplete()) {
			showHikeWarning();
		} else {
			super.onBackPressed();
		}
	}

	protected void drawVistas() {
		for (ScenicVistaV2 vista : mHike.getVistas()) {
			addMarker(vista);
		}
	}

	protected void drawPath(){
		PolylineOptions options = new PolylineOptions();
		//options.color()
		LatLng latLng = null; //declare it out here so we can use the last point in the final zoom
		for (MapPoint p : mHike.getPoints()){
			Double latE6 = Double.valueOf(p.getLatitude());
			Double lngE6 = Double.valueOf(p.getLongitude());
			latLng = new LatLng(latE6 * .000001, lngE6 * .000001);
			options.add(latLng);
		}
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
		mMap.addPolyline(options);
	}

	//todo may need to adjust vista icon if not a marker with a point
	private void addMarker(ScenicVistaV2 vista) {
		MarkerOptions markerOptions = new MarkerOptions();
		//if end vista: .hike_completed_point
		markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.scenic_vista_point));
				//todo  if vista has been visited: R.drawable.visited_vista;
		mMap.addMarker(markerOptions.position(new LatLng(vista.getLatitude(), vista.getLongitude()))); //todo maybe we dont need to create new latlng each time
	}

	private static final int VISTA_ENTERED = 1;

//	private PendingIntent getGeofencePendingIntent() {
//		// Reuse the PendingIntent if we already have it.
//		if (mGeofencePendingIntent != null) {
//			return mGeofencePendingIntent;
//		}
//		Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
//		// We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
//		// calling addGeofences() and removeGeofences().
//		mGeofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.
//				FLAG_UPDATE_CURRENT);
//		return mGeofencePendingIntent;
//	}

	@SuppressLint("MissingPermission")
    protected void enableVistaProximityAlerts() {

		List<Geofence> mGeofenceList = new ArrayList<>();
		for (ScenicVistaV2 vista : mHike.getVistas()) {
            // setup proximity alert
			Intent intent = new Intent(PROXIMITY_INTENT + vista.hashCode());
			Log.d(TAG, "intent action: " + intent.getAction());
			PendingIntent pi = PendingIntent.getBroadcast(mContext, VISTA_ENTERED, intent, 0);

			mLocMgr.addProximityAlert(vista.getLatitude(), vista.getLongitude(), 10, -1, pi);
			Log.d(TAG, "added alert for: " + vista.getLatitude() + " long: " + vista.getLongitude());
			// set up pending intent recievers
			IntentFilter filter = new IntentFilter(PROXIMITY_INTENT + vista.hashCode());
			VistaEnteredReceiver br = new VistaEnteredReceiver();
			registerReceiver(br, filter);
			vista.setPendingIntent(pi);
			vista.setBroadcastReceiver(br);
		}
	}

	public void onSearchClick(View v) {
		Intent i = new Intent(this, SearchActivity.class);
		startActivity(i);
	}

	public void onHikesClick(View v) {
		showHikeWarning();
	}

	private void showHikeWarning() {
		if (mHike.getOriginal()) {
			// (warn that current hike will be lost)
			new AlertDialog.Builder(this).setTitle("create a new hike")
					.setMessage("are you sure you would like to create a new hike?\nthe current hike will be lost.")
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent i = new Intent(IHMapActivity.this, CreateHikeActivity.class);
							startActivity(i);
							dialog.dismiss();
						}
					}).setNegativeButton("No", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).show();
		} else { // just start create hike activity
			Intent i = new Intent(this, CreateHikeActivity.class);
			startActivity(i);
		}
	}


	@Override
	public void onMapReady(GoogleMap googleMap) {
		//animate to current location (?)
		Log.d(TAG, "map ready");
		mMap = googleMap;
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: show error
			return;
		}
		mMap.setMyLocationEnabled(true);
		//todo manually pan to users location (? double check this one, what if they want to look at a hike somewhere else?
		//
	}

	public class VistaEnteredReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "IH broadcast, recieved: " + intent.getAction());
			Bundle extras = intent.getExtras();
			String index = intent.getAction().split(PROXIMITY_INTENT)[1];
			Integer i = Integer.valueOf(index);
			boolean entering = extras.getBoolean(LocationManager.KEY_PROXIMITY_ENTERING);
			String enter = entering ? "Entering" : "Leaving";
			Log.i(TAG, enter + " a scenic vista.");
			// final ScenicVista enteredVista = mHike.getVistaByHashCode(i);
			if (entering && mCurrentVista == null) { // entered a new vista ..
				// display hike & vista info at top of screen
				mCurrentVista = mHike.getVistaByHashCode(i);
				findViewById(R.id.hike_layout).setVisibility(View.VISIBLE);
				findViewById(R.id.vista_layout).setVisibility(View.VISIBLE);
				TextView vistaInfo = findViewById(R.id.vista_info);
				vistaInfo.setText(Html.fromHtml(mCurrentVista.getAction().getVerbiage()));
				// set click listeners
				View vistaCont = findViewById(R.id.vista_cont);
				setActionClickListeners(mCurrentVista, vistaCont);
			} else {
				// TODO - if vista task is not completed, tsk tsk! ??
				if (mCurrentVista != null && mCurrentVista.getComplete()) {
					// if vista is complete, get rid of vista info bar
					findViewById(R.id.hike_layout).setVisibility(View.GONE);
					findViewById(R.id.vista_layout).setVisibility(View.GONE);
				}
				// otherwise we ignore ..
			}
		}
	}

	private void setActionClickListeners(final ScenicVistaV2 vista, View... views) {
		for (View view : views) {
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// depending on action type
					// open note dialog
					ActionType type = vista.getAction().getAction_type();
					if (type == ActionType.NOTE) {
						final View alertContent = getLayoutInflater().inflate(R.layout.note_dialog, null);
						new AlertDialog.Builder(mContext).setTitle("make a field note").setIcon(0)
								.setView(alertContent).setPositiveButton("Done", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface d, int which) {
										EditText noteInput = (EditText) alertContent.findViewById(R.id.vista_note);
										vista.setNote(noteInput.getText().toString());
										if (noteInput.length() > 0) {
											// allow user to move on to the next vista
											markVistaAsCompleted(vista);
										}
									}
								}).create().show();
					} else if (type == ActionType.MEDITATE) {
						vista.setComplete(true);
						markVistaAsCompleted(vista);
					} else if (type == ActionType.PHOTO) {
						// open camera intent
						startCameraIntent(vista);
					} else if (type == ActionType.TEXT) {
						final View alertContent = getLayoutInflater().inflate(R.layout.text_dialog, null);
						// populate contacts dropdown
						final Spinner contactSpinner = (Spinner) alertContent.findViewById(R.id.vista_contact);
						final EditText confirmView = (EditText) alertContent.findViewById(R.id.confirm_contact);
						final Button editConfirm = (Button) alertContent.findViewById(R.id.edit_confirm);
						ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext,
								android.R.layout.simple_spinner_item, new ArrayList<String>(mContacts.keySet()));
						arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						contactSpinner.setAdapter(arrayAdapter);
						// set on selection listener to display selected number
						contactSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
								confirmView.setText(mContacts.get(contactSpinner.getSelectedItem()));
							}

							@Override
							public void onNothingSelected(AdapterView<?> parent) {
							}
						});
						// set listener for confirm button
						editConfirm.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								confirmView.setEnabled(true);
							}
						});
						new AlertDialog.Builder(mContext).setTitle("send a field note").setIcon(0)
								.setView(alertContent).setPositiveButton("Send", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface d, int which) {
										EditText noteInput = (EditText) alertContent.findViewById(R.id.vista_note);
										vista.setNote(noteInput.getText().toString());
										String number = confirmView.getText().toString();// mContacts.get(contactSpinner.getSelectedItem());
										Log.d(TAG, "selected number to send to: " + number);
										if (number != null) {
											PendingIntent pi = PendingIntent.getActivity(mContext, 0, null, 0);
											SmsManager sms = SmsManager.getDefault();
											sms.sendTextMessage(number, null, noteInput.getText().toString(), pi, null);
										}
										if (vista.getComplete()) {
											// allow user to move on to the next vista
											markVistaAsCompleted(vista);
										}
									}
								}).create().show();
					}
				}
			});
		}
	}

	private void startCameraIntent(ScenicVistaV2 vista) {
		// check that sdcard is available
		String state = Environment.getExternalStorageState();
		if (!Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			Toast.makeText(this, "make sure SD Card is available before taking photos.", Toast.LENGTH_LONG).show();
			return;
		}
		// save the vista for which we are saving the photo
		mPhotoVista = vista;

		// create an intent for accessing the camera
		Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		ContentValues cameraValues = new ContentValues();
		cameraValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		//todo cameraValues.put(MediaStore.Images.Media.TITLE, vista.getPhotoTitle());
		cameraValues.put(MediaStore.Images.Media.DESCRIPTION, mContext.getResources().getString(R.string.app_name));
		Uri uri = mContext.getContentResolver().insert(Uri.parse("content://" + PhotoProvider.AUTHORITY), cameraValues);

		pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		mPhotoUri = uri;
		// take the picture by invoking the camera activity
		startActivityForResult(pictureIntent, TAKE_PHOTO_ACTIVITY);
	}

	private void markVistaAsCompleted(ScenicVistaV2 vista) {
		mCurrentVista = null;
		// remove vista info views
		findViewById(R.id.hike_layout).setVisibility(View.GONE);
		findViewById(R.id.vista_layout).setVisibility(View.GONE);
		// remove pending intent
		vista.removeIntent(mContext, mLocMgr);
		vista.cancelIntent();
		// save vista info to db
//		vista.save(this, mHike.hashCode());
		// check if the hike is complete
//		todo if (mHike.isComplete()) {
//			completeHike();
//		}
	}

	private void completeHike() {
		// inflate input dialog
		final View dialogView = getLayoutInflater().inflate(
				mHike.getOriginal() ? R.layout.complete_hike_dialog : R.layout.complete_walk_hike_dialog, null);
		new AlertDialog.Builder(this).setTitle("hike completed").setView(dialogView)
				.setPositiveButton("share", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// get user inputs
						EditText username = (EditText) dialogView.findViewById(R.id.hike_username);
						EditText hikeName = (EditText) dialogView.findViewById(R.id.hike_name);
						EditText hikeDesc = (EditText) dialogView.findViewById(R.id.hike_desc);
						mHike.setUsername(username.getText().toString());
						if (mHike.getOriginal()) {
							mHike.setName(hikeName.getText().toString());
							mHike.setDescription(hikeDesc.getText().toString());
						}
//						todo mHike.upload(mContext, mUploadHandler);
					}
				}).create().show();
	}

//	private Handler mUploadHandler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			if (msg.what == NetworkConstants.FAILURE) {
//				// retry
//				showRetryDialog();
//			} else { // success
//				Toast.makeText(mContext, "hike was uploaded successfully", Toast.LENGTH_LONG).show();
//				// get hike id as passed back from server
//				mHike.setId((Integer) msg.obj);
//				// start 'Share Screen' and finish this one
//				Intent i = new Intent(mContext, ShareHikeActivity.class);
//				i.putExtra(Constants.BUNDLE_HIKE, mHike);
//				startActivity(i);
//				finish();
//			}
//		}
//	};

	private void showRetryDialog() {
		new AlertDialog.Builder(this).setTitle("oops")
				.setMessage("something went wrong during the upload.\nwould you like to try again?")
				.setPositiveButton("Re-try", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//mHike.upload(mContext, mUploadHandler);
					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create().show();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}
