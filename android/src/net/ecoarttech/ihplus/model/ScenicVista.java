package net.ecoarttech.ihplus.model;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;

import net.ecoarttech.ihplus.IHMapActivity;
import net.ecoarttech.ihplus.db.DBHelper;

import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.google.android.maps.GeoPoint;

public class ScenicVista implements Serializable {
	private static final long serialVersionUID = -4922122191674605309L;
	private static final String TAG = "IH+ - ScenicVista";
	public static final String TABLE_NAME = "vistas";
	public static final String COL_HIKE_ID = "hike_id";
	public static final String COL_LAT = "latitude";
	public static final String COL_LNG = "longitude";
	public static final String COL_ACTION_ID = "action_id";
	public static final String COL_NOTE = "note";
	public static final String COL_PHOTO = "photo";
	public static final String COL_DATE = "date";
	private long id;
	private Double latitude;
	private Double longitude;
	private transient GeoPoint point;
	private Integer actionId;
	private String action;
	private ActionType actionType;
	private String note;
	private transient Uri photo;
	private String photoPath;
	private PendingIntent pi;
	private BroadcastReceiver br;
	private boolean complete = false;
	private boolean isEnd = false;

	public ScenicVista(String lat, String lon) {
		this.latitude = Double.parseDouble(lat);
		this.longitude = Double.parseDouble(lon);
		this.point = new GeoPoint((int) (latitude * 1E6), (int) (longitude * 1E6));
	}

	public static ScenicVista newFromJson(JSONObject json) throws JSONException {
		ScenicVista vista = new ScenicVista(json.optString(COL_LAT), json.optString(COL_LNG));
		JSONObject newAction = json.optJSONObject("new_vista_action");
		if (newAction != null) {
			vista.setActionId(newAction.getInt("action_id"));
			vista.setActionType(newAction.getString("action_type"));
			vista.setAction(newAction.getString("verbiage"));
		} else { // downloading an existing vista action
			vista.setActionId(json.getInt("action_id"));
			vista.setActionType(json.getString("action_type"));
			vista.setAction(json.getString("verbiage"));
			vista.setNote(json.optString("note"));
			vista.setPhotoUri(json.optString("photo"));
		}
		return vista;
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
		return ActionType.PHOTO;// actionType;
	}

	public String getPhotoTitle() {
		return "IH_" + point;// latitude + "_" + longitude;
	}

	public FileBody getUploadFile(Context c) {
		if (photo != null) {
			try {
				BitmapFactory.Options opts = new Options();
				opts.inSampleSize = 4;
				Bitmap scaled = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()
						+ photo.getEncodedPath(), opts);
				FileOutputStream os;
				os = c.openFileOutput(getPhotoTitle() + ".jpg", Context.MODE_PRIVATE);
				scaled.compress(CompressFormat.JPEG, 90, os);
				return new FileBody(c.getFileStreamPath(getPhotoTitle() + ".jpg"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public void setPendingIntentAndReceiver(PendingIntent p, BroadcastReceiver b) {
		this.pi = p;
		this.br = b;
	}

	public void cancelIntent() {
		Log.d(TAG, "canceling intents for : " + point);
		br = null;
		pi = null;
	}

	public void pauseIntent(Context c, LocationManager m) {
		Log.d(TAG, "pausing intents for : " + point);
		if (br != null) {
			try {
				c.unregisterReceiver(br);
			} catch (IllegalArgumentException e) {
				Log.e(TAG, "Receiver not registered, failed to unregisster it.");
			}
		}
		if (pi != null)
			m.removeProximityAlert(pi);
	}

	public void reenableIntent(Context c, LocationManager m) {
		if (!complete) {
			if (br != null) {
				IntentFilter filter = new IntentFilter(IHMapActivity.PROXIMITY_INTENT + hashCode());
				c.registerReceiver(br, filter);
			}
			if (pi != null)
				m.addProximityAlert(latitude, longitude, 25, -1, pi);
		}
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

	public void setPhotoUri(String path) {
		if (path != null)
			this.photoPath = path;
	}

	public String getPhotoUrl() {
		return photoPath;
	}

	public void setIsEnd() {
		isEnd = true;
	}

	public boolean isEndVista() {
		return isEnd;
	}

	public boolean isComplete() {
		return complete;
	}

	public void save(Context context, int hikeId) {
		// save to database.
		SQLiteDatabase db = DBHelper.getDB(context);
		ContentValues values = new ContentValues();
		values.put(COL_HIKE_ID, hikeId);
		values.put(COL_LAT, latitude);
		values.put(COL_LNG, longitude);
		values.put(COL_ACTION_ID, actionId);
		values.put(COL_NOTE, note);
		if (photo != null)
			values.put(COL_PHOTO, photo.toString());
		id = db.insert(TABLE_NAME, null, values);
		db.close();
	}

	public void complete() {
		this.complete = true;
	}

	public String toJson(Context context) {
		SQLiteDatabase db = DBHelper.getDB(context);
		Cursor query = db.query(TABLE_NAME, null, "id=" + id, null, null, null, null);
		String date = "";
		if (query.moveToFirst()) {
			date = query.getString(query.getColumnIndex(COL_DATE));
		}
		JSONObject json = new JSONObject();
		try {
			json.put(COL_LAT, latitude);
			json.put(COL_LNG, longitude);
			json.put(COL_ACTION_ID, actionId);
			json.put(COL_NOTE, note);
			if (photo != null)
				json.put(COL_PHOTO, photo.getEncodedPath());
			json.put(COL_DATE, date);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		query.close();
		db.close();
		return json.toString();
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
