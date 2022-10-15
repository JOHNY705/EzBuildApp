package hr.itrojnar.ezbuild.model.messaging.warehouse

import com.google.gson.annotations.SerializedName

data class EquipmentHistory (
    @SerializedName("equipmentID"   ) var equipmentID   : Int,
    @SerializedName("quantityTaken" ) var quantityTaken : Int
)