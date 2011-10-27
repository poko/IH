package net.ecoarttech.ihplus.model;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;

public class Hike {
	private ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();
	private ArrayList<ScenicVista> vistas = new ArrayList<ScenicVista>();

	public void addVista(ScenicVista vista) {
		vistas.add(vista);
		// vistas should now be registered as geo-fences ?

		// and they should get their action items from the server as well.
		// if the server has error, should have some kind of vista info on the phone
	}

	public void addPoint(GeoPoint point) {
		points.add(point);
	}
}
