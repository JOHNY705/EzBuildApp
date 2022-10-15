package hr.itrojnar.ezbuild.view.adapters

import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.ItemEquipmentLeasedLayoutBinding
import hr.itrojnar.ezbuild.model.viewModels.EquipmentHistory
import hr.itrojnar.ezbuild.model.viewModels.EquipmentHistoryGrouped
import hr.itrojnar.ezbuild.view.fragments.EquipmentFragment
import hr.itrojnar.ezbuild.view.fragments.EquipmentLeasedFragment
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EquipmentLeasedAdapter(
    private val fragment: Fragment,
    private val equipmentHistoryGrouped: ArrayList<EquipmentHistoryGrouped>
) : RecyclerView.Adapter<EquipmentLeasedAdapter.ViewHolder>() {

    class ViewHolder(view: ItemEquipmentLeasedLayoutBinding) : RecyclerView.ViewHolder(view.root) {
        val ivEquipmentLeasedImage = view.ivEquipmentLeasedImage
        val tvItemElEmployee = view.tvItemElEmployee
        val tvItemElNumberOfItems = view.tvItemElNumberOfItems
        val tvItemElDateOfLease = view.tvItemElDateOfLease
        val ivEquipmentLeasedMenu = view.ivEquipmentLeasedMenu
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemEquipmentLeasedLayoutBinding = ItemEquipmentLeasedLayoutBinding.inflate(LayoutInflater.from(fragment.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val leasedEquipment = equipmentHistoryGrouped[position]

        if (leasedEquipment.equipment.size > 0) {
            Glide.with(fragment)
                .asBitmap()
                .load(Base64.decode(leasedEquipment.equipment[0].base64Image, Base64.DEFAULT))
                .transform(CenterCrop(), RoundedCorners(12))
                .into(holder.ivEquipmentLeasedImage)

            val apiFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.")
            val formattedDate = LocalDate.parse(leasedEquipment.dateEquipmentTaken, apiFormatter).format(formatter)

            holder.tvItemElEmployee.text = leasedEquipment.employee.fullName
            holder.tvItemElNumberOfItems.text = leasedEquipment.quantityTaken.toString()
            holder.tvItemElDateOfLease.text = formattedDate
        }

        holder.itemView.setOnClickListener {
            if (fragment is EquipmentLeasedFragment) {
                fragment.leasedEquipmentDetails(leasedEquipment)
            }
        }

        holder.ivEquipmentLeasedMenu.setOnClickListener {
            if (fragment is EquipmentLeasedFragment) {
                val wrapper = ContextThemeWrapper(fragment.context, R.style.PopupMenu)
                val popupMenu = PopupMenu(wrapper, holder.ivEquipmentLeasedMenu, Gravity.RIGHT)
                popupMenu.menuInflater.inflate(R.menu.menu_equipment_leased_adapter, popupMenu.menu)

                popupMenu.setOnMenuItemClickListener {
                    if (it.itemId == R.id.action_leased_equipment_adapter_edit) {
                        fragment.editLeasedEquipment(leasedEquipment)
                    }
                    else if (it.itemId == R.id.action_leased_equipment_adapter_delete) {
                        fragment.deleteLeasedEquipment(leasedEquipment)
                    }
                    true
                }

                try {
                    val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                    fieldMPopup.isAccessible = true
                    val popup = fieldMPopup.get(popupMenu)
                    popup.javaClass
                        .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                        .invoke(popup, true)
                } catch (e: Exception) {
                    Log.e("POPUP MENU", "Error showing menu icons", e)
                }

                popupMenu.show()
            }
        }
    }

    override fun getItemCount(): Int {
        return equipmentHistoryGrouped.size
    }
}