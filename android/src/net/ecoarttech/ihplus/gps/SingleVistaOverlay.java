package net.ecoarttech.ihplus.gps;

import net.ecoarttech.ihplus.R;
import net.ecoarttech.ihplus.model.ScenicVista;
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

	private ScenicVista mVista;
	private GeoPoint mGeoPoint;
	Paint mPaint = new Paint();
	Bitmap mVistaBitmap;
	Bitmap mVisitedBitmap;

	public SingleVistaOverlay(Context c, ScenicVista vista) {
		this.mVista = vista;
		this.mGeoPoint = vista.getPoint();
		this.mVistaBitmap = BitmapFactory.decodeResource(c.getResources(), R.drawable.scenic_vista_point);
		this.mVisitedBitmap = BitmapFactory.decodeResource(c.getResources(), R.drawable.visited_vista);
	}

	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
		Projection projection = mapView.getProjection();
		if (shadow == false) {
			mPaint.setAntiAlias(true);
			Point point = new Point();
			projection.toPixels(mGeoPoint, point);
			Bitmap bitmap = mVista.isComplete() ? mVisitedBitmap : mVistaBitmap;
			canvas.drawBitmap(bitmap, (float) point.x - (bitmap.getWidth() / 2), (float) point.y
					- (bitmap.getHeight() / 2), mPaint);
		}
		return super.draw(canvas, mapView, shadow, when);
	}
}
