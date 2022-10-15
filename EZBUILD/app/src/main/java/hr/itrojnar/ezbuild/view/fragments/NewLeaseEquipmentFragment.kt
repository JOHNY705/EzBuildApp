package hr.itrojnar.ezbuild.view.fragments

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.DialogCustomListBinding
import hr.itrojnar.ezbuild.databinding.FragmentNewLeaseEquipmentBinding
import hr.itrojnar.ezbuild.model.messaging.BaseResponse
import hr.itrojnar.ezbuild.model.messaging.employee.EmployeesForFirmResponse
import hr.itrojnar.ezbuild.model.messaging.warehouse.CreateEquipmentHistoryRequest
import hr.itrojnar.ezbuild.model.messaging.warehouse.EquipmentForWarehouseResponse
import hr.itrojnar.ezbuild.model.messaging.warehouse.EquipmentHistory
import hr.itrojnar.ezbuild.model.network.EzBuildAPIInterface
import hr.itrojnar.ezbuild.model.viewModels.Employee
import hr.itrojnar.ezbuild.model.viewModels.Equipment
import hr.itrojnar.ezbuild.utils.Constants
import hr.itrojnar.ezbuild.utils.EzBuildButton
import hr.itrojnar.ezbuild.view.activities.MainActivity
import hr.itrojnar.ezbuild.view.adapters.CustomListEmployeeAdapter
import hr.itrojnar.ezbuild.view.adapters.EquipmentAdapter
import hr.itrojnar.ezbuild.view.adapters.LeaseEquipmentAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewLeaseEquipmentFragment : Fragment() {

    private lateinit var mBinding: FragmentNewLeaseEquipmentBinding
    private lateinit var mPreferences: SharedPreferences

    private lateinit var mDialog: Dialog

    private var mResponsiblePerson = Employee()
    private var mEquipmentToLease: ArrayList<Equipment> = arrayListOf()
    private var mEquipment: ArrayList<Equipment> = arrayListOf()
    private var mPhysicalWorkers: ArrayList<Employee> = arrayListOf()
    private lateinit var mLeaseEquipmentAdapter: LeaseEquipmentAdapter

    private val ezBuildAPIInterface = EzBuildAPIInterface.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentNewLeaseEquipmentBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).useUpButton()
        mPreferences = requireActivity().getSharedPreferences(Constants.EZBUILD_PREFERENCES, Context.MODE_PRIVATE)
        val args: NewLeaseEquipmentFragmentArgs by navArgs()
        loadEquipmentForWarehouse(args.warehouseID)
        setupListeners()
    }

    private fun setupListeners() {
        mBinding.btnAddResponsiblePerson.setOnClickListener {
            customItemsDialog(getString(R.string.lbl_responsible_person), mPhysicalWorkers)
        }
        mBinding.ivRemoveEmployee.setOnClickListener {
            removeResponsiblePerson()
        }
        mBinding.btnAddLeasedEquipment.setOnClickListener {
            showEquipmentListDialog()
        }
        mBinding.btnLeaseEquipment.setOnClickListener {
            leaseEquipment()
        }
    }

    private fun leaseEquipment() {
        val leaseEquipment: ArrayList<EquipmentHistory> = arrayListOf()
        mEquipmentToLease.forEach {
            leaseEquipment.add(EquipmentHistory(it.idEquipment!!, it.quantity))
        }
        val args: NewLeaseEquipmentFragmentArgs by navArgs()
        val createEquipmentHistoryRequest = CreateEquipmentHistoryRequest(
            leaseEquipment, mResponsiblePerson.idEmployee!!, args.warehouseID
        )

        (activity as MainActivity).showProgressDialog(getString(R.string.please_wait))

        ezBuildAPIInterface.createEquipmentHistoryForWarehouse(createEquipmentHistoryRequest).enqueue(object:
            Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    (activity as MainActivity).hideProgressDialog()
                    (activity as MainActivity).showSnackBar(getString(R.string.success_lease_equipment), false)
                    findNavController().navigate(NewLeaseEquipmentFragmentDirections.actionNewLeaseEquipmentFragmentToEquipmentLeasedFragment(args.warehouseID))
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                (activity as MainActivity).hideProgressDialog()
                (activity as MainActivity).showSnackBar(getString(R.string.erorr_lease_equipment), true)
                Log.e("LEASE ERROR", "Error leasing equipment for employee.")
            }
        })
    }

    private fun loadEquipmentForWarehouse(warehouseID: Int) {

        (activity as MainActivity).showProgressDialog(getString(R.string.please_wait))

        ezBuildAPIInterface.getEquipmentForWarehouse(warehouseID).enqueue(object:
            Callback<EquipmentForWarehouseResponse> {
            override fun onResponse(
                call: Call<EquipmentForWarehouseResponse>,
                response: Response<EquipmentForWarehouseResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    mEquipment = response.body()!!.equipment
                    mEquipment.sortBy { it.equipmentName }
                    loadPhysicalWorkersForFirm()
                }
            }

            override fun onFailure(call: Call<EquipmentForWarehouseResponse>, t: Throwable) {
                (activity as MainActivity).hideProgressDialog()
                (activity as MainActivity).showSnackBar(getString(R.string.erorr_equipment_fetch), true)
                Log.e("EQUIPMENT ERROR", "Error fetching equipment for warehouse.")
            }
        })
    }

    private fun loadPhysicalWorkersForFirm() {
        ezBuildAPIInterface.getPhysicalWorkersForFirm(mPreferences.getInt(Constants.USER_FIRM_ID, 0)).enqueue(object:
            Callback<EmployeesForFirmResponse> {
            override fun onResponse(
                call: Call<EmployeesForFirmResponse>,
                response: Response<EmployeesForFirmResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    mPhysicalWorkers = response.body()!!.employees
                    mPhysicalWorkers.sortBy { it.fullName }
                    (activity as MainActivity).hideProgressDialog()
                }
            }

            override fun onFailure(call: Call<EmployeesForFirmResponse>, t: Throwable) {
                (activity as MainActivity).hideProgressDialog()
                (activity as MainActivity).showSnackBar(getString(R.string.error_api_employees_loading_fail), true)
                Log.e("EQUIPMENT ERROR", "Error fetching employees for firm.")
            }
        })
    }

    private fun customItemsDialog(title: String, itemsList: List<Employee>) {
        mDialog = Dialog(requireContext())
        val binding : DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)

        mDialog.setContentView(binding.root)
        binding.tvDialogCustomListTitle.text = title

        binding.rvDialogCustomList.layoutManager = LinearLayoutManager(requireContext())

        if (mPhysicalWorkers.isEmpty()) {
            binding.tvCustomListNoMoreUnassignedEmployees.visibility = View.VISIBLE
            binding.tvCustomListNoMoreUnassignedEmployees.text = getString(R.string.lbl_no_employees_to_select)
        }

        val adapter = CustomListEmployeeAdapter(requireActivity(), this@NewLeaseEquipmentFragment, itemsList, Constants.PHYSICAL_WORKER)
        binding.rvDialogCustomList.adapter = adapter
        mDialog.show()
    }

    fun selectedResponsiblePerson(employee: Employee) {

        mBinding.tvLeasedEquipmentEmployeeFullName.text = employee.fullName
        mBinding.tvLeasedEquipmentEmployeeType.text = getString(R.string.type_physical_worker)
        mBinding.tvLeasedEquipmentEmployeePhoneNumber.text = employee.phone

        mResponsiblePerson = employee

        mBinding.cwEmployeeLeasedEquipment.visibility = View.VISIBLE
        mBinding.btnAddResponsiblePerson.visibility = View.GONE

        mDialog.dismiss()
        toggleLeaseButtonVisibility()
    }

    private fun removeResponsiblePerson() {
        mResponsiblePerson = Employee()
        mBinding.cwEmployeeLeasedEquipment.visibility = View.GONE
        mBinding.btnAddResponsiblePerson.visibility = View.VISIBLE
        toggleLeaseButtonVisibility()
    }

    private fun showEquipmentListDialog() {
        mDialog = Dialog(requireActivity(), R.style.CustomDialog)
        mDialog.setContentView(R.layout.dialog_equipment)
        mDialog.window?.setLayout((resources.displayMetrics.widthPixels * 0.95).toInt(), (resources.displayMetrics.heightPixels * 0.85).toInt())

        val equipmentRecyclerView = mDialog.findViewById<RecyclerView>(R.id.rv_dialog_warehouse_equipment)
        equipmentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val equipmentAdapter = EquipmentAdapter(this@NewLeaseEquipmentFragment, mEquipment)
        equipmentRecyclerView.adapter = equipmentAdapter

        val cancelButton = mDialog.findViewById<EzBuildButton>(R.id.btn_dialog_equipment_cancel)
        cancelButton.setOnClickListener {
            mDialog.dismiss()
        }

        mDialog.show()
    }

    fun addEquipmentItemToLeaseItems(equipmentItem: Equipment) {
        if (equipmentItem.quantity > 0) {
            if (mEquipmentToLease.find { it.idEquipment == equipmentItem.idEquipment} != null) {
                val indexOfEquipmentLeaseItem = mEquipmentToLease.indexOfFirst { it.idEquipment == equipmentItem.idEquipment }
                mEquipmentToLease[indexOfEquipmentLeaseItem].quantity += 1

                val indexOfEquipment = mEquipment.indexOfFirst { it.idEquipment == equipmentItem.idEquipment }
                if (indexOfEquipment >= 0) {
                    mEquipment[indexOfEquipment].quantity -= 1
                }
            } else {
                val equipmentLeaseItem = Equipment(equipmentItem.idEquipment, equipmentItem.base64Image, equipmentItem.equipmentName, 1, equipmentItem.equipmentDescription)
                mEquipmentToLease.add(equipmentLeaseItem)
                val indexOf = mEquipment.indexOfFirst { it.idEquipment == equipmentItem.idEquipment }
                if (indexOf >= 0) {
                    mEquipment[indexOf].quantity -= 1
                }
            }
            mDialog.dismiss()

            setupRecyclerView()
            toggleLeaseButtonVisibility()
        } else {
            (activity as MainActivity).showSnackBar(getString(R.string.error_no_more_equipment_in_warehouse), true)
        }
    }

    private fun setupRecyclerView() {
        mBinding.rvLeasedEquipmentForEmployee.layoutManager = LinearLayoutManager(requireContext())
        mLeaseEquipmentAdapter = LeaseEquipmentAdapter(this@NewLeaseEquipmentFragment, mEquipmentToLease)
        mBinding.rvLeasedEquipmentForEmployee.adapter = mLeaseEquipmentAdapter

        if (mEquipmentToLease.isNotEmpty()) {
            mBinding.rvLeasedEquipmentForEmployee.visibility = View.VISIBLE
        } else {
            mBinding.rvLeasedEquipmentForEmployee.visibility = View.GONE
        }
    }

    fun removeOneQuantity(equipmentItem: Equipment) {
        val indexOfLeaseEquipment = mEquipmentToLease.indexOfFirst { it.idEquipment == equipmentItem.idEquipment }
        if (indexOfLeaseEquipment >= 0) {
            mEquipmentToLease[indexOfLeaseEquipment].quantity -= 1

            val indexOfEquipment = mEquipment.indexOfFirst { it.idEquipment == equipmentItem.idEquipment }
            if (indexOfEquipment >= 0) {
                mEquipment[indexOfEquipment].quantity += 1
            }

            if (mEquipmentToLease[indexOfLeaseEquipment].quantity == 0) {
                mEquipmentToLease.remove(equipmentItem)
            }
        }
        setupRecyclerView()
        toggleLeaseButtonVisibility()
    }

    fun addOneQuantity(equipmentItem: Equipment) {
        val indexOfLeaseEquipment = mEquipmentToLease.indexOfFirst { it.idEquipment == equipmentItem.idEquipment }
        if (indexOfLeaseEquipment >= 0) {

            val indexOfEquipment = mEquipment.indexOfFirst { it.idEquipment == equipmentItem.idEquipment }
            if (indexOfEquipment >= 0) {
                if (mEquipment[indexOfEquipment].quantity == 1) {
                    mEquipment[indexOfEquipment].quantity -= 1
                    mEquipmentToLease[indexOfLeaseEquipment].quantity += 1
                    setupRecyclerView()
                }
                else if (mEquipment[indexOfEquipment].quantity > 1) {
                    mEquipment[indexOfEquipment].quantity -= 1
                    mEquipmentToLease[indexOfLeaseEquipment].quantity += 1
                    setupRecyclerView()
                } else {
                    (activity as MainActivity).showSnackBar(getString(R.string.error_no_more_equipment_quantity_in_warehouse), true)
                }
            }
        }
    }

    fun removeLeaseItem(equipmentItem: Equipment) {
        val indexOfLeaseEquipment = mEquipmentToLease.indexOfFirst { it.idEquipment == equipmentItem.idEquipment }
        if (indexOfLeaseEquipment >= 0) {
            val indexOfEquipment = mEquipment.indexOfFirst { it.idEquipment == equipmentItem.idEquipment }
            if (indexOfEquipment >= 0) {
                mEquipment[indexOfEquipment].quantity += mEquipmentToLease[indexOfLeaseEquipment].quantity
            }
            mEquipmentToLease.remove(equipmentItem)
            setupRecyclerView()
            toggleLeaseButtonVisibility()
        }
    }

    private fun toggleLeaseButtonVisibility() {

        if (mResponsiblePerson.idEmployee != null && mEquipmentToLease.isNotEmpty()) {
            mBinding.btnLeaseEquipment.visibility = View.VISIBLE
        } else {
            mBinding.btnLeaseEquipment.visibility = View.GONE
        }
    }
}