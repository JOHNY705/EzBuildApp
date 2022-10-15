package hr.itrojnar.ezbuild.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.ItemWorkHoursMonthLayoutBinding
import hr.itrojnar.ezbuild.enums.MonthEnum
import hr.itrojnar.ezbuild.model.viewModels.EmployeeHoursMonth
import hr.itrojnar.ezbuild.utils.Constants
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

class EmployeeHoursMonthAdapter(
    private val fragment: Fragment,
    private val employeeHoursMonth: List<EmployeeHoursMonth>
    ) : RecyclerView.Adapter<EmployeeHoursMonthAdapter.ViewHolder>() {

    class ViewHolder(view: ItemWorkHoursMonthLayoutBinding) : RecyclerView.ViewHolder(view.root) {
        val tvItemWorkHoursMonth = view.tvItemWorkHoursMonth
        val tvItemWorkHoursYear = view.tvItemWorkHoursYear
        val tvItemWorkHoursCount = view.tvItemWorkHoursCount
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemWorkHoursMonthLayoutBinding = ItemWorkHoursMonthLayoutBinding.inflate(LayoutInflater.from(fragment.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val employeeHourMonth = employeeHoursMonth[position]

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val date = LocalDate.parse(employeeHourMonth.dateWorkDone, formatter)
        val month = date.month.value
        val year = date.year

        when (month) {
            MonthEnum.JANUARY.monthID -> holder.tvItemWorkHoursMonth.text = fragment.requireContext().getString(R.string.january)
            MonthEnum.FEBRUARY.monthID -> holder.tvItemWorkHoursMonth.text = fragment.requireContext().getString(R.string.february)
            MonthEnum.MARCH.monthID -> holder.tvItemWorkHoursMonth.text = fragment.requireContext().getString(R.string.march)
            MonthEnum.APRIL.monthID -> holder.tvItemWorkHoursMonth.text = fragment.requireContext().getString(R.string.april)
            MonthEnum.MAY.monthID -> holder.tvItemWorkHoursMonth.text = fragment.requireContext().getString(R.string.may)
            MonthEnum.JUNE.monthID -> holder.tvItemWorkHoursMonth.text = fragment.requireContext().getString(R.string.june)
            MonthEnum.JULY.monthID -> holder.tvItemWorkHoursMonth.text = fragment.requireContext().getString(R.string.july)
            MonthEnum.AUGUST.monthID -> holder.tvItemWorkHoursMonth.text = fragment.requireContext().getString(R.string.august)
            MonthEnum.SEPTEMBER.monthID -> holder.tvItemWorkHoursMonth.text = fragment.requireContext().getString(R.string.september)
            MonthEnum.OCTOBER.monthID -> holder.tvItemWorkHoursMonth.text = fragment.requireContext().getString(R.string.october)
            MonthEnum.NOVEMBER.monthID -> holder.tvItemWorkHoursMonth.text = fragment.requireContext().getString(R.string.november)
            MonthEnum.DECEMBER.monthID -> holder.tvItemWorkHoursMonth.text = fragment.requireContext().getString(R.string.december)
        }

        val currentLocale = Locale.getDefault().displayLanguage

        if (currentLocale == Constants.LOCALE_CROATIAN) {
            holder.tvItemWorkHoursYear.text = "$year."
        } else {
            holder.tvItemWorkHoursYear.text = year.toString()
        }

        holder.tvItemWorkHoursCount.text = employeeHourMonth.hoursWorked.toString()
    }

    override fun getItemCount(): Int {
        return employeeHoursMonth.size
    }
}