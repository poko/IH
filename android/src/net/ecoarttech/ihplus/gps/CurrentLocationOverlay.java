package net.ecoarttech.ihplus.gps;

import net.ecoarttech.ihplus.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import android.os.Handler;
import android.os.Message;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Projection;

public class CurrentLocationOverlay extends MyLocationOverlay {
	// public static final String LAT = "lat";
	// public static final String LNG = "lng";
	private static final int ANIMATION_DURATION = 200;
	@SuppressWarnings("unused")
	private static final String TAG = "IH+ - CurrentLocationOverlay";
	private int animationState = 1; // 1 for animating but need a start time;
	// and 2 for animating and have start time
	private long startTime;
	private int currentBitmapIndex = 0;
	private Bitmap currentBitmap;
	private Bitmap mGPS1;
	private Bitmap mGPS2;
	private Bitmap mGPS3;
	private Bitmap mGPS4;
	private int bitWidth;
	private int bitHeight;
	private Paint paint = new Paint();
	private Handler callback;
	private boolean callbackActive;

	public CurrentLocationOverlay(Context context, MapView mapView) {
		super(context, mapView);
		mGPS1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.gps_indicator1);
		mGPS2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.gps_indicator2);
		mGPS3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.gps_indicator3);
		mGPS4 = BitmapFactory.decodeResource(context.getResources(), R.drawable.gps_indicator4);
		currentBitmap = mGPS1;
		bitWidth = currentBitmap.getWidth() / 2;
		bitHeight = currentBitmap.getHeight() / 2;
	}

	@Override
	protected void drawMyLocation(Canvas canvas, MapView mapView, Location lastFix, GeoPoint myLocation, long when) {
		// Log.d(TAG, "Drawing my location: when: " + when);
		if (callback != null && callbackActive && myLocation != null) {
			// Bundle data = new Bundle();
			// data.putDouble(LAT, myLocation.getLatitudeE6() / 1E6);
			// data.putDouble(LNG, myLocation.getLongitudeE6() / 1E6);
			Message msg = new Message();
			msg.obj = myLocation;
			// msg.setData(data);
			callback.sendMessage(msg);
		}
		if (animationState == 1) {
			startTime = when;
			animationState = 2;
		}
		if (animationState == 2) {
			if (when - startTime > ANIMATION_DURATION) {
				// set drawable to next
				incrementBitmap();
			}
		}

		// draw current bitmap
		Projection p = mapView.getProjection();
		// float accuracy = p.metersToEquatorPixels(lastFix.getAccuracy());
		Point loc = p.toPixels(myLocation, null);
		paint.setAntiAlias(true);
		paint.setAlpha(255);
		canvas.drawBitmap(currentBitmap, loc.x - bitWidth, loc.y - bitHeight, paint);
		// prompt a redraw for animation
		mapView.postInvalidateDelayed(ANIMATION_DURATION - 100);
	}

	private void incrementBitmap() {
		animationState = 1;
		switch (currentBitmapIndex) {
		case 0: {
			currentBitmap = mGPS1;
			currentBitmapIndex = 1;
			break;
		}
		case 1: {
			currentBitmap = mGPS2;
			currentBitmapIndex = 2;
			break;
		}
		case 2: {
			currentBitmap = mGPS3;
			currentBitmapIndex = 3;
			break;
		}
		case 3: {
			currentBitmap = mGPS4;
			currentBitmapIndex = 0;
			break;
		}
		}
	}

	public void setCallback(Handler h) {
		this.callbackActive = true;
		this.callback = h;
	}

	public void callbackReceived() {
		this.callbackActive = false;
	}
}
