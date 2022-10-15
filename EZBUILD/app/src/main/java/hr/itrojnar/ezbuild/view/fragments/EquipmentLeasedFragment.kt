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
import hr.itrojnar.ezbuild.databinding.FragmentEquipmentLeasedBinding
import hr.itrojnar.ezbuild.model.messaging.BaseResponse
import hr.itrojnar.ezbuild.model.messaging.warehouse.DeleteEquipmentHistoryRequest
import hr.itrojnar.ezbuild.model.messaging.warehouse.EquipmentHistoryForWarehouseResponse
import hr.itrojnar.ezbuild.model.network.EzBuildAPIInterface
import hr.itrojnar.ezbuild.model.viewModels.Equipment
import hr.itrojnar.ezbuild.model.viewModels.EquipmentHistory
import hr.itrojnar.ezbuild.model.viewModels.EquipmentHistoryGrouped
import hr.itrojnar.ezbuild.utils.Constants
import hr.itrojnar.ezbuild.view.activities.MainActivity
import hr.itrojnar.ezbuild.view.adapters.EquipmentLeasedAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EquipmentLeasedFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var mBinding: FragmentEquipmentLeasedBinding
    private lateinit var mEquipmentHistoryGrouped: ArrayList<EquipmentHistoryGrouped>
    private lateinit var mEquipmentLeasedAdapter: EquipmentLeasedAdapter

    private val ezBuildAPIInterface = EzBuildAPIInterface.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentEquipmentLeasedBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).useUpButton()
        mBinding.equipmentHistorySwipeRefresh.setOnRefreshListener(this)
        val args: EquipmentLeasedFragmentArgs by navArgs()
        loadEquipmentHistoryForWarehouse(args.warehouseID)
    }

    private fun loadEquipmentHistoryForWarehouse(warehouseID: Int) {

        (activity as MainActivity).showProgressDialog(getString(R.string.please_wait))

        ezBuildAPIInterface.getEquipmentHistoryForWarehouse(warehouseID).enqueue(object:
            Callback<EquipmentHistoryForWarehouseResponse> {
            override fun onResponse(
                call: Call<EquipmentHistoryForWarehouseResponse>,
                response: Response<EquipmentHistoryForWarehouseResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    setupData(response.body()!!.equipmentHistory)
                    setupRecyclerView()
                    (activity as MainActivity).hideProgressDialog()
                }
            }

            override fun onFailure(call: Call<EquipmentHistoryForWarehouseResponse>, t: Throwable) {
                (activity as MainActivity).hideProgressDialog()
                (activity as MainActivity).showSnackBar(getString(R.string.error_equipment_history_fetch), true)
                Log.e("EQUIPMENT HISTORY ERROR", "Error fetching equipment history for warehouse")
            }
        })
    }

    private fun setupData(equipmentHistory: ArrayList<EquipmentHistory>) {
        mEquipmentHistoryGrouped = arrayListOf()
        for ((index, value) in equipmentHistory.withIndex()) {
            val listOfEquipment: ArrayList<Equipment> = arrayListOf()
            listOfEquipment.add(value.equipment!!)
            val equipmentHistoryGroupedItem = EquipmentHistoryGrouped(value.employee, value.dateEquipmentTaken, value.quantityTaken, listOfEquipment, value.warehouseID)
            if (index == 0) {
                mEquipmentHistoryGrouped.add(equipmentHistoryGroupedItem)
            }
            else {
                if (mEquipmentHistoryGrouped.find { it.employee == equipmentHistoryGroupedItem.employee
                            && it.dateEquipmentTaken == equipmentHistoryGroupedItem.dateEquipmentTaken } != null) {
                    val testIndex = mEquipmentHistoryGrouped.withIndex().first { it.value.employee == equipmentHistoryGroupedItem.employee
                            && it.value.dateEquipmentTaken == equipmentHistoryGroupedItem.dateEquipmentTaken }.index
                    mEquipmentHistoryGrouped[testIndex].equipment.add(value.equipment!!)
                    mEquipmentHistoryGrouped[testIndex].quantityTaken += value.quantityTaken
                } else {
                    mEquipmentHistoryGrouped.add(equipmentHistoryGroupedItem)
                }
            }
            mEquipmentHistoryGrouped.sortByDescending { it.dateEquipmentTaken }
        }
    }

    private fun setupRecyclerView() {
        mBinding.rvLeasedEquipment.layoutManager = LinearLayoutManager(requireContext())
        mEquipmentLeasedAdapter = EquipmentLeasedAdapter(this@EquipmentLeasedFragment, mEquipmentHistoryGrouped)
        mBinding.rvLeasedEquipment.adapter = mEquipmentLeasedAdapter

        toggleRecyclerViewVisibility()
    }

    private fun toggleRecyclerViewVisibility() {
        if (mEquipmentHistoryGrouped.isNotEmpty()) {
            mBinding.llWarehouseLeasedEquipment.visibility = View.VISIBLE
            mBinding.llNoLeasedEquipment.visibility = View.GONE
        }
        else {
            mBinding.llWarehouseLeasedEquipment.visibility = View.GONE
            mBinding.llNoLeasedEquipment.visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_leased_equipment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_leased_equipment_lease -> {
                val args: EquipmentLeasedFragmentArgs by navArgs()
                findNavController().navigate(EquipmentLeasedFragmentDirections.actionEquipmentLeasedFragmentToNewLeaseEquipmentFragment(args.warehouseID))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun leasedEquipmentDetails(leasedEquipment: EquipmentHistoryGrouped) {
        findNavController().navigate(EquipmentLeasedFragmentDirections.actionEquipmentLeasedFragmentToLeasedEquipmentDetailsFragment(leasedEquipment.employee.idEmployee!!, leasedEquipment.dateEquipmentTaken!!))
    }

    fun editLeasedEquipment(leasedEquipment: EquipmentHistoryGrouped) {
        findNavController().navigate(EquipmentLeasedFragmentDirections.actionEquipmentLeasedFragmentToEditLeasedEquipmentFragment(
            leasedEquipment.employee.idEmployee!!, leasedEquipment.dateEquipmentTaken!!, leasedEquipment.warehouseID!!, Constants.FRAGMENT_EQUIPMENT_LEASED)
        )
    }

    fun deleteLeasedEquipment(leasedEquipment: EquipmentHistoryGrouped) {

        val builder = AlertDialog.Builder(requireActivity(), R.style.AlertDialogTheme)
        builder.setTitle(resources.getString(R.string.title_delete_leased_equipment))
        builder.setMessage(resources.getString(R.string.msg_delete_leased_equipment))
        builder.setIcon(resources.getDrawable(R.drawable.ic_warning_24))
        builder.setPositiveButton(resources.getString(R.string.btn_yes)) { dialogInterface, _ ->

            dialogInterface.dismiss()

            (activity as MainActivity).showProgressDialog(resources.getString(R.string.please_wait))

            val deleteEquipmentHistoryRequest = DeleteEquipmentHistoryRequest(leasedEquipment.employee.idEmployee!!, leasedEquipment.dateEquipmentTaken!!)

            ezBuildAPIInterface.deleteEquipmentHistory(deleteEquipmentHistoryRequest).enqueue(object:
                Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if(response.body() != null && response.body()!!.isSuccessful) {
                        (activity as MainActivity).hideProgressDialog()
                        (activity as MainActivity).showSnackBar(getString(R.string.success_delete_equipment_history), false)
                        mEquipmentHistoryGrouped.remove(leasedEquipment)
                        setupRecyclerView()
                    }
                }

                override fun onFailure(
                    call: Call<BaseResponse>,
                    t: Throwable
                ) {
                    (activity as MainActivity).hideProgressDialog()
                    (activity as MainActivity).showSnackBar(getString(R.string.error_api_delete_equipment_history_fail), true)
                    Log.e(requireActivity().javaClass.simpleName, "Error while deleting equipment history on API.", t)
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
        mBinding.equipmentHistorySwipeRefresh.isRefreshing = true
        refreshEquipmentHistoryForWarehouse()
    }

    private fun refreshEquipmentHistoryForWarehouse() {

        val args: EquipmentLeasedFragmentArgs by navArgs()

        ezBuildAPIInterface.getEquipmentHistoryForWarehouse(args.warehouseID).enqueue(object:
            Callback<EquipmentHistoryForWarehouseResponse> {
            override fun onResponse(
                call: Call<EquipmentHistoryForWarehouseResponse>,
                response: Response<EquipmentHistoryForWarehouseResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    mBinding.equipmentHistorySwipeRefresh.isRefreshing = false
                    setupData(response.body()!!.equipmentHistory)
                    setupRecyclerView()
                }
            }

            override fun onFailure(call: Call<EquipmentHistoryForWarehouseResponse>, t: Throwable) {
                mBinding.equipmentHistorySwipeRefresh.isRefreshing = false
                (activity as MainActivity).showSnackBar(getString(R.string.error_equipment_history_fetch), true)
                Log.e("EQUIPMENT HISTORY ERROR", "Error fetching equipment history for warehouse")
            }
        })
    }
}