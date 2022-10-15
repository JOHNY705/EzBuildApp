package hr.itrojnar.ezbuild.model.messaging.employeeHours

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import hr.itrojnar.ezbuild.model.viewModels.EmployeeHours
import kotlinx.parcelize.Parcelize

@Parcelize
data class EmployeeHoursResponse (
    @SerializedName("employeeHours" ) var employeeHours : ArrayList<EmployeeHours> = arrayListOf(),
    @SerializedName("isSuccessful"  ) var isSuccessful  : Boolean                  = true,
    @SerializedName("message"       ) var message       : String?                  = null
) : Parcelable