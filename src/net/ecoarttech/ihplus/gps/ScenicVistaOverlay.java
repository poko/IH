package net.ecoarttech.ihplus.gps;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class ScenicVistaOverlay extends ItemizedOverlay<OverlayItem> {
	private static final String TAG = "IH+ - ScenicVistaOverlay";
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	Paint mPaint = new Paint();

	public ScenicVistaOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
		Log.d(TAG, "drawing itemizedOverlay");
		// Projection projection = mapView.getProjection();
		// if (shadow == false) {
		// mPaint.setAntiAlias(true);
		// Point point = new Point();
		// projection.toPixels(mGeoPoint, point);
		// canvas.drawBitmap(mBitmap, (float) point.x, (float) point.y, mPaint);
		// }
		return super.draw(canvas, mapView, shadow, when);
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	public void addVista(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();

	}

}
