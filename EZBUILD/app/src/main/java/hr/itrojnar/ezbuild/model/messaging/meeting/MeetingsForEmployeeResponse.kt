package hr.itrojnar.ezbuild.model.messaging.meeting

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import hr.itrojnar.ezbuild.model.viewModels.Meeting
import kotlinx.parcelize.Parcelize

@Parcelize
data class MeetingsForEmployeeResponse (
    @SerializedName("meetings"     ) var meetings     : ArrayList<Meeting> = arrayListOf(),
    @SerializedName("isSuccessful" ) var isSuccessful : Boolean             = true,
    @SerializedName("message"      ) var message      : String?             = null
) : Parcelable