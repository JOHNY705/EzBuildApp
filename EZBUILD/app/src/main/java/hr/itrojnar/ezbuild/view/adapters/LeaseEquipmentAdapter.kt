package hr.itrojnar.ezbuild.view.adapters

import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import hr.itrojnar.ezbuild.databinding.ItemEquipmentLeaseLayoutBinding
import hr.itrojnar.ezbuild.model.viewModels.Equipment
import hr.itrojnar.ezbuild.view.fragments.EditLeasedEquipmentFragment
import hr.itrojnar.ezbuild.view.fragments.NewLeaseEquipmentFragment

class LeaseEquipmentAdapter(
    private val fragment: Fragment,
    private val equipment: ArrayList<Equipment>
) : RecyclerView.Adapter<LeaseEquipmentAdapter.ViewHolder>() {

    class ViewHolder(view: ItemEquipmentLeaseLayoutBinding) : RecyclerView.ViewHolder(view.root) {
        val ivEquipmentRvImage = view.ivEquipmentRvImage
        val tvItemEquipmentName = view.tvItemEquipmentName
        val tvEquipmentLeaseQuantity = view.tvEquipmentLeaseQuantity
        val tvItemEquipmentDescription = view.tvItemEquipmentDescription
        val ivDeleteEquipmentLease = view.ivDeleteEquipmentLease
        val ibEquipmentLeaseRemove = view.ibEquipmentLeaseRemove
        val ibEquipmentLeaseAdd = view.ibEquipmentLeaseAdd
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemEquipmentLeaseLayoutBinding = ItemEquipmentLeaseLayoutBinding.inflate(
            LayoutInflater.from(fragment.context), parent, false)
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
        holder.tvEquipmentLeaseQuantity.text = equipmentItem.quantity.toString()
        holder.tvItemEquipmentDescription.text = equipmentItem.equipmentDescription

        holder.ibEquipmentLeaseRemove.setOnClickListener {
            if (fragment is NewLeaseEquipmentFragment) {
                fragment.removeOneQuantity(equipmentItem)
            }
            else if (fragment is EditLeasedEquipmentFragment) {
                fragment.removeOneQuantity(equipmentItem)
            }
        }
        holder.ibEquipmentLeaseAdd.setOnClickListener {
            if (fragment is NewLeaseEquipmentFragment) {
                fragment.addOneQuantity(equipmentItem)
            }
            else if (fragment is EditLeasedEquipmentFragment) {
                fragment.addOneQuantity(equipmentItem)
            }
        }
        holder.ivDeleteEquipmentLease.setOnClickListener {
            if (fragment is NewLeaseEquipmentFragment) {
                fragment.removeLeaseItem(equipmentItem)
            }
            else if (fragment is EditLeasedEquipmentFragment) {
                fragment.removeLeaseItem(equipmentItem)
            }
        }
    }

    override fun getItemCount(): Int {
        return equipment.size
    }
}