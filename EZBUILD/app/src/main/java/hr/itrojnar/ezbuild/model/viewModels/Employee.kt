package hr.itrojnar.ezbuild.model.viewModels

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Employee (
    var idEmployee         : Int?    = null,
    var firebaseID         : String? = null,
    var fullName           : String? = null,
    var email              : String? = null,
    var phone              : String? = null,
    var firmID             : Int?    = null,
    var employeeTypeID     : Int?    = null,
    var constructionSiteID : Int?    = null
) : Parcelable