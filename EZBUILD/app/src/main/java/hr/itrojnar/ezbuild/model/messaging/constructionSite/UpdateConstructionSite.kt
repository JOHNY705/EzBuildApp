package hr.itrojnar.ezbuild.model.messaging.constructionSite

import android.os.Parcelable
import hr.itrojnar.ezbuild.model.viewModels.ConstructionSite
import kotlinx.parcelize.Parcelize

@Parcelize
data class UpdateConstructionSiteRequest (
    var idConstructionSite             : Int,
    var base64Image                    : String,
    var fullAddress                    : String,
    var latitude                       : Double,
    var longitude                      : Double,
    var isActive                       : Boolean,
    var constructionSiteManagerID      : Int,
    var employees                      : ArrayList<Int>
) : Parcelable

@Parcelize
data class UpdateConstructionSiteResponse(
    var constructionSite  : ConstructionSite,
    var isSuccessful      : Boolean,
    val message           : String
) : Parcelable