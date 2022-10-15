package hr.itrojnar.ezbuild.model.viewModels

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EquipmentHistoryGrouped (
    var employee           : Employee             = Employee(),
    var dateEquipmentTaken : String?              = null,
    var quantityTaken      : Int                  = 0,
    var equipment          : ArrayList<Equipment> = arrayListOf(),
    var warehouseID        : Int?                 = null
) : Parcelable