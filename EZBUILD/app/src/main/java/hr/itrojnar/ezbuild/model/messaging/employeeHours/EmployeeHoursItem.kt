package hr.itrojnar.ezbuild.model.messaging.employeeHours

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class EmployeeHoursItem (
    @SerializedName("hoursWorked"  ) var hoursWorked  : Int?    = null,
    @SerializedName("dateWorkDone" ) var dateWorkDone : String? = null,
    @SerializedName("employeeId"   ) var employeeId   : Int?    = null
) : Parcelable