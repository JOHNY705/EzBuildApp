package hr.itrojnar.ezbuild.model.messaging.constructionSite

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateConstructionSiteRequest (
    var base64Image                  : String?                                 = null,
    var fullAddress                  : String?                                 = null,
    var latitude                     : Double?                                 = null,
    var longitude                    : Double?                                 = null,
    var isActive                     : Boolean?                                = null,
    var constructionSiteManagerID    : Int?                                    = null,
    var firmID                       : Int?                                    = null,
    var employees                    : ArrayList<Int>                          = arrayListOf()
) : Parcelable
