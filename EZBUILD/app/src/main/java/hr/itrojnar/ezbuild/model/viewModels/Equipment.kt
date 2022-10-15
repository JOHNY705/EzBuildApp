package hr.itrojnar.ezbuild.model.viewModels

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Equipment(
    @SerializedName("idEquipment"          ) var idEquipment          : Int?,
    @SerializedName("base64Image"          ) var base64Image          : String,
    @SerializedName("equipmentName"        ) var equipmentName        : String,
    @SerializedName("quantity"             ) var quantity             : Int,
    @SerializedName("equipmentDescription" ) var equipmentDescription : String
) : Parcelable
