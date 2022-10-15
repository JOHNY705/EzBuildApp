package hr.itrojnar.ezbuild.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import hr.itrojnar.ezbuild.databinding.ItemEmployeeHoursEmployeeDayLayoutBinding
import hr.itrojnar.ezbuild.model.messaging.employeeHours.EmployeeHoursItem
import hr.itrojnar.ezbuild.model.viewModels.Employee
import hr.itrojnar.ezbuild.utils.EzBuildButton
import hr.itrojnar.ezbuild.view.fragments.EmployeeHoursForConstructionSiteFragment
import hr.itrojnar.ezbuild.view.fragments.EmployeeHoursForEmployeeTypesFragment

class EmployeeHoursDayAdapter(
    private val fragment: Fragment,
    private val employees: List<Employee>,
    private val employeeHours: List<EmployeeHoursItem>
) : RecyclerView.Adapter<EmployeeHoursDayAdapter.ViewHolder>() {

    class ViewHolder(view: ItemEmployeeHoursEmployeeDayLayoutBinding) : RecyclerView.ViewHolder(view.root) {
        val tvItemEmployeeHoursEmployeeName = view.tvItemEmployeeHoursEmployeeName
        val tvItemWorkHoursCount = view.tvItemWorkHoursCount
        val ibAddHours = view.ibAddHours
        val ibRemoveHours = view.ibRemoveHours
        val btnAdd8Hours = view.btnAdd8Hours
        val btnRemove8Hours = view.btnRemove8Hours
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemEmployeeHoursEmployeeDayLayoutBinding = ItemEmployeeHoursEmployeeDayLayoutBinding.inflate(
            LayoutInflater.from(fragment.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val employee = employees[position]

        holder.tvItemEmployeeHoursEmployeeName.text = employee.fullName

        var numberOfHoursWorked = employeeHours.find { it.employeeId == employee.idEmployee }!!.hoursWorked

        numberOfHoursWorked?.let {

            if (numberOfHoursWorked == 8) {
                holder.ibAddHours.visibility = View.INVISIBLE
                holder.btnAdd8Hours.visibility = View.GONE
                setLayoutParams(holder.btnRemove8Hours, 0)
            }
            else if (numberOfHoursWorked == 0) {
                holder.ibRemoveHours.visibility = View.INVISIBLE
                holder.btnRemove8Hours.visibility = View.GONE
            }

            holder.tvItemWorkHoursCount.text = numberOfHoursWorked.toString()
        }

        holder.ibAddHours.setOnClickListener {
            numberOfHoursWorked?.let {
                if (numberOfHoursWorked < 8) {
                    numberOfHoursWorked++
                    holder.ibRemoveHours.visibility = View.VISIBLE
                    setLayoutParams(holder.btnRemove8Hours, 8)
                }

                if (numberOfHoursWorked == 8) {
                    holder.ibAddHours.visibility = View.INVISIBLE
                    holder.btnAdd8Hours.visibility = View.GONE
                }

                holder.btnRemove8Hours.visibility = View.VISIBLE

                holder.tvItemWorkHoursCount.text = numberOfHoursWorked.toString()

                if (fragment is EmployeeHoursForConstructionSiteFragment) {
                    fragment.updateEmployeeHoursForEmployee(employee.idEmployee!!, numberOfHoursWorked)
                }
                else if (fragment is EmployeeHoursForEmployeeTypesFragment) {
                    fragment.updateEmployeeHoursForEmployee(employee.idEmployee!!, numberOfHoursWorked)
                }
            }
        }

        holder.ibRemoveHours.setOnClickListener {
            numberOfHoursWorked?.let {
                if (numberOfHoursWorked > 0) {
                    numberOfHoursWorked--
                    holder.ibAddHours.visibility = View.VISIBLE
                    setLayoutParams(holder.btnRemove8Hours, 8)
                }

                if (numberOfHoursWorked == 0) {
                    holder.ibRemoveHours.visibility = View.INVISIBLE
                    holder.btnRemove8Hours.visibility = View.GONE
                }

                holder.btnAdd8Hours.visibility = View.VISIBLE

                holder.tvItemWorkHoursCount.text = numberOfHoursWorked.toString()

                if (fragment is EmployeeHoursForConstructionSiteFragment) {
                    fragment.updateEmployeeHoursForEmployee(employee.idEmployee!!, numberOfHoursWorked)
                }
                else if (fragment is EmployeeHoursForEmployeeTypesFragment) {
                    fragment.updateEmployeeHoursForEmployee(employee.idEmployee!!, numberOfHoursWorked)
                }
            }
        }

        holder.btnAdd8Hours.setOnClickListener {
            numberOfHoursWorked?.let {
               while (numberOfHoursWorked < 8) {
                   numberOfHoursWorked++
               }

                holder.tvItemWorkHoursCount.text = numberOfHoursWorked.toString()

                holder.btnAdd8Hours.visibility = View.GONE
                holder.btnRemove8Hours.visibility = View.VISIBLE

                setLayoutParams(holder.btnRemove8Hours, 0)

                holder.ibRemoveHours.visibility = View.VISIBLE
                holder.ibAddHours.visibility = View.INVISIBLE

                if (fragment is EmployeeHoursForConstructionSiteFragment) {
                    fragment.updateEmployeeHoursForEmployee(employee.idEmployee!!, numberOfHoursWorked)
                }
                else if (fragment is EmployeeHoursForEmployeeTypesFragment) {
                    fragment.updateEmployeeHoursForEmployee(employee.idEmployee!!, numberOfHoursWorked)
                }
            }
        }

        holder.btnRemove8Hours.setOnClickListener {
            numberOfHoursWorked?.let {
                while (numberOfHoursWorked > 0) {
                    numberOfHoursWorked--
                }

                holder.tvItemWorkHoursCount.text = numberOfHoursWorked.toString()

                holder.btnRemove8Hours.visibility = View.GONE
                holder.btnAdd8Hours.visibility = View.VISIBLE
                holder.ibRemoveHours.visibility = View.INVISIBLE
                holder.ibAddHours.visibility = View.VISIBLE

                if (fragment is EmployeeHoursForConstructionSiteFragment) {
                    fragment.updateEmployeeHoursForEmployee(employee.idEmployee!!, numberOfHoursWorked)
                }
                else if (fragment is EmployeeHoursForEmployeeTypesFragment) {
                    fragment.updateEmployeeHoursForEmployee(employee.idEmployee!!, numberOfHoursWorked)
                }
            }
        }
    }

    private fun setLayoutParams(button: EzBuildButton, value: Int) {
        val param = button.layoutParams as ViewGroup.MarginLayoutParams
        param.topMargin = value
        button.layoutParams = param
    }

    override fun getItemCount(): Int {
        return employees.size
    }
}
