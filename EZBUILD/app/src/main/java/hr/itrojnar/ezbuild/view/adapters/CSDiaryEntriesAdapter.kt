package hr.itrojnar.ezbuild.view.adapters

import android.annotation.SuppressLint
import android.text.format.DateFormat
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.ItemConstructionSiteDiaryEntryBinding
import hr.itrojnar.ezbuild.model.viewModels.ConstructionSiteDiaryEntry
import hr.itrojnar.ezbuild.view.fragments.ConstructionSiteDetailsFragment
import java.text.SimpleDateFormat
import java.util.*

class CSDiaryEntriesAdapter(private val fragment: Fragment, private val diaryEntries: ArrayList<ConstructionSiteDiaryEntry>) : RecyclerView.Adapter<CSDiaryEntriesAdapter.ViewHolder>() {

    class ViewHolder(view: ItemConstructionSiteDiaryEntryBinding) : RecyclerView.ViewHolder(view.root) {
        val tvItemCsDiaryEntryAuthorizedEmployee = view.tvItemCsDiaryEntryAuthorizedEmployee
        val tvItemCsDiaryEntry = view.tvItemCsDiaryEntry
        val tvItemCsDiaryEntryDateOfEntry = view.tvItemCsDiaryEntryDateOfEntry
        val ivConstructionSiteDiaryEntryMenu = view.ivConstructionSiteDiaryEntryMenu
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemConstructionSiteDiaryEntryBinding = ItemConstructionSiteDiaryEntryBinding.inflate(
            LayoutInflater.from(fragment.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("DiscouragedPrivateApi")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val diaryEntry = diaryEntries[position]

        holder.tvItemCsDiaryEntryAuthorizedEmployee.text = diaryEntry.employeeFullName
        holder.tvItemCsDiaryEntry.text = diaryEntry.diaryEntry

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val formattedDate = DateFormat.format("dd.MM.yyyy.", sdf.parse(diaryEntry.diaryEntryDate!!)).toString()
        holder.tvItemCsDiaryEntryDateOfEntry.text = formattedDate

        holder.ivConstructionSiteDiaryEntryMenu.setOnClickListener {
            val wrapper = ContextThemeWrapper(fragment.context, R.style.PopupMenu)
            val popupMenu = PopupMenu(wrapper, holder.ivConstructionSiteDiaryEntryMenu, Gravity.RIGHT)
            popupMenu.menuInflater.inflate(R.menu.menu_diary_entry_adapter, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener {
                if (it.itemId == R.id.action_diary_entry_edit) {
                    if (fragment is ConstructionSiteDetailsFragment) {
                        fragment.editDiaryEntry(diaryEntry)
                    }
                }
                else if (it.itemId == R.id.action_diary_entry_delete) {
                    if (fragment is ConstructionSiteDetailsFragment) {
                        fragment.deleteDiaryEntry(diaryEntry)
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
        return diaryEntries.size
    }

    fun removeDiaryEntry(diaryEntry: ConstructionSiteDiaryEntry) {
        diaryEntries.remove(diaryEntry)
        notifyDataSetChanged()
    }
}