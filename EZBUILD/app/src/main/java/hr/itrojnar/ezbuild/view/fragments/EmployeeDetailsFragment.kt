package hr.itrojnar.ezbuild.view.fragments

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.FragmentEmployeeDetailsBinding
import hr.itrojnar.ezbuild.model.messaging.constructionSite.ConstructionSitesResponse
import hr.itrojnar.ezbuild.model.messaging.employeeHours.EmployeeHoursMonthResponse
import hr.itrojnar.ezbuild.model.viewModels.ConstructionSite
import hr.itrojnar.ezbuild.model.viewModels.Employee
import hr.itrojnar.ezbuild.model.viewModels.EmployeeHoursMonth
import hr.itrojnar.ezbuild.model.network.EzBuildAPIInterface
import hr.itrojnar.ezbuild.enums.EmployeeType
import hr.itrojnar.ezbuild.model.messaging.BaseResponse
import hr.itrojnar.ezbuild.model.viewModels.Warehouse
import hr.itrojnar.ezbuild.utils.Constants
import hr.itrojnar.ezbuild.view.activities.MainActivity
import hr.itrojnar.ezbuild.view.adapters.ConstructionSiteAdapter
import hr.itrojnar.ezbuild.view.adapters.EmployeeHoursMonthAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class EmployeeDetailsFragment : Fragment() {

    private lateinit var mEmployee: Employee
    private var mConstructionSites: ArrayList<ConstructionSite> = arrayListOf()
    private lateinit var mWarehouse: Warehouse
    private var mEmployeeHours: ArrayList<EmployeeHoursMonth> = arrayListOf()

    private lateinit var mBinding: FragmentEmployeeDetailsBinding

    private val ezBuildAPIInterface = EzBuildAPIInterface.create()

    private lateinit var mConstructionSiteAdapter: ConstructionSiteAdapter
    private lateinit var mEmployeeHoursMonthAdapter: EmployeeHoursMonthAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentEmployeeDetailsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: EmployeeDetailsFragmentArgs by navArgs()
        mEmployee = args.employee
        mWarehouse = args.warehouse

        (activity as MainActivity).useUpButton()

        setupView()

        (activity as MainActivity).showProgressDialog(resources.getString(R.string.please_wait))

        if (mEmployee.employeeTypeID != EmployeeType.WAREHOUSE_MANAGER.typeID) {
            loadConstructionSitesForEmployee()
        }

        loadEmployeeHours()
    }

    private fun loadConstructionSitesForEmployee() {

        ezBuildAPIInterface.getConstructionSitesForEmployee(mEmployee.idEmployee!!).enqueue(object:
            Callback<ConstructionSitesResponse> {
            override fun onResponse(
                call: Call<ConstructionSitesResponse>,
                response: Response<ConstructionSitesResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    mConstructionSites.addAll(response.body()!!.constructionSites)

                    setupConstructionSitesRecyclerView()
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

    private fun loadEmployeeHours() {

        ezBuildAPIInterface.getEmployeeHoursMonthForEmployee(mEmployee.idEmployee!!).enqueue(object:
            Callback<EmployeeHoursMonthResponse> {
            override fun onResponse(
                call: Call<EmployeeHoursMonthResponse>,
                response: Response<EmployeeHoursMonthResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    mEmployeeHours.addAll(response.body()!!.employeeHours)
                    mEmployeeHours.sortByDescending { it.dateWorkDone }

                    setupEmployeeHoursMonthRecyclerView()

                    (activity as MainActivity).hideProgressDialog()
                }
            }

            override fun onFailure(
                call: Call<EmployeeHoursMonthResponse>,
                t: Throwable
            ) {
                (activity as MainActivity).hideProgressDialog()
                (activity as MainActivity).showSnackBar(getString(R.string.error_api_construction_sites_loading_fail), true)
                Log.e(requireActivity().javaClass.simpleName, "Error while getting employee hours from API.", t)
            }
        })
    }

    private fun setupConstructionSitesRecyclerView() {

        mBinding.rvEmployeeDetailsConstructionSites.layoutManager = LinearLayoutManager(requireContext())
        mConstructionSiteAdapter = ConstructionSiteAdapter(this@EmployeeDetailsFragment, mConstructionSites,  1)
        mBinding.rvEmployeeDetailsConstructionSites.adapter = mConstructionSiteAdapter

        toggleRecyclerViewVisibility()
    }

    private fun setupEmployeeHoursMonthRecyclerView() {
        mBinding.rvEmployeeDetailsWorkHoursMonth.layoutManager = LinearLayoutManager(requireContext())
        mEmployeeHoursMonthAdapter = EmployeeHoursMonthAdapter(this@EmployeeDetailsFragment, mEmployeeHours)
        mBinding.rvEmployeeDetailsWorkHoursMonth.adapter = mEmployeeHoursMonthAdapter

        toggleRecyclerViewVisibility()
    }

    private fun toggleRecyclerViewVisibility() {

        if (mConstructionSites.isNotEmpty()) {
            mBinding.rvEmployeeDetailsConstructionSites.visibility = View.VISIBLE
            mBinding.tvEmployeeDetailsNoCs.visibility = View.GONE
        }

        if (mEmployeeHours.isNotEmpty()) {
            mBinding.rvEmployeeDetailsWorkHoursMonth.visibility = View.VISIBLE
            mBinding.tvEmployeeDetailsNoWorkHours.visibility = View.GONE
        }
    }

    private fun setupView() {
        mBinding.tvEmployeeDetailsFullName.text = mEmployee.fullName

        when (mEmployee.employeeTypeID) {
            EmployeeType.ENGINEER.typeID -> mBinding.tvEmployeeDetailsType.text = getString(R.string.type_engineer)
            EmployeeType.PHYSICAL_WORKER.typeID -> mBinding.tvEmployeeDetailsType.text = getString(R.string.type_physical_worker)
            EmployeeType.WAREHOUSE_MANAGER.typeID -> {
                mBinding.tvEmployeeDetailsType.text = getString(R.string.type_warehouse_manager)
                mBinding.tvEmployeeConstructionSiteEmployee.visibility = View.GONE
                mBinding.tvEmployeeDetailsNoCs.visibility = View.GONE
            }
        }

        if (mEmployee.email!!.isNotEmpty()) {
            mBinding.tvEmployeeDetailsEmail.text = mEmployee.email
        }

        mBinding.tvEmployeeDetailsPhoneNumber.text = mEmployee.phone

        mBinding.ivCallEmployee.setOnClickListener {
            Dexter.withContext(requireContext()).withPermission(
                Manifest.permission.CALL_PHONE,
            ).withListener(object: PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" +  mEmployee.phone))
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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_employee_details, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_employee_details_edit -> {
                findNavController().navigate(EmployeeDetailsFragmentDirections.actionEmployeeDetailsFragmentToEditEmployeeFragment(mEmployee, mWarehouse, mConstructionSites.toTypedArray(), Constants.FRAGMENT_EMPLOYEE_DETAILS))
            }
            R.id.action_employee_details_delete -> {
                showDeleteEmployeeDialog()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showDeleteEmployeeDialog() {

        val args: EmployeeDetailsFragmentArgs by navArgs()
        val warehouse = args.warehouse
        val firmConstructionSites = args.constructionSites

        val builder = AlertDialog.Builder(requireActivity(), R.style.AlertDialogTheme)
        builder.setTitle(resources.getString(R.string.action_delete_employee))
        builder.setMessage(resources.getString(R.string.msg_delete_employee_dialog))
        builder.setIcon(resources.getDrawable(R.drawable.ic_warning_24))
        builder.setPositiveButton(resources.getString(R.string.btn_yes)) { dialogInterface, _ ->

            if (mEmployee.employeeTypeID == EmployeeType.ENGINEER.typeID) {
                if (firmConstructionSites.find { it.constructionSiteManager!!.idEmployee == mEmployee.idEmployee } != null) {
                    showWarningDeleteEmployeeDialog(getString(R.string.lbl_warning_delete_engineer))
                } else {
                    deleteEmployee(mEmployee)
                }
            }
            else if (mEmployee.employeeTypeID == EmployeeType.WAREHOUSE_MANAGER.typeID) {
                warehouse.warehouseManager?.let {
                    if (warehouse.warehouseManager!!.idEmployee == mEmployee.idEmployee) {
                        showWarningDeleteEmployeeDialog(getString(R.string.lbl_warning_delete_warehouse_manager))
                    } else {
                        deleteEmployee(mEmployee)
                    }
                }
            }
            else {
                deleteEmployee(mEmployee)
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

    private fun deleteEmployee(employee: Employee) {
        (activity as MainActivity).showProgressDialog(resources.getString(R.string.please_wait))

        ezBuildAPIInterface.deleteEmployee(employee.idEmployee!!).enqueue(object:
            Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    (activity as MainActivity).hideProgressDialog()
                    (activity as MainActivity).showSnackBar(getString(R.string.success_api_delete_employee), false)
                    findNavController().navigate(EmployeeDetailsFragmentDirections.actionEmployeeDetailsFragmentToNavigationEmployees())
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

    /*private fun formatWorkHours() {
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2022-05-23T00:00:00")
        val month = SimpleDateFormat("MMMM").format(date)
        println(month)
        println(Month.of(3).getDisplayName(TextStyle.FULL_STANDALONE, Locale.ENGLISH))
    }*/
}