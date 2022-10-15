package hr.itrojnar.ezbuild.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.FragmentLeasedEquipmentDetailsBinding
import hr.itrojnar.ezbuild.model.messaging.BaseResponse
import hr.itrojnar.ezbuild.model.messaging.warehouse.DeleteEquipmentHistoryRequest
import hr.itrojnar.ezbuild.model.messaging.warehouse.EquipmentHistoryForWarehouseResponse
import hr.itrojnar.ezbuild.model.messaging.warehouse.LeasedEquipmentDetailsRequest
import hr.itrojnar.ezbuild.model.network.EzBuildAPIInterface
import hr.itrojnar.ezbuild.model.viewModels.Employee
import hr.itrojnar.ezbuild.model.viewModels.Equipment
import hr.itrojnar.ezbuild.model.viewModels.EquipmentHistory
import hr.itrojnar.ezbuild.utils.Constants
import hr.itrojnar.ezbuild.view.activities.MainActivity
import hr.itrojnar.ezbuild.view.adapters.EquipmentAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
import kotlin.collections.ArrayList

class LeasedEquipmentDetailsFragment : Fragment() {

    private lateinit var mBinding: FragmentLeasedEquipmentDetailsBinding
    private var mEquipmentHistory: ArrayList<EquipmentHistory> = arrayListOf()
    private lateinit var mEquipmentAdapter: EquipmentAdapter

    private val ezBuildAPIInterface = EzBuildAPIInterface.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentLeasedEquipmentDetailsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).useUpButton()
        loadLeasedEquipmentDetails()
    }

    private fun loadLeasedEquipmentDetails() {

        val args: LeasedEquipmentDetailsFragmentArgs by navArgs()
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
                    setupView()
                    (activity as MainActivity).hideProgressDialog()
                }
            }

            override fun onFailure(call: Call<EquipmentHistoryForWarehouseResponse>, t: Throwable) {
                (activity as MainActivity).hideProgressDialog()
                (activity as MainActivity).showSnackBar(getString(R.string.error_leased_equipment_fetch), true)
                Log.e("LEASED EQUIPMENT ERROR", "Error fetching leased equipment details")
            }
        })
    }

    private fun setupView() {

        mBinding.tvLeasedEquipmentEmployeeFullName.text = mEquipmentHistory[0].employee.fullName
        mBinding.tvLeasedEquipmentEmployeeType.text = getString(R.string.type_physical_worker)
        mBinding.tvLeasedEquipmentEmployeePhoneNumber.text = mEquipmentHistory[0].employee.phone

        val apiFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.")
        val formattedDate = LocalDate.parse(mEquipmentHistory[0].dateEquipmentTaken, apiFormatter)
        val dayAndFormattedDate = formattedDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.titlecase() } + ", " + formattedDate.format(formatter)

        mBinding.tvEquipmentLeasedDate.text = dayAndFormattedDate

        val equipment = arrayListOf<Equipment>()
        mEquipmentHistory.forEach {
            it.equipment.quantity = it.quantityTaken
            equipment.add(it.equipment)
        }

        mBinding.rvLeasedEquipmentForEmployee.layoutManager = LinearLayoutManager(requireContext())
        mEquipmentAdapter = EquipmentAdapter(this@LeasedEquipmentDetailsFragment, equipment)
        mBinding.rvLeasedEquipmentForEmployee.adapter = mEquipmentAdapter

        mBinding.rvLeasedEquipmentForEmployee.visibility = View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_leased_equipment_details, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_leased_equipment_details_edit -> {
                if (mEquipmentHistory.isNotEmpty()) {
                    findNavController().navigate(LeasedEquipmentDetailsFragmentDirections.actionLeasedEquipmentDetailsFragmentToEditLeasedEquipmentFragment(
                        mEquipmentHistory[0].employee.idEmployee!!, mEquipmentHistory[0].dateEquipmentTaken!!, mEquipmentHistory[0].warehouseID!!, Constants.FRAGMENT_LEASED_EQUIPMENT_DETAILS
                    ))
                }
            }
            R.id.action_leased_equipment_details_delete -> {
                val builder = AlertDialog.Builder(requireActivity(), R.style.AlertDialogTheme)
                builder.setTitle(resources.getString(R.string.title_delete_leased_equipment))
                builder.setMessage(resources.getString(R.string.msg_delete_leased_equipment))
                builder.setIcon(resources.getDrawable(R.drawable.ic_warning_24))
                builder.setPositiveButton(resources.getString(R.string.btn_yes)) { dialogInterface, _ ->

                    dialogInterface.dismiss()

                    (activity as MainActivity).showProgressDialog(resources.getString(R.string.please_wait))

                    val deleteEquipmentHistoryRequest = DeleteEquipmentHistoryRequest(mEquipmentHistory[0].employee.idEmployee!!, mEquipmentHistory[0].dateEquipmentTaken!!)

                    ezBuildAPIInterface.deleteEquipmentHistory(deleteEquipmentHistoryRequest).enqueue(object:
                        Callback<BaseResponse> {
                        override fun onResponse(
                            call: Call<BaseResponse>,
                            response: Response<BaseResponse>
                        ) {
                            if(response.body() != null && response.body()!!.isSuccessful) {
                                (activity as MainActivity).hideProgressDialog()
                                (activity as MainActivity).showSnackBar(getString(R.string.success_delete_equipment_history), false)
                                findNavController().navigate(LeasedEquipmentDetailsFragmentDirections.actionLeasedEquipmentDetailsFragmentToEquipmentLeasedFragment(mEquipmentHistory[0].warehouseID!!))
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
                alertDialog.show()            }
        }
        return super.onOptionsItemSelected(item)
    }
}