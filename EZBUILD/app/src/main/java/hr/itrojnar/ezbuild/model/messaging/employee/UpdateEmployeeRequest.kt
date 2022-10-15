package hr.itrojnar.ezbuild.model.messaging.employee

import com.google.gson.annotations.SerializedName

data class UpdateEmployeeRequest(
    @SerializedName("idEmployee" ) var idEmployee : Int,
    @SerializedName("fullName"   ) var fullName   : String,
    @SerializedName("phone"      ) var phone      : String
)
