package hr.itrojnar.ezbuild.model.messaging.warehouse

import com.google.gson.annotations.SerializedName

data class UpdateEquipmentHistoryRequest(
    @SerializedName("equipmentHistory"   ) var equipmentHistory   : ArrayList<EquipmentHistory> = arrayListOf(),
    @SerializedName("oldEmployeeID"      ) var oldEmployeeID      : Int,
    @SerializedName("newEmployeeID"      ) var newEmployeeID      : Int,
    @SerializedName("warehouseID"        ) var warehouseID        : Int,
    @SerializedName("dateEquipmentTaken" ) var dateEquipmentTaken : String
)
