package net.ecoarttech.ihplus.model;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.maps.GeoPoint;

public class ScenicVista {
	private static final String TAG = "IH+ - ScenicVista";
	private Double latitude;
	private Double longitude;
	private GeoPoint point;
	private Integer actionId;
	private String action;
	private PendingIntent pi;
	private BroadcastReceiver br;

	public ScenicVista(Context context, String lat, String lon) {
		this.latitude = Double.parseDouble(lat);
		this.longitude = Double.parseDouble(lon);
		this.point = new GeoPoint((int) (latitude * 1E6), (int) (longitude * 1E6));
	}

	public Double getLat() {
		return latitude;
	}

	public Double getLong() {
		return longitude;
	}

	public GeoPoint getPoint() {
		return point;
	}

	public void setActionId(int id) {
		this.actionId = id;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}

	public void setPendingIntentAndReceiver(PendingIntent p, BroadcastReceiver b) {
		this.pi = p;
		this.br = b;
	}

	public void cancelIntent(Context c, LocationManager m) {
		Log.d(TAG, "canceling intent for : " + point);
		c.unregisterReceiver(br);
		m.removeProximityAlert(pi);
	}

	public void complete() {
		// TODO - this.complete = true;
		// cancelIntent(c, m);
	}
}
