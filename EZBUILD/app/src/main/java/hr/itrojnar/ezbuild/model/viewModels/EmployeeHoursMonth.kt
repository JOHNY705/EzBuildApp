package hr.itrojnar.ezbuild.model.viewModels


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EmployeeHoursMonth(
    @SerializedName("hoursWorked"  ) var hoursWorked  : Int?    = null,
    @SerializedName("dateWorkDone" ) var dateWorkDone : String? = null,
    @SerializedName("employeeID"   ) var employeeID   : Int?    = null
) : Parcelable
