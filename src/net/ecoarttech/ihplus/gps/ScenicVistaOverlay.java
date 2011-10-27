package net.ecoarttech.ihplus.gps;

import net.ecoarttech.ihplus.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class ScenicVistaOverlay extends ItemizedOverlay {
	private GeoPoint mGeoPoint;
	Paint mPaint = new Paint();
	Bitmap mBitmap;

	public ScenicVistaOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	public ScenicVistaOverlay(Context c, GeoPoint point) {
		super(mBitmap);
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
			canvas.drawBitmap(mBitmap, (float) point.x, (float) point.y, mPaint);
		}
		return super.draw(canvas, mapView, shadow, when);
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
	}

	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

}
