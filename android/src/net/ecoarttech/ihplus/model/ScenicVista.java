package net.ecoarttech.ihplus.model;

import net.ecoarttech.ihplus.db.DBHelper;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.net.Uri;
import android.util.Log;

import com.google.android.maps.GeoPoint;

public class ScenicVista {
	private static final String TAG = "IH+ - ScenicVista";
	public static final String TABLE_NAME = "vistas";
	public static final String COL_HIKE_ID = "hike_id";
	public static final String COL_LAT = "latitude";
	public static final String COL_LNG = "longitude";
	public static final String COL_ACTION_ID = "action_id";
	public static final String COL_NOTE = "note";
	public static final String COL_PHOTO = "photo";
	public static final String COL_DATE = "date";
	private Double latitude;
	private Double longitude;
	private GeoPoint point;
	private Integer actionId;
	private String action;
	private ActionType actionType;
	private String note;
	private Uri photo;
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
		this.actionType = ActionType.valueOf(type.toUpperCase());
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
		if (br != null)
			c.unregisterReceiver(br);
		br = null;
		if (pi != null)
			m.removeProximityAlert(pi);
		pi = null;
	}

	public void setNote(String note) {
		this.note = note;
		complete = note.length() > 0;
	}

	public String getNote() {
		return note;
	}

	public void setPhotoUri(Uri uri) {
		this.photo = uri;
		complete = uri != null;
	}

	public boolean isComplete() {
		return complete;
	}

	public void save(Context context, int hikeId) {
		// save to database.
		DBHelper helper = new DBHelper(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COL_HIKE_ID, hikeId);
		values.put(COL_LAT, latitude);
		values.put(COL_LNG, longitude);
		values.put(COL_ACTION_ID, actionId);
		values.put(COL_NOTE, note);
		if (photo != null)
			values.put(COL_PHOTO, photo.toString());
		db.insert(TABLE_NAME, null, values);
		db.close();
	}

	public void complete() {
		this.complete = true;
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
