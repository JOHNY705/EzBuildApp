package hr.itrojnar.ezbuild.model.messaging

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BaseResponse (
    var isSuccessful : Boolean        = true,
    var message      : String?        = null
) : Parcelable