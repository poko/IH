package net.ecoarttech.ihplus.model

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Parcelable
import android.util.Log
import kotlinx.android.parcel.Parcelize
import net.ecoarttech.ihplus.activities.IHMapActivity

@Parcelize
public class ScenicVistaV2(var id: Int = -1,
                                var hike_id: Int = -1,
                                var action_id: Int = -1,
                                var longitude: Double = 0.0,
                                var latitude: Double = 0.0,
                                var date: String = "",
                                var note: String = "",
                                var photo: String = "",
                                var verbiage: String = "",
                                var action_type: ActionType? = null,
                                var new_vista_action: Action? = null): Parcelable{

    constructor(lat: Double , lng: Double ) : this(latitude = lat, longitude = lng)


    var visited: Boolean = false
    var pendingIntent: PendingIntent? = null;
    var broadcastReceiver: BroadcastReceiver? = null;
    var complete: Boolean = false

    fun getAction() : Action{
        return new_vista_action ?: Action(-1, action_type, verbiage);
    }


    fun cancelIntent() {
        //		Log.d(TAG, "canceling intents for : " + point);
        broadcastReceiver = null
        pendingIntent = null
    }

    fun removeIntent(c: Context, m: LocationManager) {
        //		Log.d(TAG, "pausing intents for : " + point);
        if (broadcastReceiver != null) {
            try {
                c.unregisterReceiver(broadcastReceiver)
            } catch (e: IllegalArgumentException) {
                Log.e("ScenicVista", "Receiver not registered, failed to unregisster it.")
            }

        }
        if (pendingIntent != null)
            m.removeProximityAlert(pendingIntent)
    }

    fun pauseIntent(c: Context, m: LocationManager) {
        //		Log.d(TAG, "pausing intents for : " + point);
        if (broadcastReceiver != null) {
            try {
                c.unregisterReceiver(broadcastReceiver)
            } catch (e: IllegalArgumentException) {
                Log.e(this.toString(), "Receiver not registered, failed to unregister it.")
            }

        }
        if (pendingIntent != null)
            m.removeProximityAlert(pendingIntent)
    }

    @SuppressLint("MissingPermission")
    fun reenableIntent(c: Context, m: LocationManager) {
        if (!complete) {
            if (broadcastReceiver != null) {
                val filter = IntentFilter(IHMapActivity.PROXIMITY_INTENT + hashCode())
                c.registerReceiver(broadcastReceiver, filter)
            }
            if (pendingIntent != null)
                m.addProximityAlert(latitude, longitude, 25f, -1, pendingIntent)
        }
    }

    override fun hashCode(): Int {
        if (id != -1)
            return id
        else {
            val prime = 31
            var result = 1
            result = prime * result + if (latitude == null) 0 else latitude.hashCode()
            result = prime * result + if (longitude == null) 0 else longitude.hashCode()
            return result
        }
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }
}