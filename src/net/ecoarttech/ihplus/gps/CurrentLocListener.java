package net.ecoarttech.ihplus.gps;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class CurrentLocListener implements LocationListener {
	private static final String TAG = "IH+ - CurrentLocationListener";
	private Handler mHandler;

	public CurrentLocListener(Handler h) {
		this.mHandler = h;
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d(TAG, "location changed! " + location);
		Message msg = new Message();
		msg.what = 1; // TODO
		msg.obj = location;
		mHandler.sendMessage(msg);
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

}
