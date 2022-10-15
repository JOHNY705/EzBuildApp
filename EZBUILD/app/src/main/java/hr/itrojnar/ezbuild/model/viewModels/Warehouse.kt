package hr.itrojnar.ezbuild.model.viewModels

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Warehouse(
    @SerializedName("idWarehouse"      ) var idWarehouse      : Int?      = null,
    @SerializedName("fullAddress"      ) var fullAddress      : String?   = null,
    @SerializedName("warehouseManager" ) var warehouseManager : Employee? = null
) : Parcelable
