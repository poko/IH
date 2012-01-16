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

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
		// get start/end points from bundle
		if (randomPoint) {
			// get address for random geo points
			getRandomAddress();
		} else {
			getDirectionData(mStart, mEnd);
		}
	}

	private void getRandomAddress() {
		StartCoordsAsyncTask startTask = new StartCoordsAsyncTask(this, mStart, coordsListener);
		startTask.execute();
	}

	private DirectionCompletionListener coordsListener = new DirectionCompletionListener() {

		@Override
		public void onComplete(Document doc) {
			if (doc != null) {
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
					getDirectionData(mStart, to);
					getDirectionData(to, mEnd);
				} catch (IOException e) {
					e.printStackTrace();
					displayRetryDialog();
				}
			} else {
				// display retry dialog.
				displayRetryDialog();
			}
		}
	};

	private void displayRetryDialog() {
		new AlertDialog.Builder(this).setTitle("oops").setMessage(
				"there was an error generating your hike.\ntry again?").setPositiveButton("Retry",
				new DialogInterface.OnClickListener() {

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
								if (mPathCalls == 1) {
									// add the random mid-point to scenic vistas (addVista() call will prevent double
									// vistas)
									createNewVista(tempContent[tempContent.length - 1]);
								}
								if (mPathCalls == 2) {
									drawVistasAndDownloadTasks();
								}
							} else {
								// we're done!
								drawVistasAndDownloadTasks();
							}
						} else {
							displayRetryDialog();
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
		ScenicVista vista = new ScenicVista(lngLat[1], lngLat[0]);
		mHike.addVista(vista);
		Log.d(TAG, "new vista!" + coordsStr);
	}

	private void drawVistasAndDownloadTasks() {
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
					v
							.setAction(cursor.getString(cursor
									.getColumnIndex(NetworkConstants.RESPONSE_JSON_VISTAS_VERBIAGE)));
					v
							.setActionType(cursor.getString(cursor
									.getColumnIndex(NetworkConstants.RESPONSE_JSON_VISTAS_TYPE)));
				}
				cursor.close();
			}
			// enable all the Vista proximity alerts
			enableVistaProximityAlerts();
		}
	};

	private static double getRandomOffset() {
		double num = Math.random() * (.009);
		double offset = Math.floor(num * 1000 + 0.5) / 1000;
		int i = (offset / .001) % 2 == 0 ? 1 : -1;
		return offset * i;
	}
}
