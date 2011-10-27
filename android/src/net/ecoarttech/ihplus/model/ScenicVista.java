package net.ecoarttech.ihplus.model;

import com.google.android.maps.GeoPoint;

public class ScenicVista {
	private String latitude;
	private String longitude;
	private GeoPoint point;

	public ScenicVista(String lat, String lon) {
		this.latitude = lat;
		this.longitude = lon;
		this.point = new GeoPoint((int) (Double.parseDouble(lat) * 1E6), (int) (Double.parseDouble(lon) * 1E6));

	}

	public GeoPoint getPoint() {
		return point;
	}
}
