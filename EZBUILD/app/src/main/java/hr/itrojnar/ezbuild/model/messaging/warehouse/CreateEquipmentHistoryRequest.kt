package hr.itrojnar.ezbuild.model.messaging.warehouse

import com.google.gson.annotations.SerializedName

data class CreateEquipmentHistoryRequest(
    @SerializedName("equipmentHistory" ) var equipmentHistory : ArrayList<EquipmentHistory>,
    @SerializedName("employeeID"       ) var employeeID       : Int,
    @SerializedName("warehouseID"      ) var warehouseID      : Int
)
