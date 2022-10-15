package hr.itrojnar.ezbuild.view.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.FragmentEmployeeHoursForEmployeeTypesBinding
import hr.itrojnar.ezbuild.enums.EmployeeType
import hr.itrojnar.ezbuild.model.messaging.BaseResponse
import hr.itrojnar.ezbuild.model.messaging.employee.EmployeesForFirmResponse
import hr.itrojnar.ezbuild.model.messaging.employeeHours.CreateEmployeeHours
import hr.itrojnar.ezbuild.model.messaging.employeeHours.EmployeeHoursItem
import hr.itrojnar.ezbuild.model.messaging.employeeHours.EmployeeHoursResponse
import hr.itrojnar.ezbuild.model.network.EzBuildAPIInterface
import hr.itrojnar.ezbuild.model.viewModels.Employee
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

class EmployeeHoursForEmployeeTypesFragment : Fragment() {

    private lateinit var mBinding: FragmentEmployeeHoursForEmployeeTypesBinding

    private var mEmployees: List<Employee> = listOf()
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
        mBinding = FragmentEmployeeHoursForEmployeeTypesBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).useUpButton()

        val args: EmployeeHoursForEmployeeTypesFragmentArgs by navArgs()

        mPreferences = requireActivity().getSharedPreferences(Constants.EZBUILD_PREFERENCES, Context.MODE_PRIVATE)

        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.")
        val formattedDate = LocalDate.now().format(formatter)
        val selectedDate = LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.titlecase() } + ", " + formattedDate

        mBinding.tvEmployeeHoursSelectedDate.text = selectedDate

        when (args.employeeTypeID) {
            EmployeeType.ENGINEER.typeID -> {
                (activity as MainActivity).supportActionBar?.title = getString(R.string.title_work_hours_for_engineers)
                mBinding.tvEmployeeHoursNoEmployees.text = getString(R.string.lbl_no_engineers_available)
                loadEngineers()
            }
            EmployeeType.PHYSICAL_WORKER.typeID -> {
                (activity as MainActivity).supportActionBar?.title = getString(R.string.title_work_hours_for_physical_workers)
                mBinding.tvEmployeeHoursNoEmployees.text = getString(R.string.lbl_no_physical_workers_available)
                loadPhysicalWorkers()
            }
            EmployeeType.WAREHOUSE_MANAGER.typeID -> {
                (activity as MainActivity).supportActionBar?.title = getString(R.string.title_work_hours_for_warehouse_manager)
                mBinding.tvEmployeeHoursNoEmployees.text = getString(R.string.lbl_no_warehouse_manager_available)
                loadWarehouseManager()
            }
        }

        selectedCalendarViewDate = LocalDate.now()

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

        mBinding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedCalendarViewDate = LocalDate.of(year, month + 1, dayOfMonth)
            println(selectedCalendarViewDate)

            val newFormattedDate = selectedCalendarViewDate.format(formatter)
            val newSelectedDate = selectedCalendarViewDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.titlecase() } + ", " + newFormattedDate
            mBinding.tvEmployeeHoursSelectedDate.text = newSelectedDate

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

            for (i in mEmployees) {
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

            setupRecyclerViewAdapter()
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

                        (activity as MainActivity).showSnackBar(getString(R.string.employee_hours_save_successful), false)
                        //findNavController().navigate(EmployeeHoursForConstructionSiteFragmentDirections.actionEmployeeHoursForConstructionSiteFragmentToNavigationEmployeeHoursMenu())
                        refreshEmployeeHours(args.employeeTypeID)
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

    private fun refreshEmployeeHours(employeeTypeID: Int) {

        when (employeeTypeID) {

            EmployeeType.ENGINEER.typeID -> {
                loadEmployeeHoursForEngineers()
            }
            EmployeeType.PHYSICAL_WORKER.typeID -> {
                loadEmployeeHoursForPhysicalWorkers()
            }
            EmployeeType.WAREHOUSE_MANAGER.typeID -> {
                loadEmployeeHoursForWarehouseManager()
            }
        }
    }

    private fun loadEngineers() {

        (activity as MainActivity).showProgressDialog(getString(R.string.please_wait))

        ezBuildAPIInterface.getEngineersForFirm(mPreferences.getInt(Constants.USER_FIRM_ID, 0)).enqueue(object:
            Callback<EmployeesForFirmResponse> {
            override fun onResponse(
                call: Call<EmployeesForFirmResponse>,
                response: Response<EmployeesForFirmResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    mEmployees = response.body()!!.employees
                    loadEmployeeHoursForEngineers()
                }
            }

            override fun onFailure(
                call: Call<EmployeesForFirmResponse>,
                t: Throwable
            ) {
                (activity as MainActivity).hideProgressDialog()
                //(activity as MainActivity).showErrorSnackBar(getString(R.string.error_api_employee_hours_save_fail), true)
                Log.e(requireActivity().javaClass.simpleName, "Error while getting engineers from API.", t)
            }
        })
    }

    private fun loadPhysicalWorkers() {
        (activity as MainActivity).showProgressDialog(getString(R.string.please_wait))

        ezBuildAPIInterface.getPhysicalWorkersForFirm(mPreferences.getInt(Constants.USER_FIRM_ID, 0)).enqueue(object:
            Callback<EmployeesForFirmResponse> {
            override fun onResponse(
                call: Call<EmployeesForFirmResponse>,
                response: Response<EmployeesForFirmResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    mEmployees = response.body()!!.employees
                    loadEmployeeHoursForPhysicalWorkers()
                }
            }

            override fun onFailure(
                call: Call<EmployeesForFirmResponse>,
                t: Throwable
            ) {
                (activity as MainActivity).hideProgressDialog()
                //(activity as MainActivity).showErrorSnackBar(getString(R.string.error_api_employee_hours_save_fail), true)
                Log.e(requireActivity().javaClass.simpleName, "Error while getting physical workers from API.", t)
            }
        })
    }

    private fun loadWarehouseManager() {
        (activity as MainActivity).showProgressDialog(getString(R.string.please_wait))

        ezBuildAPIInterface.getWarehouseManagerForFirm(mPreferences.getInt(Constants.USER_FIRM_ID, 0)).enqueue(object:
            Callback<EmployeesForFirmResponse> {
            override fun onResponse(
                call: Call<EmployeesForFirmResponse>,
                response: Response<EmployeesForFirmResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    mEmployees = response.body()!!.employees
                    loadEmployeeHoursForWarehouseManager()
                }
            }

            override fun onFailure(
                call: Call<EmployeesForFirmResponse>,
                t: Throwable
            ) {
                (activity as MainActivity).hideProgressDialog()
                //(activity as MainActivity).showErrorSnackBar(getString(R.string.error_api_employee_hours_save_fail), true)
                Log.e(requireActivity().javaClass.simpleName, "Error while getting warehouse manager from API.", t)
            }
        })
    }

    private fun setupData() {

        mEmployees.sortedBy { it.fullName }

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val localDate = LocalDate.now()

        val employeeHours = mEmployeeHours.filter {
            LocalDate.parse(it.dateWorkDone, formatter) == localDate
        }

        employeeHours.forEach {
            mEmployeeHoursItems.add(EmployeeHoursItem(it.hoursWorked, it.dateWorkDone, it.employeeID))
        }

        for (i in mEmployees) {
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

        setupRecyclerViewAdapter()
    }

    private fun loadEmployeeHoursForEngineers() {

        ezBuildAPIInterface.getEmployeeHoursForEngineers(mPreferences.getInt(Constants.USER_FIRM_ID, 0)).enqueue(object:
            Callback<EmployeeHoursResponse> {
            override fun onResponse(
                call: Call<EmployeeHoursResponse>,
                response: Response<EmployeeHoursResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    mEmployeeHours = response.body()!!.employeeHours
                    setupData()
                    (activity as MainActivity).hideProgressDialog()
                }
            }

            override fun onFailure(
                call: Call<EmployeeHoursResponse>,
                t: Throwable
            ) {
                (activity as MainActivity).hideProgressDialog()
                //(activity as MainActivity).showErrorSnackBar(getString(R.string.error_api_employee_hours_save_fail), true)
                Log.e(requireActivity().javaClass.simpleName, "Error while getting employee hours for engineers from API.", t)
            }
        })
    }

    private fun loadEmployeeHoursForPhysicalWorkers() {

        ezBuildAPIInterface.getEmployeeHoursForPhysicalWorkers(mPreferences.getInt(Constants.USER_FIRM_ID, 0)).enqueue(object:
            Callback<EmployeeHoursResponse> {
            override fun onResponse(
                call: Call<EmployeeHoursResponse>,
                response: Response<EmployeeHoursResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    mEmployeeHours = response.body()!!.employeeHours
                    setupData()
                    (activity as MainActivity).hideProgressDialog()
                }
            }

            override fun onFailure(
                call: Call<EmployeeHoursResponse>,
                t: Throwable
            ) {
                (activity as MainActivity).hideProgressDialog()
                //(activity as MainActivity).showErrorSnackBar(getString(R.string.error_api_employee_hours_save_fail), true)
                Log.e(requireActivity().javaClass.simpleName, "Error while getting employee hours for physical workers from API.", t)
            }
        })
    }

    private fun loadEmployeeHoursForWarehouseManager() {

        ezBuildAPIInterface.getEmployeeHoursForWarehouseManager(mPreferences.getInt(Constants.USER_FIRM_ID, 0)).enqueue(object:
            Callback<EmployeeHoursResponse> {
            override fun onResponse(
                call: Call<EmployeeHoursResponse>,
                response: Response<EmployeeHoursResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    mEmployeeHours = response.body()!!.employeeHours
                    setupData()
                    (activity as MainActivity).hideProgressDialog()
                }
            }

            override fun onFailure(
                call: Call<EmployeeHoursResponse>,
                t: Throwable
            ) {
                (activity as MainActivity).hideProgressDialog()
                //(activity as MainActivity).showErrorSnackBar(getString(R.string.error_api_employee_hours_save_fail), true)
                Log.e(requireActivity().javaClass.simpleName, "Error while getting employee hours for warehouse manager from API.", t)
            }
        })
    }


    private fun setupRecyclerViewAdapter() {

        mBinding.rvEmployeeHoursDay.layoutManager = LinearLayoutManager(requireContext())
        mEmployeeHoursDayAdapter = EmployeeHoursDayAdapter(this@EmployeeHoursForEmployeeTypesFragment, mEmployees, mEmployeeHoursItems)
        mBinding.rvEmployeeHoursDay.adapter = mEmployeeHoursDayAdapter

        toggleRecyclerViewVisibility()
    }

    private fun toggleRecyclerViewVisibility() {
        if (mEmployeeHours.isNotEmpty()) {
            mBinding.rvEmployeeHoursDay.visibility = View.VISIBLE
        }
        else {
            mBinding.tvEmployeeHoursNoEmployees.visibility = View.VISIBLE
        }
    }

    fun updateEmployeeHoursForEmployee(idEmployee: Int, numberOfHoursWorked: Int) {

        mEmployeeHoursItems.find { it.employeeId == idEmployee }?.hoursWorked = numberOfHoursWorked
        mBinding.btnEmployeeHoursSave.visibility = View.VISIBLE
    }
}