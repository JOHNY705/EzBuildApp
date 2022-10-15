package hr.itrojnar.ezbuild.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import hr.itrojnar.ezbuild.databinding.ItemCsDetailsAssignedEmployeesListBinding
import hr.itrojnar.ezbuild.model.viewModels.Employee

class CSDetailsEmployeesAdapter(private val fragment: Fragment, private val employees: List<Employee>) : RecyclerView.Adapter<CSDetailsEmployeesAdapter.ViewHolder>() {

    class ViewHolder(view: ItemCsDetailsAssignedEmployeesListBinding) : RecyclerView.ViewHolder(view.root) {
        val tvItemCsDetailsAssignedEmployeesListText = view.tvItemCsDetailsAssignedEmployeesListText
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemCsDetailsAssignedEmployeesListBinding = ItemCsDetailsAssignedEmployeesListBinding.inflate(LayoutInflater.from(fragment.requireActivity()), parent, false)
        return  ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val employee = employees[position]
        holder.tvItemCsDetailsAssignedEmployeesListText.text = employee.fullName
    }

    override fun getItemCount(): Int {
        return employees.count()
    }
}