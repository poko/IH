package net.ecoarttech.ihplus;

import java.net.URLEncoder;
import java.util.ArrayList;

import net.ecoarttech.ihplus.model.Route;
import net.ecoarttech.ihplus.network.DirectionCompletionListener;
import net.ecoarttech.ihplus.network.DirectionsAsyncTask;
import net.ecoarttech.ihplus.network.MapDirectionCompletionListener;
import net.ecoarttech.ihplus.network.MapDirectionsAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class IHActivity extends MapActivity {
	private static final String TAG = "IHMapView";
	MapView mMapView;
	MapController mMapController;
	GeoPoint geoPoint;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		mMapView = (MapView) findViewById(R.id.map_view);
		mMapView.setSatellite(false);
		String from = URLEncoder.encode("1200 Bob Harrison St Austin, TX");
		String to = URLEncoder.encode("600 W 6th St Austin, TX");
		getMapDirectionData(from, to);
		// String pairs[] = getDirectionData(from, to);
		// drawPath(pairs);

	}

	private void drawPath(String[] pairs) {
		String[] lngLat = pairs[0].split(",");

		// STARTING POINT
		GeoPoint startGP = new GeoPoint(
				(int) (Double.parseDouble(lngLat[1]) * 1E6), (int) (Double
						.parseDouble(lngLat[0]) * 1E6));

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
			gp2 = new GeoPoint((int) (Double.parseDouble(lngLat[1]) * 1E6),
					(int) (Double.parseDouble(lngLat[0]) * 1E6));
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
		DirectionsAsyncTask task = new DirectionsAsyncTask(this, srcPlace,
				destPlace, new DirectionCompletionListener() {

					@Override
					public void onComplete(Document doc) {
						String pathConent = "";
						Log.d(TAG, "doc: " + doc);
						if (doc != null) {
							NodeList nl = doc
									.getElementsByTagName("LineString");
							for (int s = 0; s < nl.getLength(); s++) {
								Node rootNode = nl.item(s);
								NodeList configItems = rootNode.getChildNodes();
								for (int x = 0; x < configItems.getLength(); x++) {
									Node lineStringNode = configItems.item(x);
									NodeList path = lineStringNode
											.getChildNodes();
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
		MapDirectionsAsyncTask task = new MapDirectionsAsyncTask(this,
				srcPlace, destPlace, new MapDirectionCompletionListener() {

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
}
