package net.ecoarttech.ihplus.gps;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class DirectionPathOverlay extends Overlay {
	@SuppressWarnings("unused")
	private static final String TAG = "IH+ - DirectionPathOverlay";
	private GeoPoint gp1;
	private GeoPoint gp2;
	Paint paint = new Paint();

	public DirectionPathOverlay(GeoPoint gp1, GeoPoint gp2) {
		this.gp1 = gp1;
		this.gp2 = gp2;
	}

	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
		// change path width based on zoom level
		int lineWidth = getLineWidth(mapView.getZoomLevel());
		Projection projection = mapView.getProjection();
		if (shadow == false) {
			paint.setAntiAlias(true);
			Point point = new Point();
			projection.toPixels(gp1, point);
			paint.setColor(0xAA21D9FC);
			Point point2 = new Point();
			projection.toPixels(gp2, point2);
			paint.setStrokeWidth(lineWidth);
			canvas.drawLine((float) point.x, (float) point.y, (float) point2.x, (float) point2.y, paint);
		}
		return super.draw(canvas, mapView, shadow, when);
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
	}

	private int getLineWidth(int zoom) {
		int lineWidth = 3;
		if (zoom > 16) {
			switch (zoom) {
			case 17: {
				lineWidth = 4;
				break;
			}
			case 18: {
				lineWidth = 5;
				break;
			}
			default: {
			}
			case 19: {
				lineWidth = 6;
				break;
			}
			}
		}
		return lineWidth;
	}

}
