package net.ecoarttech.ihplus.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import net.ecoarttech.ihplus.network.UploadHikeTask;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.google.android.maps.GeoPoint;

public class Hike {
	@SuppressWarnings("unused")
	private static final String TAG = "IH+ - Hike";
	private static final String JSON_ID = "hike_id";
	private static final String JSON_NAME = "name";
	private static final String JSON_DESC = "desc";
	private static final String JSON_CREATE_DATE = "date";
	private static final String JSON_USERNAME = "username";
	public static final String TABLE_NAME = "hikes";
	private Double startLat;
	private Double startLng;
	private ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();
	private ArrayList<ScenicVista> vistas = new ArrayList<ScenicVista>();
	private int id;
	private String name;
	private String description;
	private String createDate;
	private String username;

	public Hike() {
	}

	public Hike(int id, String name, String desc, String createDate, String user) {
		this.id = id;
		this.name = name;
		this.description = desc;
		this.createDate = createDate;
		this.username = user;
	}

	public void addVista(ScenicVista vista) {
		if (!vistas.contains(vista))
			vistas.add(vista);
	}

	public void setStartPoints(Double lat, Double lng) {
		this.startLat = lat;
		this.startLng = lng;
	}

	public void addPoint(GeoPoint point) {
		points.add(point);
	}

	public ArrayList<ScenicVista> getVistas() {
		return vistas;
	}

	public ScenicVista getVistaByHashCode(int hashcode) {
		for (ScenicVista vista : vistas) {
			if (vista.hashCode() == hashcode)
				return vista;
		}
		return null;
	}
	
	public int getId(){
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreateDate() {
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = s.parse(createDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.yy");
		return sdf.format(date);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Double getStartLat() {
		return startLat;
	}

	public Double getStartLng() {
		return startLng;
	}

	public boolean isComplete() {
		if (vistas.size() == 0)
			return false;
		for (ScenicVista vista : vistas) {
			if (!vista.isComplete())
				return false;
		}
		return true;
	}

	public String getVistasAsJson(Context context) {
		StringBuilder sb = new StringBuilder("[");
		for (ScenicVista vista : vistas) {
			sb.append(vista.toJson(context));
			sb.append(",");
		}
		sb.deleteCharAt(sb.length() - 1); // remove last comma
		sb.append("]");
		return sb.toString();
	}

	public void upload(Context context, Handler completionListener) {
		new UploadHikeTask(context, this, completionListener).execute();
	}

	public static Hike fromJson(JSONObject json) {
		return new Hike(json.optInt(JSON_ID), json.optString(JSON_NAME), json.optString(JSON_DESC), json.optString(JSON_CREATE_DATE), json
				.optString(JSON_USERNAME));
	}
}
