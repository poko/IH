package net.ecoarttech.ihplus.model;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;

public class Hike {
	private ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();
	private ArrayList<ScenicVista> vistas = new ArrayList<ScenicVista>();

	public void addVista(ScenicVista vista) {
		vistas.add(vista);
	}

	public void addPoint(GeoPoint point) {
		points.add(point);
	}
}
