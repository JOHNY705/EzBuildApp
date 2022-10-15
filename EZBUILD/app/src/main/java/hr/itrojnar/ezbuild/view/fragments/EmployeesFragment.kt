package hr.itrojnar.ezbuild.view.fragments

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.FragmentEmployeesBinding
import hr.itrojnar.ezbuild.model.messaging.constructionSite.ConstructionSitesResponse
import hr.itrojnar.ezbuild.model.messaging.employee.EmployeesForFirmResponse
import hr.itrojnar.ezbuild.model.viewModels.ConstructionSite
import hr.itrojnar.ezbuild.model.viewModels.Employee
import hr.itrojnar.ezbuild.model.network.EzBuildAPIInterface
import hr.itrojnar.ezbuild.utils.Constants
import hr.itrojnar.ezbuild.enums.EmployeeType
import hr.itrojnar.ezbuild.model.messaging.BaseResponse
import hr.itrojnar.ezbuild.model.messaging.warehouse.WarehouseForFirmResponse
import hr.itrojnar.ezbuild.model.viewModels.Warehouse
import hr.itrojnar.ezbuild.view.activities.MainActivity
import hr.itrojnar.ezbuild.view.adapters.EmployeeAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmployeesFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var mBinding: FragmentEmployeesBinding

    private val ezBuildAPIInterface = EzBuildAPIInterface.create()

    private lateinit var mPreferences: SharedPreferences

    private lateinit var mEngineers: ArrayList<Employee>
    private lateinit var mPhysicalWorkers: ArrayList<Employee>
    private lateinit var mWarehouseManager: ArrayList<Employee>
    private lateinit var mConstructionSites: ArrayList<ConstructionSite>
    private var mWarehouse = Warehouse()

    private lateinit var mEngineersAdapter: EmployeeAdapter
    private lateinit var mPhysicalWorkersAdapter: EmployeeAdapter
    private lateinit var mWarehouseManagersAdapter: EmployeeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentEmployeesBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPreferences = requireActivity().getSharedPreferences(Constants.EZBUILD_PREFERENCES, Context.MODE_PRIVATE)
        mBinding.employeesSwipeRefresh.setOnRefreshListener(this)
        loadEmployees()
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is MainActivity) {
            (activity as MainActivity).useHamburgerButton()
            (activity as MainActivity?)!!.showBottomNavigationView()
        }
    }

    private fun loadEmployees() {

        mEngineers = ArrayList()
        mPhysicalWorkers = ArrayList()
        mWarehouseManager = ArrayList()
        mConstructionSites = ArrayList()

        (activity as MainActivity).showProgressDialog(resources.getString(R.string.please_wait))

        ezBuildAPIInterface.getEmployeesForFirm(mPreferences.getInt(Constants.USER_FIRM_ID, 0)).enqueue(object:
            Callback<EmployeesForFirmResponse> {
            override fun onResponse(
                call: Call<EmployeesForFirmResponse>,
                response: Response<EmployeesForFirmResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    val employees = response.body()!!.employees

                    employees.forEach {
                        if (it.employeeTypeID == EmployeeType.ENGINEER.typeID) {
                            mEngineers.add(it)
                        } else if (it.employeeTypeID == EmployeeType.PHYSICAL_WORKER.typeID) {
                            mPhysicalWorkers.add(it)
                        } else if (it.employeeTypeID == EmployeeType.WAREHOUSE_MANAGER.typeID) {
                            mWarehouseManager.add(it)
                        }
                    }

                    loadConstructionSites()
                    setupRecyclerViewAdapters()

                    mBinding.llEmployeesFragment.visibility = View.VISIBLE

                    (activity as MainActivity).hideProgressDialog()

                    toggleRecyclerViewVisibility()
                }
            }

            override fun onFailure(
                call: Call<EmployeesForFirmResponse>,
                t: Throwable
            ) {
                (activity as MainActivity).hideProgressDialog()
                (activity as MainActivity).showSnackBar(getString(R.string.error_api_employees_loading_fail), true)
                Log.e(requireActivity().javaClass.simpleName, "Error while getting employees from API.", t)
            }
        })
    }

    private fun loadConstructionSites() {
        ezBuildAPIInterface.getConstructionSitesForFirmNoImage(mPreferences.getInt(Constants.USER_FIRM_ID, 0)).enqueue(object:
            Callback<ConstructionSitesResponse> {
            override fun onResponse(
                call: Call<ConstructionSitesResponse>,
                response: Response<ConstructionSitesResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    mConstructionSites = response.body()!!.constructionSites
                    loadWarehouseForFirm()
                }
            }

            override fun onFailure(
                call: Call<ConstructionSitesResponse>,
                t: Throwable
            ) {
                (activity as MainActivity).hideProgressDialog()
                (activity as MainActivity).showSnackBar(getString(R.string.error_api_construction_sites_loading_fail), true)
                Log.e(requireActivity().javaClass.simpleName, "Error while getting construction sites from API.", t)
            }
        })
    }

    private fun loadWarehouseForFirm() {
        ezBuildAPIInterface.getWarehouseForFirm(mPreferences.getInt(Constants.USER_FIRM_ID, 0)).enqueue(object:
            Callback<WarehouseForFirmResponse> {
            override fun onResponse(
                call: Call<WarehouseForFirmResponse>,
                response: Response<WarehouseForFirmResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    println(mWarehouse)
                    mWarehouse = response.body()!!.warehouse
                }
            }

            override fun onFailure(call: Call<WarehouseForFirmResponse>, t: Throwable) {
                (activity as MainActivity).hideProgressDialog()
                (activity as MainActivity).showSnackBar(getString(R.string.error_getting_warehouse_details), true)
                Log.e("WAREHOUSE ERROR", "Error getting warehouse from API")
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_employees, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_employees_add_employee -> {
                findNavController().navigate(EmployeesFragmentDirections.actionNavigationEmployeesToAddEmployeeFragment())

                if (requireActivity() is MainActivity) {
                    (activity as MainActivity).hideBottomNavigationView()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerViewAdapters() {

        mEngineers.sortBy { it.fullName }
        mPhysicalWorkers.sortBy { it.fullName }
        mWarehouseManager.sortBy { it.fullName }

        mBinding.rvEmployeesEngineers.layoutManager = LinearLayoutManager(requireContext())
        mEngineersAdapter = EmployeeAdapter(this@EmployeesFragment, mEngineers)
        mBinding.rvEmployeesEngineers.adapter = mEngineersAdapter

        mBinding.rvEmployeesPhysicalWorkers.layoutManager = LinearLayoutManager(requireContext())
        mPhysicalWorkersAdapter = EmployeeAdapter(this@EmployeesFragment, mPhysicalWorkers)
        mBinding.rvEmployeesPhysicalWorkers.adapter = mPhysicalWorkersAdapter

        mBinding.rvEmployeesWarehouseManager.layoutManager = LinearLayoutManager(requireContext())
        mWarehouseManagersAdapter = EmployeeAdapter(this@EmployeesFragment, mWarehouseManager)
        mBinding.rvEmployeesWarehouseManager.adapter = mWarehouseManagersAdapter
    }

    private fun toggleRecyclerViewVisibility() {

        if (mEngineers.isNotEmpty()) {
            mBinding.rvEmployeesEngineers.visibility = View.VISIBLE
            mBinding.tvEmployeesNoEngineersAdded.visibility = View.GONE
        }

        if (mPhysicalWorkers.isNotEmpty()) {
            mBinding.rvEmployeesPhysicalWorkers.visibility = View.VISIBLE
            mBinding.tvEmployeesNoPhysicalWorkersAdded.visibility = View.GONE
        }

        if (mWarehouseManager.isNotEmpty()) {
            mBinding.rvEmployeesWarehouseManager.visibility = View.VISIBLE
            mBinding.tvEmployeesNoWarehouseManagerAddedYet.visibility = View.GONE
        }
    }

    fun showEmployeeDetailsFragment(employee: Employee) {
        findNavController().navigate(EmployeesFragmentDirections.actionNavigationEmployeesToEmployeeDetailsFragment(employee, mWarehouse, mConstructionSites.toTypedArray()))

        if (requireActivity() is MainActivity) {
            (activity as MainActivity).hideBottomNavigationView()
        }
    }

    fun showDeleteEmployeeDialog(employee: Employee) {

        val builder = AlertDialog.Builder(requireActivity(), R.style.AlertDialogTheme)
        builder.setTitle(resources.getString(R.string.action_delete_employee))
        builder.setMessage(resources.getString(R.string.msg_delete_employee_dialog))
        builder.setIcon(resources.getDrawable(R.drawable.ic_warning_24))
        builder.setPositiveButton(resources.getString(R.string.btn_yes)) { dialogInterface, _ ->

            if (employee.employeeTypeID == EmployeeType.ENGINEER.typeID) {
                if (mConstructionSites.find { it.constructionSiteManager!!.idEmployee == employee.idEmployee } != null) {
                    showWarningDeleteEmployeeDialog(getString(R.string.lbl_warning_delete_engineer))
                } else {
                    deleteEmployee(employee)
                }
            }
            else if (employee.employeeTypeID == EmployeeType.WAREHOUSE_MANAGER.typeID) {
                mWarehouse.warehouseManager?.let {
                    if (mWarehouse.warehouseManager!!.idEmployee == employee.idEmployee) {
                        showWarningDeleteEmployeeDialog(getString(R.string.lbl_warning_delete_warehouse_manager))
                    } else {
                        deleteEmployee(employee)
                    }
                }
            }
            else {
                deleteEmployee(employee)
            }
            dialogInterface.dismiss()
        }

        builder.setNegativeButton(resources.getString(R.string.btn_no)) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun deleteEmployee(employee: Employee) {
        (activity as MainActivity).showProgressDialog(resources.getString(R.string.please_wait))

        ezBuildAPIInterface.deleteEmployee(employee.idEmployee!!).enqueue(object:
            Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    if (employee.employeeTypeID == EmployeeType.ENGINEER.typeID) {
                        mEngineers.remove(employee)
                    } else if (employee.employeeTypeID == EmployeeType.PHYSICAL_WORKER.typeID) {
                        mPhysicalWorkers.remove(employee)
                    } else if (employee.employeeTypeID == EmployeeType.WAREHOUSE_MANAGER.typeID) {
                        mWarehouseManager.remove(employee)
                    }
                    setupRecyclerViewAdapters()
                    (activity as MainActivity).hideProgressDialog()
                    (activity as MainActivity).showSnackBar(getString(R.string.success_api_delete_employee), false)
                }
            }

            override fun onFailure(
                call: Call<BaseResponse>,
                t: Throwable
            ) {
                (activity as MainActivity).hideProgressDialog()
                (activity as MainActivity).showSnackBar(getString(R.string.error_api_delete_employee), true)
                Log.e(requireActivity().javaClass.simpleName, "Error while deleting employee.", t)
            }
        })
    }

    private fun showWarningDeleteEmployeeDialog(warningMessage: String) {
        val builder = AlertDialog.Builder(requireActivity(), R.style.AlertDialogTheme)
        builder.setTitle(resources.getString(R.string.action_delete_employee))
        builder.setMessage(warningMessage)
        builder.setIcon(resources.getDrawable(R.drawable.ic_warning_24))
        builder.setPositiveButton(resources.getString(R.string.btn_close)) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    override fun onRefresh() {
        mBinding.employeesSwipeRefresh.isRefreshing = true
        refreshEmployees()
    }

    private fun refreshEmployees() {

        ezBuildAPIInterface.getEmployeesForFirm(mPreferences.getInt(Constants.USER_FIRM_ID, 0)).enqueue(object:
            Callback<EmployeesForFirmResponse> {
            override fun onResponse(
                call: Call<EmployeesForFirmResponse>,
                response: Response<EmployeesForFirmResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    mEngineers = ArrayList()
                    mPhysicalWorkers = ArrayList()
                    mWarehouseManager = ArrayList()
                    mConstructionSites = ArrayList()

                    val employees = response.body()!!.employees

                    employees.forEach {
                        if (it.employeeTypeID == EmployeeType.ENGINEER.typeID) {
                            mEngineers.add(it)
                        } else if (it.employeeTypeID == EmployeeType.PHYSICAL_WORKER.typeID) {
                            mPhysicalWorkers.add(it)
                        } else if (it.employeeTypeID == EmployeeType.WAREHOUSE_MANAGER.typeID) {
                            mWarehouseManager.add(it)
                        }
                    }

                    refreshConstructionSites()

                    setupRecyclerViewAdapters()
                    toggleRecyclerViewVisibility()

                    mBinding.employeesSwipeRefresh.isRefreshing = false
                }
            }

            override fun onFailure(
                call: Call<EmployeesForFirmResponse>,
                t: Throwable
            ) {
                (activity as MainActivity).showSnackBar(getString(R.string.error_api_employees_loading_fail), true)
                Log.e(requireActivity().javaClass.simpleName, "Error while getting employees from API.", t)
            }
        })
    }

    private fun refreshConstructionSites() {

        ezBuildAPIInterface.getConstructionSitesForFirmNoImage(mPreferences.getInt(Constants.USER_FIRM_ID, 0)).enqueue(object:
            Callback<ConstructionSitesResponse> {
            override fun onResponse(
                call: Call<ConstructionSitesResponse>,
                response: Response<ConstructionSitesResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    mConstructionSites = response.body()!!.constructionSites
                }
            }

            override fun onFailure(
                call: Call<ConstructionSitesResponse>,
                t: Throwable
            ) {
                (activity as MainActivity).showSnackBar(getString(R.string.error_api_construction_sites_loading_fail), true)
                Log.e(requireActivity().javaClass.simpleName, "Error while getting construction sites from API.", t)
            }
        })
    }

    fun navigateToEmployeeEditFragment(employee: Employee) {
        findNavController().navigate(EmployeesFragmentDirections.actionNavigationEmployeesToEditEmployeeFragment(employee, mWarehouse, mConstructionSites.toTypedArray(), Constants.FRAGMENT_EMPLOYEES))
        if (requireActivity() is MainActivity) {
            (activity as MainActivity).hideBottomNavigationView()
        }
    }

    fun phoneEmployee(employee: Employee) {

        Dexter.withContext(requireContext()).withPermission(
            Manifest.permission.CALL_PHONE,
        ).withListener(object: PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" +  employee.phone))
                startActivity(intent)
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                Toast.makeText(requireContext(), getString(R.string.denied_phone_call_permission), Toast.LENGTH_SHORT).show()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?,
                p1: PermissionToken?
            ) {
                showRationalDialogForPermissions()
            }

        }).onSameThread().check()
    }

    private fun showRationalDialogForPermissions() {
        android.app.AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.msg_permissions_turned_off))
            .setPositiveButton(getString(R.string.btn_go_to_settings))
            {_,_ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", requireContext().packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton(getString(R.string.btn_cancel)) {dialog, _ ->
                dialog.dismiss()
            }.show()
    }
}