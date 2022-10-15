package hr.itrojnar.ezbuild.view.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.FragmentWarehouseBinding
import hr.itrojnar.ezbuild.enums.EmployeeType
import hr.itrojnar.ezbuild.model.messaging.BaseResponse
import hr.itrojnar.ezbuild.model.messaging.warehouse.WarehouseForFirmResponse
import hr.itrojnar.ezbuild.model.network.EzBuildAPIInterface
import hr.itrojnar.ezbuild.model.viewModels.Warehouse
import hr.itrojnar.ezbuild.utils.Constants
import hr.itrojnar.ezbuild.view.activities.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WarehouseFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var mBinding: FragmentWarehouseBinding

    private lateinit var mPreferences: SharedPreferences
    private val ezBuildAPIInterface = EzBuildAPIInterface.create()
    private lateinit var mMenu: Menu
    private var mWarehouse = Warehouse()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentWarehouseBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.warehouseSwipeRefresh.setOnRefreshListener(this)
        mPreferences = requireActivity().getSharedPreferences(Constants.EZBUILD_PREFERENCES, Context.MODE_PRIVATE)
        setupListeners()
        loadWarehouseForFirm()
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is MainActivity) {
            (activity as MainActivity).useHamburgerButton()
            (activity as MainActivity).showBottomNavigationView()
        }
    }

    private fun setupListeners() {
        mBinding.btnAddWarehouse.setOnClickListener {
            findNavController().navigate(WarehouseFragmentDirections.actionNavigationWarehouseToAddWarehouseFragment())
            if (activity is MainActivity) {
                (activity as MainActivity).hideBottomNavigationView()
            }
        }
        mBinding.btnWarehouseEquipment.setOnClickListener {
            findNavController().navigate(WarehouseFragmentDirections.actionNavigationWarehouseToEquipmentFragment(mWarehouse.idWarehouse!!))
            if (activity is MainActivity) {
                (activity as MainActivity).hideBottomNavigationView()
            }
        }
        mBinding.btnWarehouseEquipmentInCharge.setOnClickListener {
            findNavController().navigate(WarehouseFragmentDirections.actionNavigationWarehouseToEquipmentInChargeFragment(mWarehouse.idWarehouse!!))
            if (activity is MainActivity) {
                (activity as MainActivity).hideBottomNavigationView()
            }
        }
    }

    private fun loadWarehouseForFirm() {

        (activity as MainActivity).showProgressDialog(resources.getString(R.string.please_wait))

        ezBuildAPIInterface.getWarehouseForFirm(mPreferences.getInt(Constants.USER_FIRM_ID, 0)).enqueue(object:
            Callback<WarehouseForFirmResponse> {
            override fun onResponse(
                call: Call<WarehouseForFirmResponse>,
                response: Response<WarehouseForFirmResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    mWarehouse = response.body()!!.warehouse
                    setupView()
                }
                else {
                    mBinding.llWarehouse.visibility = View.GONE
                    mBinding.llWarehouseNoWarehouse.visibility = View.VISIBLE
                    hideOptionsMenu()
                }
                (activity as MainActivity).hideProgressDialog()
            }

            override fun onFailure(call: Call<WarehouseForFirmResponse>, t: Throwable) {
                (activity as MainActivity).hideProgressDialog()
                (activity as MainActivity).showSnackBar(getString(R.string.error_getting_warehouse_details), true)
                Log.e("WAREHOUSE ERROR", "Error getting warehouse from API")
            }
        })
    }

    private fun hideOptionsMenu() {
        mMenu.removeItem(R.id.action_warehouse_edit)
        mMenu.removeItem(R.id.action_warehouse_delete)
    }

    private fun setupView() {

        mBinding.tvWarehouseAddress.text = mWarehouse.fullAddress
        mBinding.tvWarehouseManager.text = mWarehouse.warehouseManager?.fullName
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        mMenu = menu
        inflater.inflate(R.menu.menu_warehouse, menu)
        if (mPreferences.getInt(Constants.USER_TYPE_ID, 0) != EmployeeType.DIRECTOR.typeID) {
            mMenu.removeItem(R.id.action_warehouse_edit)
            mMenu.removeItem(R.id.action_warehouse_delete)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_warehouse_edit -> {
                findNavController().navigate(WarehouseFragmentDirections.actionNavigationWarehouseToEditWarehouseFragment(mWarehouse))

                if (activity is MainActivity) {
                    (activity as MainActivity).hideBottomNavigationView()
                }
            }
            R.id.action_warehouse_delete -> {
                deleteWarehouse()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteWarehouse() {
        val builder = AlertDialog.Builder(requireActivity(), R.style.AlertDialogTheme)
        builder.setTitle(resources.getString(R.string.title_delete_warehouse))
        builder.setMessage(resources.getString(R.string.msg_delete_warehouse))
        builder.setIcon(resources.getDrawable(R.drawable.ic_warning_24))
        builder.setPositiveButton(resources.getString(R.string.btn_yes)) { dialogInterface, _ ->

            dialogInterface.dismiss()

            println(mWarehouse.idWarehouse)

            (activity as MainActivity).showProgressDialog(resources.getString(R.string.please_wait))

            ezBuildAPIInterface.deleteWarehouse(mWarehouse.idWarehouse!!).enqueue(object:
                Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if(response.body() != null && response.body()!!.isSuccessful) {

                        mBinding.llWarehouse.visibility = View.GONE
                        mBinding.llWarehouseNoWarehouse.visibility = View.VISIBLE
                        hideOptionsMenu()
                        (activity as MainActivity).hideProgressDialog()
                        (activity as MainActivity).showSnackBar(getString(R.string.delete_warehouse_success), false)
                    }
                }

                override fun onFailure(
                    call: Call<BaseResponse>,
                    t: Throwable
                ) {
                    (activity as MainActivity).hideProgressDialog()
                    (activity as MainActivity).showSnackBar(getString(R.string.error_api_delete_warehouse_fail), true)
                    Log.e(requireActivity().javaClass.simpleName, "Error while deleting warehouse on API.", t)
                }
            })
        }

        builder.setNegativeButton(resources.getString(R.string.btn_no)) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    override fun onRefresh() {
        mBinding.warehouseSwipeRefresh.isRefreshing = true

        ezBuildAPIInterface.getWarehouseForFirm(mPreferences.getInt(Constants.USER_FIRM_ID, 0)).enqueue(object:
            Callback<WarehouseForFirmResponse> {
            override fun onResponse(
                call: Call<WarehouseForFirmResponse>,
                response: Response<WarehouseForFirmResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    mWarehouse = response.body()!!.warehouse
                    setupView()
                }
                else {
                    mBinding.llWarehouse.visibility = View.GONE
                    mBinding.llWarehouseNoWarehouse.visibility = View.VISIBLE
                    hideOptionsMenu()
                }
                mBinding.warehouseSwipeRefresh.isRefreshing = false
            }

            override fun onFailure(call: Call<WarehouseForFirmResponse>, t: Throwable) {
                (activity as MainActivity).showSnackBar(getString(R.string.error_getting_warehouse_details), true)
                Log.e("WAREHOUSE ERROR", "Error getting warehouse from API")
            }
        })
    }
}