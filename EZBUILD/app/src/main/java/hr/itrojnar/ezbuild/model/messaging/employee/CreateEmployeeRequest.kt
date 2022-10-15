package hr.itrojnar.ezbuild.model.messaging.employee

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateEmployeeRequest (
    @SerializedName("firebaseID"         ) var firebaseID         : String? = null,
    @SerializedName("fullName"           ) var fullName           : String? = null,
    @SerializedName("email"              ) var email              : String? = null,
    @SerializedName("phone"              ) var phone              : String? = null,
    @SerializedName("firmID"             ) var firmID             : Int?    = null,
    @SerializedName("employeeTypeID"     ) var employeeTypeID     : Int?    = null
) : Parcelable