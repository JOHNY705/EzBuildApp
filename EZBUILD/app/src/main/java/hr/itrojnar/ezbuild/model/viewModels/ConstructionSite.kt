package hr.itrojnar.ezbuild.model.viewModels
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ConstructionSite (
    var idConstructionSite           : Int,
    var base64Image                  : String?                                 = null,
    var fullAddress                  : String?                                 = null,
    var latitude                     : Double,
    var longitude                    : Double,
    var isActive                     : Boolean?                                = null,
    var constructionSiteManager      : Employee?                               = Employee(),
    var constructionSiteDiaryEntries : ArrayList<ConstructionSiteDiaryEntry>   = arrayListOf(),
    var employees                    : ArrayList<Employee>                     = arrayListOf()
) : Parcelable