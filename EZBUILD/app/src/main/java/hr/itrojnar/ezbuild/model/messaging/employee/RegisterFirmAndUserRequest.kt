package hr.itrojnar.ezbuild.model.messaging.employee

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegisterFirmAndUserRequest (
    var userFullName    : String,
    var userFirebaseUID : String,
    var userEmail       : String,
    var firmName        : String
) : Parcelable