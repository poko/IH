package net.ecoarttech.ihplus;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
		String pairs[] = getDirectionData(from, to);
		drawPath(pairs);

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
		mMapController.setZoom(17);
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

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	private String[] getDirectionData(String srcPlace, String destPlace) {
		String urlString = "http://maps.google.com/maps?f=d&hl=en&saddr="
				+ srcPlace + "&daddr=" + destPlace
				+ "&ie=UTF8&0&om=0&output=kml";
		Log.d("URL", urlString);
		Document doc = null;
		HttpURLConnection urlConnection = null;
		URL url = null;
		String pathConent = "";
		try {

			url = new URL(urlString.toString());
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.connect();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(urlConnection.getInputStream());

		} catch (Exception e) {
		}
		Log.d(TAG, "doc: " + doc);
		// TODO - null check here -- make sure user is connected to the
		// internet!
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
		return tempContent;
	}

	// private void getDirectionData(String srcPlace, String destPlace) {
	// DirectionsAsyncTask task = new DirectionsAsyncTask(this, srcPlace,
	// destPlace, new DirectionCompletionListener() {
	//
	// @Override
	// public void onComplete(String result) {
	// // TODO Auto-generated method stub
	//
	// DocumentBuilderFactory dbf = DocumentBuilderFactory
	// .newInstance();
	// DocumentBuilder db;
	// Document doc = null;
	// try {
	// db = dbf.newDocumentBuilder();
	// doc = db.parse(result);
	// } catch (ParserConfigurationException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (SAXException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// String pathConent = "";
	// Log.d(TAG, "doc: " + doc);
	// if (doc != null) {
	// NodeList nl = doc
	// .getElementsByTagName("LineString");
	// for (int s = 0; s < nl.getLength(); s++) {
	// Node rootNode = nl.item(s);
	// NodeList configItems = rootNode.getChildNodes();
	// for (int x = 0; x < configItems.getLength(); x++) {
	// Node lineStringNode = configItems.item(x);
	// NodeList path = lineStringNode
	// .getChildNodes();
	// pathConent = path.item(0).getNodeValue();
	// }
	// }
	// String[] tempContent = pathConent.split(" ");
	// drawPath(tempContent);
	// }
	// }
	// });
	// task.execute();
	// // String urlString = "http://maps.google.com/maps?f=d&hl=en&saddr="
	// // + srcPlace + "&daddr=" + destPlace
	// // + "&ie=UTF8&0&om=0&output=kml";
	// // Log.d("URL", urlString);
	// // Document doc = null;
	// // HttpURLConnection urlConnection = null;
	// // URL url = null;
	// // try {
	// //
	// // url = new URL(urlString);
	// // urlConnection = (HttpURLConnection) url.openConnection();
	// // urlConnection.setRequestMethod("GET");
	// // urlConnection.setDoOutput(true);
	// // urlConnection.setDoInput(true);
	// // urlConnection.connect();
	// // DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	// // DocumentBuilder db = dbf.newDocumentBuilder();
	// // doc = db.parse(urlConnection.getInputStream());
	// //
	// // } catch (Exception e) {
	// // }
	// //
	// // return tempContent;
	// // } else {
	// // // TODO
	// // return null;
	// // }
	// }
}
