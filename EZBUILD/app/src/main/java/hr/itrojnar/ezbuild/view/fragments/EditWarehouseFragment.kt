package hr.itrojnar.ezbuild.view.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.DialogCustomListBinding
import hr.itrojnar.ezbuild.databinding.FragmentEditWarehouseBinding
import hr.itrojnar.ezbuild.model.messaging.BaseResponse
import hr.itrojnar.ezbuild.model.messaging.employee.EmployeesForFirmResponse
import hr.itrojnar.ezbuild.model.messaging.warehouse.CreateUpdateWarehouseRequest
import hr.itrojnar.ezbuild.model.network.EzBuildAPIInterface
import hr.itrojnar.ezbuild.model.viewModels.Employee
import hr.itrojnar.ezbuild.utils.Constants
import hr.itrojnar.ezbuild.view.activities.MainActivity
import hr.itrojnar.ezbuild.view.adapters.CustomListEmployeeAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditWarehouseFragment : Fragment() {

    private lateinit var mBinding: FragmentEditWarehouseBinding

    private lateinit var mCustomListDialog: Dialog
    private var mWarehouseManagers: ArrayList<Employee> = arrayListOf()
    private var mWarehouseManagerID: Int? = null
    private lateinit var mPreferences: SharedPreferences
    private val ezBuildAPIInterface = EzBuildAPIInterface.create()

    companion object {
        private const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentEditWarehouseBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPreferences = requireActivity().getSharedPreferences(Constants.EZBUILD_PREFERENCES, Context.MODE_PRIVATE)
        (activity as MainActivity).useUpButton()
        val args: EditWarehouseFragmentArgs by navArgs()
        mWarehouseManagerID = args.warehouse.idWarehouse

        mBinding.etEditWarehouseAddress.setText(args.warehouse.fullAddress)
        mBinding.etEditWarehouseManager.setText(args.warehouse.warehouseManager?.fullName)

        loadWarehouseManager()
    }

    private fun loadWarehouseManager() {

        (activity as MainActivity).showProgressDialog(resources.getString(R.string.please_wait))

        ezBuildAPIInterface.getWarehouseManagerForFirm(mPreferences.getInt(Constants.USER_FIRM_ID, 0)).enqueue(object:
            Callback<EmployeesForFirmResponse> {
            override fun onResponse(
                call: Call<EmployeesForFirmResponse>,
                response: Response<EmployeesForFirmResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    mWarehouseManagers = response.body()!!.employees
                    (activity as MainActivity).hideProgressDialog()
                }
            }

            override fun onFailure(call: Call<EmployeesForFirmResponse>, t: Throwable) {
                (activity as MainActivity).hideProgressDialog()
                (activity as MainActivity).showSnackBar(getString(R.string.error_getting_warehouse_manager), true)
                Log.e("WAREHOUSE ERROR", "Error fetching warehouse manager")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if(!Places.isInitialized()) {
            Places.initialize(requireActivity().applicationContext, resources.getString(R.string.google_maps_api_key))
        }
        setupListeners()
    }

    private fun setupListeners() {
        mBinding.etEditWarehouseAddress.setOnClickListener {
            try {
                val fields = listOf(Place.Field.ADDRESS)
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(requireContext())
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE
                )
            } catch (e:Exception) { }
        }
        mBinding.etEditWarehouseManager.setOnClickListener {
            customItemsDialog(getString(R.string.title_select_warehouse_manager), mWarehouseManagers)
        }
        mBinding.btnEditSaveWarehouse.setOnClickListener {
            validateEntriesAndSave()
        }
    }

    private fun validateEntriesAndSave() {
        val warehouseAddress = mBinding.etEditWarehouseAddress.text.toString().trim()
        val warehouseManager = mBinding.etEditWarehouseManager.text.toString().trim()

        when {
            TextUtils.isEmpty(warehouseAddress) -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_enter_warehouse_address),
                    Toast.LENGTH_SHORT
                ).show()
            }
            TextUtils.isEmpty(warehouseManager) -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_select_warehouse_manager),
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                val createUpdateWarehouseRequest = CreateUpdateWarehouseRequest(
                    mPreferences.getInt(Constants.USER_FIRM_ID, 0),
                    warehouseAddress,
                    mWarehouseManagerID
                )
                updateWarehouse(createUpdateWarehouseRequest)
            }
        }
    }

    private fun updateWarehouse(createUpdateWarehouseRequest: CreateUpdateWarehouseRequest) {

        (activity as MainActivity).showProgressDialog(getString(R.string.please_wait))

        ezBuildAPIInterface.createUpdateWarehouse(createUpdateWarehouseRequest).enqueue(object:
            Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    (activity as MainActivity).hideProgressDialog()
                    (activity as MainActivity).showSnackBar(getString(R.string.edit_warehouse_success), false)
                    (activity as MainActivity).useHamburgerButton()
                    findNavController().navigate(EditWarehouseFragmentDirections.actionEditWarehouseFragmentToNavigationWarehouse())
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                (activity as MainActivity).hideProgressDialog()
                (activity as MainActivity).showSnackBar(getString(R.string.edit_warehouse_error), true)
                Log.e("WAREHOUSE ERROR", "Error updating warehouse.")
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE && resultCode != Activity.RESULT_CANCELED) {
            val place: Place = Autocomplete.getPlaceFromIntent(data!!)
            mBinding.etEditWarehouseAddress.setText(place.address)
        }
    }

    private fun customItemsDialog(title: String, itemsList: List<Employee>) {
        mCustomListDialog = Dialog(requireContext())
        val binding : DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)

        mCustomListDialog.setContentView(binding.root)
        binding.tvDialogCustomListTitle.text = title

        binding.rvDialogCustomList.layoutManager = LinearLayoutManager(requireContext())

        if (mWarehouseManagers.isEmpty()) {
            binding.tvCustomListNoMoreUnassignedEmployees.visibility = View.VISIBLE
            binding.tvCustomListNoMoreUnassignedEmployees.text = getString(R.string.lbl_no_warehouse_managers_for_firm)
        }

        val adapter = CustomListEmployeeAdapter(requireActivity(), this@EditWarehouseFragment, itemsList, Constants.WAREHOUSE_MANAGER)
        binding.rvDialogCustomList.adapter = adapter
        mCustomListDialog.show()
    }

    fun selectedWarehouseManager(employee: Employee) {
        mCustomListDialog.dismiss()
        mWarehouseManagerID = employee.idEmployee
        mBinding.etEditWarehouseManager.setText(employee.fullName)
    }
}