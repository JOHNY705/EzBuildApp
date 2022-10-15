package hr.itrojnar.ezbuild.model.messaging.diaryEntry

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateDiaryEntryRequest(
    var diaryEntry           : String,
    var diaryEntryDate       : String,
    var diaryEntryEmployeeID : Int,
    var constructionSiteID   : Int
) : Parcelable