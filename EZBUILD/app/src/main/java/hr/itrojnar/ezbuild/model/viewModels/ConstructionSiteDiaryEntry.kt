package hr.itrojnar.ezbuild.model.viewModels

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ConstructionSiteDiaryEntry (
    var idConstructionSiteDiary : Int,
    var diaryEntry              : String? = null,
    var diaryEntryDate          : String? = null,
    var employeeFullName        : String? = null,
    var constructionSiteID      : Int?    = null
) : Parcelable
