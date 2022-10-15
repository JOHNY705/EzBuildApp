package hr.itrojnar.ezbuild.model.viewModels

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class EquipmentHistory (
    @SerializedName("employee"           ) var employee           : Employee   = Employee(),
    @SerializedName("dateEquipmentTaken" ) var dateEquipmentTaken : String?    = null,
    @SerializedName("quantityTaken"      ) var quantityTaken      : Int        = 0,
    @SerializedName("equipment"          ) var equipment          : Equipment,
    @SerializedName("warehouseID"        ) var warehouseID        : Int?       = null
) : Parcelable