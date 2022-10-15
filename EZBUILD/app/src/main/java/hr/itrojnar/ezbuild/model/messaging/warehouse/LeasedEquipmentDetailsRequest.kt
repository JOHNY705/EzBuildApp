package hr.itrojnar.ezbuild.model.messaging.warehouse

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class LeasedEquipmentDetailsRequest(
    @SerializedName("employeeId"         ) var employeeId         : Int,
    @SerializedName("dateEquipmentTaken" ) var dateEquipmentTaken : String
) : Parcelable
