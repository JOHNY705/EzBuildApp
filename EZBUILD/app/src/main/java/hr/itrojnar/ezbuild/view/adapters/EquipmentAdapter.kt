package hr.itrojnar.ezbuild.view.adapters

import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.ItemEquipmentLayoutBinding
import hr.itrojnar.ezbuild.model.viewModels.Equipment
import hr.itrojnar.ezbuild.view.fragments.EditLeasedEquipmentFragment
import hr.itrojnar.ezbuild.view.fragments.EquipmentFragment
import hr.itrojnar.ezbuild.view.fragments.LeasedEquipmentDetailsFragment
import hr.itrojnar.ezbuild.view.fragments.NewLeaseEquipmentFragment

class EquipmentAdapter(
    private val fragment: Fragment,
    private val equipment: ArrayList<Equipment>
) : RecyclerView.Adapter<EquipmentAdapter.ViewHolder>() {

    class ViewHolder(view: ItemEquipmentLayoutBinding) : RecyclerView.ViewHolder(view.root) {
        val ivEquipmentRvImage = view.ivEquipmentRvImage
        val tvItemEquipmentName = view.tvItemEquipmentName
        val tvItemEquipmentQuantity = view.tvItemEquipmentQuantity
        val tvItemEquipmentDescription = view.tvItemEquipmentDescription
        val tvEquipmentQuantityPlaceholder = view.tvEquipmentQuantityPlaceholder
        val ivEquipmentMenu = view.ivEquipmentMenu
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemEquipmentLayoutBinding = ItemEquipmentLayoutBinding.inflate(LayoutInflater.from(fragment.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val equipmentItem = equipment[position]

        Glide.with(fragment)
            .asBitmap()
            .load(Base64.decode(equipmentItem.base64Image, Base64.DEFAULT))
            .transform(CenterCrop(), RoundedCorners(12))
            .into(holder.ivEquipmentRvImage)

        holder.tvItemEquipmentName.text = equipmentItem.equipmentName

        if (fragment is LeasedEquipmentDetailsFragment) {
            holder.tvEquipmentQuantityPlaceholder.text = fragment.requireContext().getString(R.string.lbl_equipment_leased_quantity)
            holder.ivEquipmentMenu.visibility = View.GONE
        }
        else if (fragment is NewLeaseEquipmentFragment) {
            holder.ivEquipmentMenu.visibility = View.GONE
        }

        holder.tvItemEquipmentQuantity.text = equipmentItem.quantity.toString()
        if (equipmentItem.quantity == 0) {
            holder.tvItemEquipmentQuantity.setTextColor(fragment.resources.getColor(R.color.colorSnackBarError))
        }
        holder.tvItemEquipmentDescription.text = equipmentItem.equipmentDescription

        holder.itemView.setOnClickListener {
            if (fragment is EquipmentFragment) {
                fragment.equipmentDetails(equipmentItem)
            }
            else if (fragment is NewLeaseEquipmentFragment) {
                fragment.addEquipmentItemToLeaseItems(equipmentItem)
            }
            else if (fragment is EditLeasedEquipmentFragment) {
                fragment.addEquipmentItemToLeaseItems(equipmentItem)
            }
        }

        holder.ivEquipmentMenu.setOnClickListener {

            val wrapper = ContextThemeWrapper(fragment.context, R.style.PopupMenu)
            val popupMenu = PopupMenu(wrapper, holder.ivEquipmentMenu, Gravity.RIGHT)
            popupMenu.menuInflater.inflate(R.menu.menu_equipment_adapter, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener {
                if (it.itemId == R.id.action_equipment_adapter_edit) {
                    if (fragment is EquipmentFragment) {
                        fragment.editEquipment(equipmentItem)
                    }
                }
                else if (it.itemId == R.id.action_equipment_adapter_delete) {
                    if (fragment is EquipmentFragment) {
                        fragment.deleteEquipment(equipmentItem)
                    }
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

    override fun getItemCount(): Int {
        return equipment.size
    }
}