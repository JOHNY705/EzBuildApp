package hr.itrojnar.ezbuild.model.messaging.warehouse

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateUpdateWarehouseRequest(
    @SerializedName("firmID"             ) var firmID             : Int?    = null,
    @SerializedName("fullAddress"        ) var fullAddress        : String? = null,
    @SerializedName("warehouseManagerID" ) var warehouseManagerID : Int?    = null
) : Parcelable
