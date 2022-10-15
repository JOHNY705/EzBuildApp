package hr.itrojnar.ezbuild.model.messaging.warehouse

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UpdateEquipmentRequest (
    @SerializedName("idEquipment"          ) var idEquipment          : Int?    = null,
    @SerializedName("base64Image"          ) var base64Image          : String? = null,
    @SerializedName("equipmentName"        ) var equipmentName        : String? = null,
    @SerializedName("quantity"             ) var quantity             : Int?    = null,
    @SerializedName("equipmentDescription" ) var equipmentDescription : String? = null
) : Parcelable