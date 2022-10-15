package hr.itrojnar.ezbuild.view.adapters

import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.ItemEmployeeLayoutBinding
import hr.itrojnar.ezbuild.model.viewModels.Employee
import hr.itrojnar.ezbuild.enums.EmployeeType
import hr.itrojnar.ezbuild.view.fragments.EmployeesFragment

class EmployeeAdapter(
    private val fragment: Fragment,
    private val employees: List<Employee>
) : RecyclerView.Adapter<EmployeeAdapter.ViewHolder>() {

    class ViewHolder(view: ItemEmployeeLayoutBinding) : RecyclerView.ViewHolder(view.root) {
        val tvItemEmployeeFullName = view.tvItemEmployeeFullName
        val tvItemEmployeeType = view.tvItemEmployeeType
        val tvItemEmployeePhoneNumber = view.tvItemEmployeePhoneNumber
        val ivEmployeeMenu = view.ivEmployeeMenu
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemEmployeeLayoutBinding = ItemEmployeeLayoutBinding.inflate(LayoutInflater.from(fragment.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val employee = employees[position]

        holder.tvItemEmployeeFullName.text = employee.fullName
        holder.tvItemEmployeeType.text = employee.fullName
        when (employee.employeeTypeID) {
            EmployeeType.ENGINEER.typeID -> holder.tvItemEmployeeType.text = fragment.context?.getString(R.string.type_engineer)
            EmployeeType.PHYSICAL_WORKER.typeID -> holder.tvItemEmployeeType.text = fragment.context?.getString(R.string.type_physical_worker)
            EmployeeType.WAREHOUSE_MANAGER.typeID -> holder.tvItemEmployeeType.text = fragment.context?.getString(R.string.type_warehouse_manager)
        }
        holder.tvItemEmployeePhoneNumber.text = employee.phone

        holder.itemView.setOnClickListener {
            if (fragment is EmployeesFragment) {
                fragment.showEmployeeDetailsFragment(employee)
            }
        }

        holder.ivEmployeeMenu.setOnClickListener {

            val wrapper = ContextThemeWrapper(fragment.context, R.style.PopupMenu)
            val popupMenu = PopupMenu(wrapper, holder.ivEmployeeMenu, Gravity.RIGHT)
            popupMenu.menuInflater.inflate(R.menu.menu_employee_adapter, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener {
                if (it.itemId == R.id.action_employee_edit) {
                    if (fragment is EmployeesFragment) {
                        fragment.navigateToEmployeeEditFragment(employee)
                    }
                }
                else if (it.itemId == R.id.action_employee_delete) {
                    if (fragment is EmployeesFragment) {
                        fragment.showDeleteEmployeeDialog(employee)
                    }
                }
                else if (it.itemId == R.id.action_employee_phone) {
                    if (fragment is EmployeesFragment) {
                        fragment.phoneEmployee(employee)
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
        return employees.size
    }
}
