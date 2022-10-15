package hr.itrojnar.ezbuild.model.messaging.warehouse

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import hr.itrojnar.ezbuild.model.viewModels.Equipment
import kotlinx.parcelize.Parcelize

@Parcelize
data class EquipmentForWarehouseResponse (
    @SerializedName("equipment"    ) var equipment    : ArrayList<Equipment> = arrayListOf(),
    @SerializedName("isSuccessful" ) var isSuccessful : Boolean              = true,
    @SerializedName("message"      ) var message      : String?              = null
) : Parcelable