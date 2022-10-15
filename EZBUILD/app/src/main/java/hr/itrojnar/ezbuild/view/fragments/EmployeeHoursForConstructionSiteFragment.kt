package hr.itrojnar.ezbuild.view.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.FragmentEmployeeHoursForConstructionSiteBinding
import hr.itrojnar.ezbuild.enums.EmployeeType
import hr.itrojnar.ezbuild.model.messaging.BaseResponse
import hr.itrojnar.ezbuild.model.messaging.employeeHours.CreateEmployeeHours
import hr.itrojnar.ezbuild.model.messaging.employeeHours.EmployeeHoursItem
import hr.itrojnar.ezbuild.model.messaging.employeeHours.EmployeeHoursResponse
import hr.itrojnar.ezbuild.model.network.EzBuildAPIInterface
import hr.itrojnar.ezbuild.model.viewModels.ConstructionSite
import hr.itrojnar.ezbuild.model.viewModels.EmployeeHours
import hr.itrojnar.ezbuild.utils.Constants
import hr.itrojnar.ezbuild.view.activities.MainActivity
import hr.itrojnar.ezbuild.view.adapters.EmployeeHoursDayAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
import kotlin.collections.ArrayList

class EmployeeHoursForConstructionSiteFragment : Fragment() {

    private lateinit var mBinding: FragmentEmployeeHoursForConstructionSiteBinding
    private lateinit var mConstructionSite: ConstructionSite

    private var mEmployeeHours: List<EmployeeHours> = listOf()
    private var mEmployeeHoursItems: ArrayList<EmployeeHoursItem> = arrayListOf()
    private lateinit var mEmployeeHoursDayAdapter: EmployeeHoursDayAdapter

    private lateinit var selectedCalendarViewDate: LocalDate

    private val ezBuildAPIInterface = EzBuildAPIInterface.create()

    private lateinit var mPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentEmployeeHoursForConstructionSiteBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: EmployeeHoursForConstructionSiteFragmentArgs by navArgs()
        mConstructionSite = args.constructionSite

        mPreferences = requireActivity().getSharedPreferences(Constants.EZBUILD_PREFERENCES, Context.MODE_PRIVATE)

        (activity as MainActivity).useUpButton()

        if (mConstructionSite.employees.isEmpty()) {
            mBinding.tvEmployeeHoursCsNoEmployees.visibility = View.VISIBLE
        }

        if (mPreferences.getInt(Constants.USER_TYPE_ID, 0) == EmployeeType.DIRECTOR.typeID) {
            setupDirectorView()
            loadEmployeeHoursForConstructionSite()
        }
        else if (mPreferences.getInt(Constants.USER_TYPE_ID, 0) == EmployeeType.ENGINEER.typeID) {
            setupEngineerView()
            loadEmployeeHoursForConstructionSite()
        }
    }

    private fun setupDirectorView() {

        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.")
        val formattedDate = LocalDate.now().format(formatter)
        val selectedDate = LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.titlecase() } + ", " + formattedDate

        mBinding.tvEmployeeHoursCsSelectedDate.text = selectedDate
        mBinding.tvEmployeeHoursCsAddress.text = mConstructionSite.fullAddress
        mBinding.tvEmployeeHoursCsManager.text = mConstructionSite.constructionSiteManager!!.fullName

        selectedCalendarViewDate = LocalDate.now()

        mBinding.btnToggleCalendar.visibility = View.VISIBLE
        mBinding.calendarView.visibility = View.GONE

        mBinding.btnToggleCalendar.setOnClickListener {
            if (mBinding.calendarView.isVisible) {
                mBinding.calendarView.visibility = View.GONE
                mBinding.btnToggleCalendar.text = getString(R.string.btn_show_calendar)
            } else {
                mBinding.calendarView.visibility = View.VISIBLE
                mBinding.btnToggleCalendar.text = getString(R.string.btn_hide_calendar)
            }
        }

         mBinding.calendarView.setOnDateChangeListener{ _, year, month, dayOfMonth ->
             selectedCalendarViewDate = LocalDate.of(year, month + 1, dayOfMonth)
             println(selectedCalendarViewDate)

             val newFormattedDate = selectedCalendarViewDate.format(formatter)
             val newSelectedDate = selectedCalendarViewDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.titlecase() } + ", " + newFormattedDate
             mBinding.tvEmployeeHoursCsSelectedDate.text = newSelectedDate

             val apiFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

             val employeeHours = mEmployeeHours.filter {
                 LocalDate.parse(it.dateWorkDone, apiFormatter) == selectedCalendarViewDate
             }

             mEmployeeHoursItems.clear()

             mBinding.btnEmployeeHoursSave.visibility = View.GONE

             if (employeeHours.isNotEmpty()) {
                 employeeHours.forEach {
                     mEmployeeHoursItems.add(EmployeeHoursItem(it.hoursWorked, it.dateWorkDone, it.employeeID))
                 }
             }

             for (i in mConstructionSite.employees) {
                 var isContained = false
                 for (j in mEmployeeHoursItems) {
                     if (j.employeeId == i.idEmployee) {
                         isContained = true
                     }
                 }
                 if (!isContained) {
                     mEmployeeHoursItems.add(EmployeeHoursItem(0, selectedCalendarViewDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), i.idEmployee))
                 }
             }

             setupRecyclerViewAdapter(mEmployeeHoursItems)
         }

        mBinding.btnEmployeeHoursSave.setOnClickListener {

            val createEmployeeHours = CreateEmployeeHours(mEmployeeHoursItems)

            (activity as MainActivity).showProgressDialog(getString(R.string.please_wait))

            ezBuildAPIInterface.createEmployeeHours(createEmployeeHours).enqueue(object:
                Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if(response.body() != null && response.body()!!.isSuccessful) {

                        (activity as MainActivity).showSnackBar(getString(R.string.employee_hours_cs_save_successful), false)
                        //findNavController().navigate(EmployeeHoursForConstructionSiteFragmentDirections.actionEmployeeHoursForConstructionSiteFragmentToNavigationEmployeeHoursMenu())
                        refreshEmployeeHoursForConstructionSite()
                    }
                }

                override fun onFailure(
                    call: Call<BaseResponse>,
                    t: Throwable
                ) {
                    (activity as MainActivity).hideProgressDialog()
                    (activity as MainActivity).showSnackBar(getString(R.string.error_api_employee_hours_save_fail), true)
                    Log.e(requireActivity().javaClass.simpleName, "Error while saving employee hours to API.", t)
                }
            })
        }
    }

    private fun setupEngineerView() {

        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.")
        val formattedDate = LocalDate.now().format(formatter)
        val selectedDate = LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.titlecase() } + ", " + formattedDate

        mBinding.calendarView.visibility = View.GONE
        mBinding.tvEmployeeHoursCsSelectedDate.text = selectedDate
        mBinding.tvEmployeeHoursCsAddress.text = mConstructionSite.fullAddress
        mBinding.tvEmployeeHoursCsManager.text = mConstructionSite.constructionSiteManager!!.fullName

        mBinding.btnEmployeeHoursSave.setOnClickListener {

            val createEmployeeHours = CreateEmployeeHours(mEmployeeHoursItems)

            (activity as MainActivity).showProgressDialog(getString(R.string.please_wait))

            ezBuildAPIInterface.createEmployeeHours(createEmployeeHours).enqueue(object:
                Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if(response.body() != null && response.body()!!.isSuccessful) {

                        (activity as MainActivity).hideProgressDialog()
                        (activity as MainActivity).showSnackBar(getString(R.string.employee_hours_cs_save_successful), false)
                        findNavController().navigate(EmployeeHoursForConstructionSiteFragmentDirections.actionEmployeeHoursForConstructionSiteFragmentToNavigationEmployeeHoursMenu())
                    }
                }

                override fun onFailure(
                    call: Call<BaseResponse>,
                    t: Throwable
                ) {
                    (activity as MainActivity).hideProgressDialog()
                    (activity as MainActivity).showSnackBar(getString(R.string.error_api_employee_hours_save_fail), true)
                    Log.e(requireActivity().javaClass.simpleName, "Error while saving employee hours to API.", t)
                }
            })
        }
    }

    private fun loadEmployeeHoursForConstructionSite() {

        (activity as MainActivity).showProgressDialog(getString(R.string.please_wait))
        ezBuildAPIInterface.getEmployeeHoursForConstructionSite(mConstructionSite.idConstructionSite).enqueue(object:
            Callback<EmployeeHoursResponse> {
            override fun onResponse(
                call: Call<EmployeeHoursResponse>,
                response: Response<EmployeeHoursResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                    val localDate = LocalDate.now()

                    mEmployeeHours = response.body()!!.employeeHours

                    val employeeHours = mEmployeeHours.filter {
                        LocalDate.parse(it.dateWorkDone, formatter) == localDate
                    }

                    /*mEmployeeHours = response.body()!!.employeeHours.filter {
                        LocalDate.parse(it.dateWorkDone, formatter) == localDate
                    }*/

                    employeeHours.forEach {
                        mEmployeeHoursItems.add(EmployeeHoursItem(it.hoursWorked, it.dateWorkDone, it.employeeID))
                    }

                    for (i in mConstructionSite.employees) {
                        var isContained = false
                        for (j in mEmployeeHoursItems) {
                            if (j.employeeId == i.idEmployee) {
                                isContained = true
                            }
                        }
                        if (!isContained) {
                            mEmployeeHoursItems.add(EmployeeHoursItem(0, localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), i.idEmployee))
                        }
                    }

                    setupRecyclerViewAdapter(mEmployeeHoursItems)

                    (activity as MainActivity).hideProgressDialog()
                }
            }

            override fun onFailure(
                call: Call<EmployeeHoursResponse>,
                t: Throwable
            ) {
                (activity as MainActivity).hideProgressDialog()
                (activity as MainActivity).showSnackBar(getString(R.string.error_api_construction_sites_loading_fail), true)
                Log.e(requireActivity().javaClass.simpleName, "Error while getting employee hours from API.", t)
            }
        })
    }

    private fun refreshEmployeeHoursForConstructionSite() {

        ezBuildAPIInterface.getEmployeeHoursForConstructionSite(mConstructionSite.idConstructionSite).enqueue(object:
            Callback<EmployeeHoursResponse> {
            override fun onResponse(
                call: Call<EmployeeHoursResponse>,
                response: Response<EmployeeHoursResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                    val localDate = LocalDate.now()

                    mEmployeeHours = response.body()!!.employeeHours

                    val employeeHours = mEmployeeHours.filter {
                        LocalDate.parse(it.dateWorkDone, formatter) == selectedCalendarViewDate
                    }

                    mEmployeeHoursItems.clear()

                    employeeHours.forEach {
                        mEmployeeHoursItems.add(EmployeeHoursItem(it.hoursWorked, it.dateWorkDone, it.employeeID))
                    }

                    for (i in mConstructionSite.employees) {
                        var isContained = false
                        for (j in mEmployeeHoursItems) {
                            if (j.employeeId == i.idEmployee) {
                                isContained = true
                            }
                        }
                        if (!isContained) {
                            mEmployeeHoursItems.add(EmployeeHoursItem(0, localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), i.idEmployee))
                        }
                    }

                    setupRecyclerViewAdapter(mEmployeeHoursItems)

                    (activity as MainActivity).hideProgressDialog()
                }
            }

            override fun onFailure(
                call: Call<EmployeeHoursResponse>,
                t: Throwable
            ) {
                (activity as MainActivity).hideProgressDialog()
                (activity as MainActivity).showSnackBar(getString(R.string.error_api_construction_sites_loading_fail), true)
                Log.e(requireActivity().javaClass.simpleName, "Error while getting employee hours from API.", t)
            }
        })
    }

    private fun setupRecyclerViewAdapter(employeeHoursItems: ArrayList<EmployeeHoursItem>) {

        mConstructionSite.employees.sortBy { it.fullName }

        mBinding.rvEmployeeHoursDay.layoutManager = LinearLayoutManager(requireContext())
        mEmployeeHoursDayAdapter = EmployeeHoursDayAdapter(this@EmployeeHoursForConstructionSiteFragment, mConstructionSite.employees, employeeHoursItems)
        mBinding.rvEmployeeHoursDay.adapter = mEmployeeHoursDayAdapter

        toggleRecyclerViewVisibility()
    }

    private fun toggleRecyclerViewVisibility() {
        if (mConstructionSite.employees.isNotEmpty()) {
            mBinding.rvEmployeeHoursDay.visibility = View.VISIBLE
        }
    }

    fun updateEmployeeHoursForEmployee(idEmployee: Int, numberOfHoursWorked: Int) {

        mEmployeeHoursItems.find { it.employeeId == idEmployee }?.hoursWorked = numberOfHoursWorked
        mBinding.btnEmployeeHoursSave.visibility = View.VISIBLE
    }

}