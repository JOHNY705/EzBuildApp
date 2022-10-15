package hr.itrojnar.ezbuild.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.FragmentEquipmentBinding
import hr.itrojnar.ezbuild.model.messaging.BaseResponse
import hr.itrojnar.ezbuild.model.messaging.warehouse.EquipmentForWarehouseResponse
import hr.itrojnar.ezbuild.model.network.EzBuildAPIInterface
import hr.itrojnar.ezbuild.model.viewModels.Equipment
import hr.itrojnar.ezbuild.utils.Constants
import hr.itrojnar.ezbuild.view.activities.MainActivity
import hr.itrojnar.ezbuild.view.adapters.EquipmentAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EquipmentFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var mBinding: FragmentEquipmentBinding

    private lateinit var mEquipmentAdapter: EquipmentAdapter
    private var mEquipment: ArrayList<Equipment> = arrayListOf()

    private val ezBuildAPIInterface = EzBuildAPIInterface.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentEquipmentBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).useUpButton()
        mBinding.equipmentSwipeRefresh.setOnRefreshListener(this)
        loadEquipmentForWarehouse()
    }

    private fun loadEquipmentForWarehouse() {

        (activity as MainActivity).showProgressDialog(getString(R.string.please_wait))
        val args: EquipmentFragmentArgs by navArgs()
        ezBuildAPIInterface.getEquipmentForWarehouse(args.warehouseID).enqueue(object:
            Callback<EquipmentForWarehouseResponse> {
            override fun onResponse(
                call: Call<EquipmentForWarehouseResponse>,
                response: Response<EquipmentForWarehouseResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    mEquipment = response.body()!!.equipment
                    mEquipment.sortBy { it.equipmentName }
                    (activity as MainActivity).hideProgressDialog()
                    setupRecyclerViewAdapter()
                }
            }

            override fun onFailure(call: Call<EquipmentForWarehouseResponse>, t: Throwable) {
                (activity as MainActivity).hideProgressDialog()
                (activity as MainActivity).showSnackBar(getString(R.string.erorr_equipment_fetch), true)
                Log.e("EQUIPMENT ERROR", "Error fetching equipment for warehouse.")
            }
        })
    }

    private fun setupRecyclerViewAdapter() {

        mBinding.rvWarehouseEquipment.layoutManager = LinearLayoutManager(requireContext())
        mEquipmentAdapter = EquipmentAdapter(this@EquipmentFragment, mEquipment)
        mBinding.rvWarehouseEquipment.adapter = mEquipmentAdapter

        toggleRecyclerViewVisibility()
    }

    private fun toggleRecyclerViewVisibility() {

        if (mEquipment.isNotEmpty()) {
            mBinding.llWarehouseEquipment.visibility = View.VISIBLE
            mBinding.llWarehouseNoEquipment.visibility = View.GONE
        }
        else {
            mBinding.llWarehouseEquipment.visibility = View.GONE
            mBinding.llWarehouseNoEquipment.visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_equipment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_equipment_add_equipment -> {
                val args: EquipmentFragmentArgs by navArgs()
                findNavController().navigate(EquipmentFragmentDirections.actionEquipmentFragmentToAddEquipmentFragment(args.warehouseID))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun equipmentDetails(equipmentItem: Equipment) {
        val args: EquipmentFragmentArgs by navArgs()
        findNavController().navigate(EquipmentFragmentDirections.actionEquipmentFragmentToEquipmentDetailsFragment(equipmentItem, args.warehouseID))
    }

    fun editEquipment(equipmentItem: Equipment) {
        val args: EquipmentFragmentArgs by navArgs()
        findNavController().navigate(EquipmentFragmentDirections.actionEquipmentFragmentToEditEquipmentFragment(equipmentItem, Constants.FRAGMENT_EQUIPMENT, args.warehouseID))
    }

    fun deleteEquipment(equipmentItem: Equipment) {

        val builder = AlertDialog.Builder(requireActivity(), R.style.AlertDialogTheme)
        builder.setTitle(resources.getString(R.string.title_delete_equipment))
        builder.setMessage(resources.getString(R.string.msg_delete_equipment_dialog))
        builder.setIcon(resources.getDrawable(R.drawable.ic_warning_24))
        builder.setPositiveButton(resources.getString(R.string.btn_yes)) { dialogInterface, _ ->

            dialogInterface.dismiss()

            (activity as MainActivity).showProgressDialog(resources.getString(R.string.please_wait))

            ezBuildAPIInterface.deleteEquipment(equipmentItem.idEquipment!!).enqueue(object:
                Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if(response.body() != null && response.body()!!.isSuccessful) {
                        mEquipment.remove(equipmentItem)
                        setupRecyclerViewAdapter()
                        (activity as MainActivity).hideProgressDialog()
                        (activity as MainActivity).showSnackBar(getString(R.string.success_delete_equipment), false)
                    }
                }

                override fun onFailure(
                    call: Call<BaseResponse>,
                    t: Throwable
                ) {
                    (activity as MainActivity).hideProgressDialog()
                    (activity as MainActivity).showSnackBar(getString(R.string.error_api_delete_equipment_fail), true)
                    Log.e(requireActivity().javaClass.simpleName, "Error while deleting equipment on API.", t)
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
        mBinding.equipmentSwipeRefresh.isRefreshing = true
        refreshEquipment()
    }

    private fun refreshEquipment() {

        val args: EquipmentFragmentArgs by navArgs()
        ezBuildAPIInterface.getEquipmentForWarehouse(args.warehouseID).enqueue(object:
            Callback<EquipmentForWarehouseResponse> {
            override fun onResponse(
                call: Call<EquipmentForWarehouseResponse>,
                response: Response<EquipmentForWarehouseResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    mEquipment = response.body()!!.equipment
                    mEquipment.sortBy { it.equipmentName }
                    mBinding.equipmentSwipeRefresh.isRefreshing = false
                    setupRecyclerViewAdapter()
                }
            }

            override fun onFailure(call: Call<EquipmentForWarehouseResponse>, t: Throwable) {
                mBinding.equipmentSwipeRefresh.isRefreshing = false
                (activity as MainActivity).showSnackBar(getString(R.string.erorr_equipment_fetch), true)
                Log.e("EQUIPMENT ERROR", "Error fetching equipment for warehouse.")
            }
        })
    }
}