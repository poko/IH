package net.ecoarttech.ihplus.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
public data class MapPoint(var id : Int = -1,
                           var hike_id : Int = -1,
                           var indx : Int = -1,
                           var longitude: Int = -1,
                           var latitude: Int = -1): Parcelable