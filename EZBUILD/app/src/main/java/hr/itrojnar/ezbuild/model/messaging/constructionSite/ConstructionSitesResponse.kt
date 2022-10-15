package hr.itrojnar.ezbuild.model.messaging.constructionSite

import android.os.Parcelable
import hr.itrojnar.ezbuild.model.viewModels.ConstructionSite
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConstructionSitesResponse (
    var constructionSites : ArrayList<ConstructionSite>  = arrayListOf(),
    var isSuccessful      : Boolean                      = true,
    var message           : String?                      = null
) : Parcelable