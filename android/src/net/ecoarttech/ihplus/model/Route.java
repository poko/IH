package net.ecoarttech.ihplus.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;

public class Route {
	private ArrayList<GeoPoint> points;

	public Route(JSONObject routeJson) {
		points = new ArrayList<GeoPoint>();
		try {
			// TODO - get copyrights
			JSONArray legs = routeJson.getJSONArray("legs");
			for (int l = 0; l < legs.length(); l++) {
				// get steps
				JSONObject leg = legs.getJSONObject(l);
				JSONArray steps = leg.getJSONArray("steps");
				for (int s = 0; s < steps.length(); s++) {
					JSONObject step = steps.getJSONObject(s);
					JSONObject pointLocation;
					if (s == 0 && l == 0) {
						pointLocation = step.getJSONObject("start_location");
					} else {
						pointLocation = step.getJSONObject("end_location");
					}
					String lat = pointLocation.getString("lat");
					String lng = pointLocation.getString("lng");
					GeoPoint point = new GeoPoint((int) (Double
							.parseDouble(lat) * 1E6), (int) (Double
							.parseDouble(lng) * 1E6));
					points.add(point);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<GeoPoint> getPoints() {
		return points;
	}
}
