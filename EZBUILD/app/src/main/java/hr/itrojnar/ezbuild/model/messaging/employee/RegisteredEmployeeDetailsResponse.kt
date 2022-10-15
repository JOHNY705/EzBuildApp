package hr.itrojnar.ezbuild.model.messaging.employee

import android.os.Parcelable
import hr.itrojnar.ezbuild.model.viewModels.Employee
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegisteredEmployeeDetailsResponse (
    var employee     : Employee       = Employee(),
    var isSuccessful : Boolean        = true,
    var message      : String?        = null
) : Parcelable