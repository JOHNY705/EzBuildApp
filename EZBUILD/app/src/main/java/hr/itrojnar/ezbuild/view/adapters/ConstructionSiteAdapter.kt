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
import hr.itrojnar.ezbuild.databinding.ItemConstructionSiteLayoutBinding
import hr.itrojnar.ezbuild.model.viewModels.ConstructionSite
import hr.itrojnar.ezbuild.enums.EmployeeType
import hr.itrojnar.ezbuild.view.fragments.ConstructionSitesFragment
import hr.itrojnar.ezbuild.view.fragments.EmployeeHoursMenuFragment

class ConstructionSiteAdapter(
    private val fragment: Fragment,
    private val constructionSites: ArrayList<ConstructionSite>,
    private val employeeTypeID: Int) : RecyclerView.Adapter<ConstructionSiteAdapter.ViewHolder>() {

    class ViewHolder(view: ItemConstructionSiteLayoutBinding) : RecyclerView.ViewHolder(view.root) {
        val ivConstructionSiteRvImage = view.ivConstructionSiteRvImage
        val tvItemCsManager = view.tvItemCsManager
        val tvItemCsNumberOfWorkers = view.tvItemCsNumberOfWorkers
        val tvItemCsFullAddress = view.tvItemCsFullAddress
        val ivConstructionSiteMenu = view.ivConstructionSiteMenu
        val ivConstructionSiteLocation = view.ivConstructionSiteLocation
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemConstructionSiteLayoutBinding = ItemConstructionSiteLayoutBinding
            .inflate(LayoutInflater.from(fragment.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val constructionSite = constructionSites[position]

        Glide.with(fragment).asBitmap().load(Base64.decode(constructionSite.base64Image, Base64.DEFAULT))
            .transform(CenterCrop(), RoundedCorners(12)).into(holder.ivConstructionSiteRvImage)

        constructionSite.constructionSiteManager?.fullName.let {
            holder.tvItemCsManager.text = constructionSite.constructionSiteManager!!.fullName
        }
        holder.tvItemCsNumberOfWorkers.text = constructionSite.employees.count().toString()
        holder.tvItemCsFullAddress.text = constructionSite.fullAddress

        if (employeeTypeID == EmployeeType.DIRECTOR.typeID && fragment is ConstructionSitesFragment) {
            holder.ivConstructionSiteMenu.visibility = View.VISIBLE
        }

        holder.ivConstructionSiteLocation.setOnClickListener {
            if (fragment is ConstructionSitesFragment) {
                fragment.showConstructionSiteOnMap(constructionSite)
            }
            else if (fragment is EmployeeHoursMenuFragment) {
                fragment.showConstructionSiteOnMap(constructionSite)
            }
        }

        holder.ivConstructionSiteMenu.setOnClickListener {

            val wrapper = ContextThemeWrapper(fragment.context, R.style.PopupMenu)
            val popupMenu = PopupMenu(wrapper, holder.ivConstructionSiteMenu, Gravity.RIGHT)
            popupMenu.menuInflater.inflate(R.menu.menu_construction_site_adapter, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener {
                if (it.itemId == R.id.action_construction_site_edit) {
                    if (fragment is ConstructionSitesFragment) {
                        fragment.editConstructionSite(constructionSite)
                    }
                }
                else if (it.itemId == R.id.action_construction_site_delete) {
                    if (fragment is ConstructionSitesFragment) {
                        fragment.deleteConstructionSite(constructionSite)
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

        holder.itemView.setOnClickListener {
            if (fragment is ConstructionSitesFragment) {
                fragment.constructionSiteDetails(constructionSite)
            }
            else if (fragment is EmployeeHoursMenuFragment) {
                fragment.employeeHoursForConstructionSite(constructionSite)
            }
        }
    }

    override fun getItemCount(): Int {
        return constructionSites.size
    }
}


