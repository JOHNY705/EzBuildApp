package hr.itrojnar.ezbuild.model.messaging.diaryEntry

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class UpdateDiaryEntryRequest (
    var idConstructionSiteDiaryEntry           : Int,
    var diaryEntry                             : String,
    var diaryEntryDate                         : String,
    var diaryEntryEmployeeID                   : Int
) : Parcelable