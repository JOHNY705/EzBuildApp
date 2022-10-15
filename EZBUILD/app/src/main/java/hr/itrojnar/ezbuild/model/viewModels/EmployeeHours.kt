package hr.itrojnar.ezbuild.model.viewModels

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EmployeeHours (
    @SerializedName("idEmployeeHours"  ) var idEmployeeHours  : Int?    = null,
    @SerializedName("hoursWorked"      ) var hoursWorked      : Int?    = null,
    @SerializedName("dateWorkDone"     ) var dateWorkDone     : String? = null,
    @SerializedName("employeeID"       ) var employeeID       : Int?    = null,
    @SerializedName("employeeFullName" ) var employeeFullName : String? = null
) : Parcelable