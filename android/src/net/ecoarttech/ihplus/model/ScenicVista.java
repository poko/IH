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
	private ActionType actionType;
	private String note;
	private PendingIntent pi;
	private BroadcastReceiver br;
	private boolean complete = false;

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

	public void setActionType(String type) {
		this.actionType = ActionType.valueOf(type);
	}

	public ActionType getActionType() {
		return actionType;
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

	public void setNote(String note) {
		this.note = note;
		if (note.length() > 0)
			complete = true;
	}

	public String getNote() {
		return note;
	}

	public boolean isComplete() {
		return complete;
	}

	public void complete() {
		this.complete = true;
		// cancelIntent(c, m);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((latitude == null) ? 0 : latitude.hashCode());
		result = prime * result + ((longitude == null) ? 0 : longitude.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScenicVista other = (ScenicVista) obj;
		if (latitude == null) {
			if (other.latitude != null)
				return false;
		} else if (!latitude.equals(other.latitude))
			return false;
		if (longitude == null) {
			if (other.longitude != null)
				return false;
		} else if (!longitude.equals(other.longitude))
			return false;
		return true;
	}

}
