package hr.itrojnar.ezbuild.model.messaging.constructionSite

import android.os.Parcelable
import hr.itrojnar.ezbuild.model.viewModels.ConstructionSite
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetConstructionSiteResponse(
    var constructionSite  : ConstructionSite,
    var isSuccessful      : Boolean                      = true,
    val message           : String?                      = null
) : Parcelable
