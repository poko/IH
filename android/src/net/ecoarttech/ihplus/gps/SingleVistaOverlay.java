package net.ecoarttech.ihplus.gps;

import net.ecoarttech.ihplus.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class SingleVistaOverlay extends Overlay {

	private GeoPoint mGeoPoint;
	Paint mPaint = new Paint();
	Bitmap mBitmap;

	public SingleVistaOverlay(Context c, GeoPoint point) {
		this.mGeoPoint = point;
		this.mBitmap = BitmapFactory.decodeResource(c.getResources(), R.drawable.scenic_vista_point);
	}

	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
		Projection projection = mapView.getProjection();
		if (shadow == false) {
			mPaint.setAntiAlias(true);
			Point point = new Point();
			projection.toPixels(mGeoPoint, point);

			canvas.drawBitmap(mBitmap, (float) point.x - (mBitmap.getWidth() / 2), (float) point.y
					- (mBitmap.getHeight() / 2), mPaint);
		}
		return super.draw(canvas, mapView, shadow, when);
	}
}
