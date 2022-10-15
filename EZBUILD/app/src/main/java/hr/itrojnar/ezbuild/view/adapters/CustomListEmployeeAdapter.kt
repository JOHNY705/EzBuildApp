package hr.itrojnar.ezbuild.view.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import hr.itrojnar.ezbuild.databinding.ItemCustomListBinding
import hr.itrojnar.ezbuild.model.viewModels.Employee
import hr.itrojnar.ezbuild.utils.Constants
import hr.itrojnar.ezbuild.view.fragments.*

class CustomListEmployeeAdapter(
    private val activity: Activity,
    private val fragment: Fragment?,
    private val employees: List<Employee>,
    private val selection: String) : RecyclerView.Adapter<CustomListEmployeeAdapter.ViewHolder>() {

    class ViewHolder(view: ItemCustomListBinding) : RecyclerView.ViewHolder(view.root) {
        val tvItemCustomListText = view.tvItemCustomListText
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemCustomListBinding = ItemCustomListBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val employee = employees[position]
        holder.tvItemCustomListText.text = employee.fullName

        holder.itemView.setOnClickListener {
            when {
                (fragment is AddConstructionSiteFragment && selection == Constants.CS_MANAGER) -> {
                    fragment.selectedConstructionSiteManager(employee, selection)
                }
                (fragment is EditConstructionSiteFragment && selection == Constants.CS_MANAGER) -> {
                    fragment.selectedConstructionSiteManager(employee, selection)
                }
                (fragment is AddConstructionSiteFragment && selection == Constants.UNASSIGNED_EMPLOYEE) -> {
                    fragment.addUnassignedEmployeeToRecyclerView(employee)
                }
                (fragment is EditConstructionSiteFragment && selection == Constants.UNASSIGNED_EMPLOYEE) -> {
                    fragment.addUnassignedEmployeeToRecyclerView(employee)
                }
                (fragment is AddWarehouseFragment && selection == Constants.WAREHOUSE_MANAGER) -> {
                    fragment.selectedWarehouseManager(employee)
                }
                (fragment is EditWarehouseFragment && selection == Constants.WAREHOUSE_MANAGER) -> {
                    fragment.selectedWarehouseManager(employee)
                }
                (fragment is NewLeaseEquipmentFragment && selection == Constants.PHYSICAL_WORKER) -> {
                    fragment.selectedResponsiblePerson(employee)
                }
                (fragment is EditLeasedEquipmentFragment && selection == Constants.PHYSICAL_WORKER) -> {
                    fragment.selectedResponsiblePerson(employee)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return employees.size
    }
}


