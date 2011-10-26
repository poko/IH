package net.ecoarttech.ihplus.gps;

import net.ecoarttech.ihplus.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Projection;

public class CurrentLocationOverlay extends MyLocationOverlay {
	private static final String TAG = "IH+ - CurrentLocationOverlay";
	// private Bitmap mPointDrawable;
	private AnimationDrawable mPointDrawable;
	private Paint paint = new Paint();
	private boolean mAnimStarted = false;

	public CurrentLocationOverlay(Context context, MapView mapView) {
		super(context, mapView);
		// mPointDrawable = BitmapFactory.decodeResource(context.getResources(),
		// R.drawable.gps_indicator);
		mPointDrawable = (AnimationDrawable) context.getResources().getDrawable(
				R.drawable.gps_indicator);
	}

	@Override
	protected void drawMyLocation(Canvas canvas, MapView mapView, Location lastFix,
			GeoPoint myLocation, long when) {
		Log.d(TAG, "Drawing my location");
		Projection p = mapView.getProjection();
		float accuracy = p.metersToEquatorPixels(lastFix.getAccuracy());
		Point loc = p.toPixels(myLocation, null);
		paint.setAntiAlias(true);
		paint.setAlpha(255);
		// canvas.drawBitmap(mPointDrawable, loc.x, loc.y, paint);
		mPointDrawable.draw(canvas);
		if (!mAnimStarted) {
			Log.d(TAG, "starting animation?!");
			mAnimStarted = true;
			mPointDrawable.start();
		}

		// canvas.drawCircle(loc.x, loc.y, 10, paint);
	}

}
