package hr.itrojnar.ezbuild.view.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import hr.itrojnar.ezbuild.databinding.ItemCustomListBinding
import hr.itrojnar.ezbuild.view.fragments.AddConstructionSiteFragment
import hr.itrojnar.ezbuild.view.fragments.AddEmployeeFragment

class CustomListItemAdapter(
    private val activity: Activity,
    private val fragment: Fragment?,
    private val listItems: List<String>,
    private val selection: String) : RecyclerView.Adapter<CustomListItemAdapter.ViewHolder>() {

        class ViewHolder(view: ItemCustomListBinding) : RecyclerView.ViewHolder(view.root) {
            val tvItemCustomListText = view.tvItemCustomListText
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemCustomListBinding = ItemCustomListBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listItems[position]
        holder.tvItemCustomListText.text = item

        holder.itemView.setOnClickListener {
            if (fragment is AddConstructionSiteFragment) {
                fragment.selectedListItem(item, selection)
            }
            else if (fragment is AddEmployeeFragment) {
                fragment.selectedEmployeeType(item, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return listItems.size
    }
}