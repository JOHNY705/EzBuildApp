package hr.itrojnar.ezbuild.model.viewModels

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User (
    val id: String = "",
    val email: String = "",
): Parcelable