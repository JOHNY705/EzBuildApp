package hr.itrojnar.ezbuild.model.messaging.employee

import hr.itrojnar.ezbuild.model.viewModels.Employee
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EmployeesForFirmResponse (
    var employees    : ArrayList<Employee> = arrayListOf(),
    var isSuccessful : Boolean        = true,
    var message      : String?        = null
) : Parcelable