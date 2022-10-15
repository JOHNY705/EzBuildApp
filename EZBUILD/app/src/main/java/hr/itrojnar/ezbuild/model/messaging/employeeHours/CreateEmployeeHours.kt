package hr.itrojnar.ezbuild.model.messaging.employeeHours

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateEmployeeHours (
    @SerializedName("employeeHours" ) var employeeHours : ArrayList<EmployeeHoursItem> = arrayListOf()
) : Parcelable