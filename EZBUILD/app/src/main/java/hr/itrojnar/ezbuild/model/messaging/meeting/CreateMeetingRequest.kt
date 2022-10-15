package hr.itrojnar.ezbuild.model.messaging.meeting

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateMeetingRequest(
    @SerializedName("title"              ) var title              : String? = null,
    @SerializedName("meetingDate"        ) var meetingDate        : String? = null,
    @SerializedName("meetingStartTime"   ) var meetingStartTime   : String? = null,
    @SerializedName("meetingDuration"    ) var meetingDuration    : String? = null,
    @SerializedName("meetingDescription" ) var meetingDescription : String? = null,
    @SerializedName("employeeId"         ) var employeeId         : Int?    = null
) : Parcelable
