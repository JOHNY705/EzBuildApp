package hr.itrojnar.ezbuild.model.messaging.warehouse

import com.google.gson.annotations.SerializedName

data class DeleteEquipmentHistoryRequest(
    @SerializedName("employeeId"         ) var employeeId         : Int,
    @SerializedName("dateEquipmentTaken" ) var dateEquipmentTaken : String
)
