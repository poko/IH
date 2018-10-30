package net.ecoarttech.ihplus.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
public data class HikeV2(var hike_id : Int = -1,
                         var original_hike_id : Int = -1,
                         var name: String = "",
                         var description: String = "",
                         var date: String = "",
                         var username: String = "",
                         var original: Boolean = false,
                         var companion: Boolean = false,
                         var start_lat: Double = 0.0,
                         var start_lng: Double = 0.0,
                         var ip_address: String = "",
                         var vistas: MutableList<ScenicVistaV2>? = null,
                         var points: List<MapPoint>? = null) : Parcelable {

    var complete: Boolean = false

    fun isPartiallyComplete(): Boolean {
        if (vistas == null || vistas?.size == 0)
            return false
        for (vista in vistas!!) {
            if (vista.complete)
                return true
        }
        return false
    }

    fun getVistaByHashCode(code: Int) :ScenicVistaV2?{
        for (vista in vistas.orEmpty()){
            if (vista.hashCode().equals(code))
                return vista
        }
        return null;
    }

    fun addVista(vista: ScenicVistaV2){
        if (vistas == null)
            vistas = ArrayList<ScenicVistaV2>();
        vistas?.add(vista);
    }
}