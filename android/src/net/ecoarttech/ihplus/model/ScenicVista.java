package net.ecoarttech.ihplus.model;

import android.content.Context;

import com.google.android.maps.GeoPoint;

public class ScenicVista {
	private Double latitude;
	private Double longitude;
	private GeoPoint point;
	private Integer actionId;
	private String action;

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

	public void complete() {
		// TODO - this.complete = true;
	}
}
