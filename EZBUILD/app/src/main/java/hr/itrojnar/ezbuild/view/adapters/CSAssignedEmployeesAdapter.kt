package hr.itrojnar.ezbuild.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import hr.itrojnar.ezbuild.databinding.ItemUnassignedEmployeeLayoutBinding
import hr.itrojnar.ezbuild.model.viewModels.Employee
import hr.itrojnar.ezbuild.view.fragments.AddConstructionSiteFragment
import hr.itrojnar.ezbuild.view.fragments.EditConstructionSiteFragment

class CSAssignedEmployeesAdapter(private val fragment: Fragment, private val employees: ArrayList<Employee>) : RecyclerView.Adapter<CSAssignedEmployeesAdapter.ViewHolder>() {

    class ViewHolder(view: ItemUnassignedEmployeeLayoutBinding) : RecyclerView.ViewHolder(view.root) {
        val tvItemUnassignedEmployeeFullName = view.tvItemUnassignedEmployeeFullName
        val ivItemUnassignedEmployeeRemove = view.ivItemUnassignedEmployeeRemove
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemUnassignedEmployeeLayoutBinding = ItemUnassignedEmployeeLayoutBinding.inflate(LayoutInflater.from(fragment.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val employee = employees[position]
        holder.tvItemUnassignedEmployeeFullName.text = employee.fullName

        holder.ivItemUnassignedEmployeeRemove.setOnClickListener {
            if (fragment is AddConstructionSiteFragment) {
                fragment.removeAssignedEmployeeFromRecyclerView(employee)
            }
            if (fragment is EditConstructionSiteFragment) {
                fragment.removeAssignedEmployeeFromRecyclerView(employee)
            }
        }
    }

    override fun getItemCount(): Int {
        return employees.size
    }

    fun addUnassignedEmployee(employee: Employee) {
        employees.add(employee)
        notifyDataSetChanged()
    }

    fun removeAssignedEmployee(employee: Employee) {
        employees.remove(employee)
        notifyDataSetChanged()
    }

    fun getAssignedEmployees() : ArrayList<Employee> {
        return employees
    }
}