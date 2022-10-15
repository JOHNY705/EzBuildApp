package hr.itrojnar.ezbuild.model.messaging.warehouse

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import hr.itrojnar.ezbuild.model.viewModels.EquipmentHistory
import kotlinx.parcelize.Parcelize

@Parcelize
data class EquipmentHistoryForWarehouseResponse (
    @SerializedName("equipmentHistory" ) var equipmentHistory : ArrayList<EquipmentHistory> = arrayListOf(),
    @SerializedName("isSuccessful"     ) var isSuccessful     : Boolean                     = true,
    @SerializedName("message"          ) var message          : String?                     = null
) : Parcelable