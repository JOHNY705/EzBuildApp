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
import hr.itrojnar.ezbuild.databinding.FragmentEditLeasedEquipmentBinding
import hr.itrojnar.ezbuild.model.messaging.BaseResponse
import hr.itrojnar.ezbuild.model.messaging.employee.EmployeesForFirmResponse
import hr.itrojnar.ezbuild.model.messaging.warehouse.*
import hr.itrojnar.ezbuild.model.network.EzBuildAPIInterface
import hr.itrojnar.ezbuild.model.viewModels.Employee
import hr.itrojnar.ezbuild.model.viewModels.Equipment
import hr.itrojnar.ezbuild.model.viewModels.EquipmentHistory
import hr.itrojnar.ezbuild.utils.Constants
import hr.itrojnar.ezbuild.utils.EzBuildButton
import hr.itrojnar.ezbuild.view.activities.MainActivity
import hr.itrojnar.ezbuild.view.adapters.CustomListEmployeeAdapter
import hr.itrojnar.ezbuild.view.adapters.EquipmentAdapter
import hr.itrojnar.ezbuild.view.adapters.LeaseEquipmentAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditLeasedEquipmentFragment : Fragment() {

    private lateinit var mBinding: FragmentEditLeasedEquipmentBinding
    private lateinit var mPreferences: SharedPreferences

    private lateinit var mDialog: Dialog

    private var mEquipmentHistory: ArrayList<EquipmentHistory> = arrayListOf()
    private var mPreviousResponsiblePerson = Employee()
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
        mBinding = FragmentEditLeasedEquipmentBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).useUpButton()
        mPreferences = requireActivity().getSharedPreferences(Constants.EZBUILD_PREFERENCES, Context.MODE_PRIVATE)
        loadLeasedEquipmentDetails()
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
            editLeasedEquipment()
        }
    }

    private fun editLeasedEquipment() {
        val leaseEquipment: ArrayList<hr.itrojnar.ezbuild.model.messaging.warehouse.EquipmentHistory> = arrayListOf()
        mEquipmentToLease.forEach {
            leaseEquipment.add(
                EquipmentHistory(
                    it.idEquipment!!,
                    it.quantity
                )
            )
        }
        val args: EditLeasedEquipmentFragmentArgs by navArgs()
        val createEquipmentHistoryRequest = UpdateEquipmentHistoryRequest(
            leaseEquipment, mPreviousResponsiblePerson.idEmployee!!, mResponsiblePerson.idEmployee!!, args.warehouseID, mEquipmentHistory[0].dateEquipmentTaken!!
        )

        (activity as MainActivity).showProgressDialog(getString(R.string.please_wait))

        ezBuildAPIInterface.updateEquipmentHistory(createEquipmentHistoryRequest).enqueue(object:
            Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    (activity as MainActivity).hideProgressDialog()
                    (activity as MainActivity).showSnackBar(getString(R.string.success_edit_lease_equipment), false)
                    if (args.fragment == Constants.FRAGMENT_LEASED_EQUIPMENT_DETAILS) {
                        findNavController().navigate(EditLeasedEquipmentFragmentDirections.actionEditLeasedEquipmentFragmentToLeasedEquipmentDetailsFragment(
                            mResponsiblePerson.idEmployee!!, mEquipmentHistory[0].dateEquipmentTaken!!))
                    }
                    else if (args.fragment == Constants.FRAGMENT_EQUIPMENT_LEASED) {
                        findNavController().navigate(EditLeasedEquipmentFragmentDirections.actionEditLeasedEquipmentFragmentToEquipmentLeasedFragment(args.warehouseID))
                    }
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                (activity as MainActivity).hideProgressDialog()
                (activity as MainActivity).showSnackBar(getString(R.string.erorr_edit_lease_equipment), true)
                Log.e("LEASE ERROR", "Error update lease equipment for employee.")
            }
        })
    }

    private fun loadLeasedEquipmentDetails() {

        val args: EditLeasedEquipmentFragmentArgs by navArgs()
        val leasedEquipmentRequest = LeasedEquipmentDetailsRequest(args.employeeId, args.leaseDate)
        (activity as MainActivity).showProgressDialog(getString(R.string.please_wait))

        ezBuildAPIInterface.getLeasedEquipmentDetails(leasedEquipmentRequest).enqueue(object:
            Callback<EquipmentHistoryForWarehouseResponse> {
            override fun onResponse(
                call: Call<EquipmentHistoryForWarehouseResponse>,
                response: Response<EquipmentHistoryForWarehouseResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    mEquipmentHistory = response.body()!!.equipmentHistory
                    loadPhysicalWorkersForFirm()
                }
            }

            override fun onFailure(call: Call<EquipmentHistoryForWarehouseResponse>, t: Throwable) {
                (activity as MainActivity).hideProgressDialog()
                (activity as MainActivity).showSnackBar(getString(R.string.error_leased_equipment_fetch), true)
                Log.e("LEASED EQUIPMENT ERROR", "Error fetching leased equipment details")
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
                    loadEquipmentForWarehouse()
                }
            }

            override fun onFailure(call: Call<EmployeesForFirmResponse>, t: Throwable) {
                (activity as MainActivity).hideProgressDialog()
                (activity as MainActivity).showSnackBar(getString(R.string.error_api_employees_loading_fail), true)
                Log.e("EQUIPMENT ERROR", "Error fetching employees for firm.")
            }
        })
    }

    private fun loadEquipmentForWarehouse() {

        val args: EditLeasedEquipmentFragmentArgs by navArgs()
        ezBuildAPIInterface.getEquipmentForWarehouse(args.warehouseID).enqueue(object:
            Callback<EquipmentForWarehouseResponse> {
            override fun onResponse(
                call: Call<EquipmentForWarehouseResponse>,
                response: Response<EquipmentForWarehouseResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    mEquipment = response.body()!!.equipment
                    mEquipment.sortBy { it.equipmentName }
                    setupView()
                    (activity as MainActivity).hideProgressDialog()
                }
            }

            override fun onFailure(call: Call<EquipmentForWarehouseResponse>, t: Throwable) {
                (activity as MainActivity).hideProgressDialog()
                (activity as MainActivity).showSnackBar(getString(R.string.erorr_equipment_fetch), true)
                Log.e("EQUIPMENT ERROR", "Error fetching equipment for warehouse.")
            }
        })
    }

    private fun setupView() {

        setupResponsiblePersonCard()
        setupEquipmentLeaseData()
        setupRecyclerView()
    }

    private fun setupResponsiblePersonCard() {

        if (mEquipmentHistory.isNotEmpty()) {
            mPreviousResponsiblePerson = mEquipmentHistory[0].employee
            mResponsiblePerson = mEquipmentHistory[0].employee

            mBinding.tvLeasedEquipmentEmployeeFullName.text = mResponsiblePerson.fullName
            mBinding.tvLeasedEquipmentEmployeeType.text = getString(R.string.type_physical_worker)
            mBinding.tvLeasedEquipmentEmployeePhoneNumber.text = mResponsiblePerson.phone

            mBinding.cwEmployeeLeasedEquipment.visibility = View.VISIBLE
            mBinding.btnAddResponsiblePerson.visibility = View.GONE
        }
    }

    private fun setupEquipmentLeaseData() {
        mEquipmentToLease = arrayListOf()
        mEquipmentHistory.forEach {
            it.equipment.quantity = it.quantityTaken
            mEquipmentToLease.add(it.equipment)
        }
    }

    private fun setupRecyclerView() {

        mBinding.rvLeasedEquipmentForEmployee.layoutManager = LinearLayoutManager(requireContext())
        mLeaseEquipmentAdapter = LeaseEquipmentAdapter(this@EditLeasedEquipmentFragment, mEquipmentToLease)
        mBinding.rvLeasedEquipmentForEmployee.adapter = mLeaseEquipmentAdapter

        if (mEquipmentToLease.isNotEmpty()) {
            mBinding.rvLeasedEquipmentForEmployee.visibility = View.VISIBLE
        } else {
            mBinding.rvLeasedEquipmentForEmployee.visibility = View.GONE
        }
    }

    private fun showEquipmentListDialog() {
        mDialog = Dialog(requireActivity(), R.style.CustomDialog)
        mDialog.setContentView(R.layout.dialog_equipment)
        mDialog.window?.setLayout((resources.displayMetrics.widthPixels * 0.95).toInt(), (resources.displayMetrics.heightPixels * 0.85).toInt())

        val equipmentRecyclerView = mDialog.findViewById<RecyclerView>(R.id.rv_dialog_warehouse_equipment)
        equipmentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val equipmentAdapter = EquipmentAdapter(this@EditLeasedEquipmentFragment, mEquipment)
        equipmentRecyclerView.adapter = equipmentAdapter

        val cancelButton = mDialog.findViewById<EzBuildButton>(R.id.btn_dialog_equipment_cancel)
        cancelButton.setOnClickListener {
            mDialog.dismiss()
        }
        mDialog.show()
    }

    private fun removeResponsiblePerson() {
        mResponsiblePerson = Employee()
        mBinding.cwEmployeeLeasedEquipment.visibility = View.GONE
        mBinding.btnAddResponsiblePerson.visibility = View.VISIBLE
        toggleLeaseButtonVisibility()
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

        val adapter = CustomListEmployeeAdapter(requireActivity(), this@EditLeasedEquipmentFragment, itemsList, Constants.PHYSICAL_WORKER)
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
                    toggleLeaseButtonVisibility()
                }
                else if (mEquipment[indexOfEquipment].quantity > 1) {
                    mEquipment[indexOfEquipment].quantity -= 1
                    mEquipmentToLease[indexOfLeaseEquipment].quantity += 1
                    setupRecyclerView()
                    toggleLeaseButtonVisibility()
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