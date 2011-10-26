package net.ecoarttech.ihplus.gps;

import net.ecoarttech.ihplus.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Projection;

public class CurrentLocationOverlay extends MyLocationOverlay {
	private static final int ANIMATION_DURATION = 300;
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
	private Paint paint = new Paint();

	public CurrentLocationOverlay(Context context, MapView mapView) {
		super(context, mapView);
		mGPS1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.gps_indicator1);
		mGPS2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.gps_indicator2);
		mGPS3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.gps_indicator3);
		mGPS4 = BitmapFactory.decodeResource(context.getResources(), R.drawable.gps_indicator4);
		currentBitmap = mGPS1;
	}

	@Override
	protected void drawMyLocation(Canvas canvas, MapView mapView, Location lastFix,
			GeoPoint myLocation, long when) {
		Log.d(TAG, "Drawing my location: when: " + when);
		if (animationState == 1) {
			Log.d(TAG, "animation state is 1.");
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
		float accuracy = p.metersToEquatorPixels(lastFix.getAccuracy());
		Point loc = p.toPixels(myLocation, null);
		paint.setAntiAlias(true);
		paint.setAlpha(255);
		canvas.drawBitmap(currentBitmap, loc.x, loc.y, paint);
		// prompt a redraw for animation?!?
		mapView.postInvalidateDelayed(ANIMATION_DURATION);
	}

	private void incrementBitmap() {
		Log.d(TAG, "incrementing bitmap: " + currentBitmapIndex);
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

}
