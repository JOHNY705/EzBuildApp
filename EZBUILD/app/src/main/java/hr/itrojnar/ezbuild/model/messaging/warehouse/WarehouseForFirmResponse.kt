package hr.itrojnar.ezbuild.model.messaging.warehouse

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import hr.itrojnar.ezbuild.model.viewModels.Warehouse
import kotlinx.parcelize.Parcelize

@Parcelize
data class WarehouseForFirmResponse(
    @SerializedName("warehouse"    ) var warehouse    : Warehouse  = Warehouse(),
    @SerializedName("isSuccessful" ) var isSuccessful : Boolean    = true,
    @SerializedName("message"      ) var message      : String?    = null
) : Parcelable
