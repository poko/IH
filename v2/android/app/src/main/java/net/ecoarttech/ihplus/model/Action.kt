package net.ecoarttech.ihplus.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
public data class Action(var action_id : Int = -1,
                         var action_type: ActionType? = null,
                         var verbiage: String = ""): Parcelable