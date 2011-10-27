package net.ecoarttech.ihplus;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import net.ecoarttech.ihplus.gps.CurrentLocationOverlay;
import net.ecoarttech.ihplus.gps.DirectionPathOverlay;
import net.ecoarttech.ihplus.gps.ScenicVistaOverlay;
import net.ecoarttech.ihplus.model.Hike;
import net.ecoarttech.ihplus.model.ScenicVista;
import net.ecoarttech.ihplus.network.DirectionCompletionListener;
import net.ecoarttech.ihplus.network.DirectionsAsyncTask;
import net.ecoarttech.ihplus.network.StartCoordsAsyncTask;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class IHMapActivity extends MapActivity {
	private static final String TAG = "IHMapView";
	public static final String BUNDLE_START = "start";
	public static final String BUNDLE_END = "end";
	private Context mContext;
	private MapView mMapView;
	private ScenicVistaOverlay mScenicVistaOverlay;
	private MapController mMapController;
	private GeoPoint geoPoint;
	private CurrentLocationOverlay mCurrentLocationOverlay;
	private boolean mRouteShown = false;
	private Hike mHike;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		this.mContext = this;
		// setup map view & view elements
		mMapView = (MapView) findViewById(R.id.map_view);
		mMapView.setSatellite(false);
		mScenicVistaOverlay = new ScenicVistaOverlay(getResources().getDrawable(R.drawable.scenic_vista_point));
		mMapView.getOverlays().add(mScenicVistaOverlay);

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
		// get start/end points from bundle
		// get address for random geo points
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

		// create new Hike object
		mHike = new Hike();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mCurrentLocationOverlay.enableMyLocation();
	}

	protected void onPause() {
		super.onPause();
		mCurrentLocationOverlay.disableMyLocation();
	};

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
			// TODO
		} else {
			// generate random number between 2-4 (for 4-8 total scenic vistas)
			Random rand = new Random();
			int vistaAmount = rand.nextInt(2) + 2;// random method (2,3, or 4);
			Log.d(TAG, "Vistas: " + vistaAmount);
			for (int i = 0; i < vistaAmount; i++) {
				int r = rand.nextInt(points.length);
				if (!existingVistas.contains(r)) {
					// create a new scenic vista here!
					String[] lngLat = points[r].split(",");
					ScenicVista vista = new ScenicVista(lngLat[1], lngLat[0]);
					mHike.addVista(vista);
					mScenicVistaOverlay.addVista(new OverlayItem(vista.getPoint(), "Scenic Vista", ""));
					Log.d(TAG, "new vista!" + points[r]);
					existingVistas.add(r);
				}
			}
		}
	}

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
}
