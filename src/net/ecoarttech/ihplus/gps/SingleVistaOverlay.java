package net.ecoarttech.ihplus.gps;

import android.graphics.Bitmap;
import android.graphics.Paint;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class SingleVistaOverlay extends OverlayItem {
	public SingleVistaOverlay(GeoPoint point, String title, String snippet) {
		super(point, title, snippet);
		// TODO Auto-generated constructor stub
	}

	private GeoPoint mGeoPoint;
	Paint mPaint = new Paint();
	Bitmap mBitmap;

	// public SingleVistaOverlay(Context c, GeoPoint point) {
	// this.mGeoPoint = point;
	// this.mBitmap = BitmapFactory.decodeResource(c.getResources(),
	// R.drawable.scenic_vista_point);
	// }
	//
	// @Override
	// public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long
	// when) {
	// Projection projection = mapView.getProjection();
	// if (shadow == false) {
	// mPaint.setAntiAlias(true);
	// Point point = new Point();
	// projection.toPixels(mGeoPoint, point);
	// canvas.drawBitmap(mBitmap, (float) point.x, (float) point.y, mPaint);
	// }
	// return super.draw(canvas, mapView, shadow, when);
	// }
}
