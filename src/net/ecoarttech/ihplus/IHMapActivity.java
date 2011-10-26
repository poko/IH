package net.ecoarttech.ihplus;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.ecoarttech.ihplus.gps.CurrentLocListener;
import net.ecoarttech.ihplus.model.Route;
import net.ecoarttech.ihplus.network.DirectionCompletionListener;
import net.ecoarttech.ihplus.network.DirectionsAsyncTask;
import net.ecoarttech.ihplus.network.MapDirectionCompletionListener;
import net.ecoarttech.ihplus.network.MapDirectionsAsyncTask;
import net.ecoarttech.ihplus.network.StartCoordsAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class IHMapActivity extends MapActivity {
	private static final String TAG = "IHMapView";
	public static final String BUNDLE_START = "start";
	public static final String BUNDLE_END = "end";
	private Context mContext;
	private MapView mMapView;
	private MapController mMapController;
	private GeoPoint geoPoint;
	private GeoPoint mCurrentLocation;
	private LocationManager mLocMgr;
	private CurrentLocListener mCurrLocListener;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		this.mContext = this;
		// start current location listener
		mLocMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
		mCurrLocListener = new CurrentLocListener(newLocationHandler);
		mLocMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mCurrLocListener);
		mMapView = (MapView) findViewById(R.id.map_view);
		mMapView.setSatellite(false);

		Bundle extras = getIntent().getExtras();
		final String start = URLEncoder.encode(extras.getString(BUNDLE_START));
		final String end = URLEncoder.encode(extras.getString(BUNDLE_END));
		// get start/end points from bundle
		// get address for random geo points
		StartCoordsAsyncTask startTask = new StartCoordsAsyncTask(this, start,
				new DirectionCompletionListener() {

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

	Handler newLocationHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.d(TAG, "new location, mappy ol' pal!: " + msg.obj);
		}
	};

	private void drawPath(String[] pairs) {
		String[] lngLat = pairs[0].split(",");

		// STARTING POINT
		GeoPoint startGP = new GeoPoint((int) (Double.parseDouble(lngLat[1]) * 1E6), (int) (Double
				.parseDouble(lngLat[0]) * 1E6));

		mMapController = mMapView.getController();
		geoPoint = startGP;
		mMapController.setCenter(geoPoint); // TODO - center on user's location?
		mMapController.setZoom(15);
		mMapView.getOverlays().add(new DirectionPathOverlay(startGP, startGP));

		// NAVIGATE THE PATH
		GeoPoint gp1;
		GeoPoint gp2 = startGP;
		Log.d(TAG, "pairs amount:" + pairs.length);
		for (int i = 1; i < pairs.length; i++) {

			lngLat = pairs[i].split(",");
			gp1 = gp2;
			// watch out! For GeoPoint, first:latitude, second:longitude
			gp2 = new GeoPoint((int) (Double.parseDouble(lngLat[1]) * 1E6), (int) (Double
					.parseDouble(lngLat[0]) * 1E6));
			mMapView.getOverlays().add(new DirectionPathOverlay(gp1, gp2));
			Log.d("xxx", "pair:" + pairs[i]);
		}

		// END POINT
		mMapView.getOverlays().add(new DirectionPathOverlay(gp2, gp2));

		mMapView.getController().animateTo(startGP);
		mMapView.setBuiltInZoomControls(true);
		mMapView.displayZoomControls(true);
	}

	private void drawMapPath(Route route) {
		ArrayList<GeoPoint> points = route.getPoints();
		// STARTING POINT
		GeoPoint startGP = points.get(0);
		// new GeoPoint(
		// (int) (Double.parseDouble(lngLat[1]) * 1E6), (int) (Double
		// .parseDouble(lngLat[0]) * 1E6));

		mMapController = mMapView.getController();
		geoPoint = startGP;
		mMapController.setCenter(geoPoint); // TODO - center on user's location?
		mMapController.setZoom(16);
		mMapView.getOverlays().add(new DirectionPathOverlay(startGP, startGP));

		// NAVIGATE THE PATH
		GeoPoint gp1;
		GeoPoint gp2 = startGP;

		for (int i = 1; i < points.size(); i++) {
			// lngLat = pairs[i].split(",");
			gp1 = gp2;
			// watch out! For GeoPoint, first:latitude, second:longitude
			gp2 = points.get(i);
			// new GeoPoint((int) (Double.parseDouble(lngLat[1]) * 1E6),
			// (int) (Double.parseDouble(lngLat[0]) * 1E6));
			mMapView.getOverlays().add(new DirectionPathOverlay(gp1, gp2));
			Log.d("xxx", "point:" + gp2);
		}

		// END POINT
		mMapView.getOverlays().add(new DirectionPathOverlay(gp2, gp2));

		mMapView.getController().animateTo(startGP);
		mMapView.setBuiltInZoomControls(true);
		mMapView.displayZoomControls(true);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
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
							drawPath(tempContent);
						}
					}
				});
		task.execute();
	}

	private void getMapDirectionData(String srcPlace, String destPlace) {
		MapDirectionsAsyncTask task = new MapDirectionsAsyncTask(this, srcPlace, destPlace,
				new MapDirectionCompletionListener() {

					@Override
					public void onComplete(String result) {
						Log.d(TAG, "done");
						try {
							JSONObject json = new JSONObject(result);
							JSONArray routes = json.getJSONArray("routes");
							if (routes.length() > 0) {
								Route route = new Route(routes.getJSONObject(0));
								drawMapPath(route);
							} else {
								// TODO - POOP
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
		task.execute();
	}

	private static double getRandomOffset() {
		double num = Math.random() * (.009);
		double offset = Math.floor(num * 1000 + 0.5) / 1000;
		int i = (offset / .001) % 2 == 0 ? 1 : -1;
		return offset * i;
	}
}
