package hr.itrojnar.ezbuild.view.fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.FragmentEditEmployeeBinding
import hr.itrojnar.ezbuild.enums.EmployeeType
import hr.itrojnar.ezbuild.model.messaging.BaseResponse
import hr.itrojnar.ezbuild.model.messaging.constructionSite.ConstructionSitesResponse
import hr.itrojnar.ezbuild.model.messaging.employee.UpdateEmployeeRequest
import hr.itrojnar.ezbuild.model.network.EzBuildAPIInterface
import hr.itrojnar.ezbuild.model.viewModels.Employee
import hr.itrojnar.ezbuild.utils.Constants
import hr.itrojnar.ezbuild.view.activities.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditEmployeeFragment : Fragment() {

    private lateinit var mBinding: FragmentEditEmployeeBinding
    private lateinit var mEmployee: Employee

    private val ezBuildAPIInterface = EzBuildAPIInterface.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentEditEmployeeBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).useUpButton()
        setupView()
    }

    private fun setupView() {
        val args: EditEmployeeFragmentArgs by navArgs()
        mEmployee = args.employee

        mBinding.etEditEmployeeFullName.setText(mEmployee.fullName)

        when (mEmployee.employeeTypeID) {
            EmployeeType.ENGINEER.typeID -> mBinding.tvEditEmployeeType.text = getString(R.string.type_engineer)
            EmployeeType.PHYSICAL_WORKER.typeID -> mBinding.tvEditEmployeeType.text = getString(R.string.type_physical_worker)
            EmployeeType.WAREHOUSE_MANAGER.typeID -> mBinding.tvEditEmployeeType.text = getString(R.string.type_warehouse_manager)
        }

        if (mEmployee.email!!.isNotEmpty()) {
            mBinding.tvEditEmployeeEmail.text = mEmployee.email
        }

        mBinding.etEditEmployeePhone.setText(mEmployee.phone)

        mBinding.btnEditEmployee.setOnClickListener {
            editEmployee()
        }
    }

    private fun editEmployee() {
        if (validateEntries()) {

            val args: EditEmployeeFragmentArgs by navArgs()

            val updateEmployeeRequest = UpdateEmployeeRequest(
                mEmployee.idEmployee!!,
                mBinding.etEditEmployeeFullName.text.toString().trim(),
                mBinding.etEditEmployeePhone.text.toString().trim()
            )

            (activity as MainActivity).showProgressDialog(getString(R.string.please_wait))

            ezBuildAPIInterface.updateEmployee(updateEmployeeRequest).enqueue(object:
                Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if(response.body() != null && response.body()!!.isSuccessful) {

                        (activity as MainActivity).hideProgressDialog()
                        (activity as MainActivity).showSnackBar(getString(R.string.success_api_update_employee), false)
                        when (args.fragment) {
                            Constants.FRAGMENT_EMPLOYEES -> {
                                findNavController().navigate(EditEmployeeFragmentDirections.actionEditEmployeeFragmentToNavigationEmployees())
                            }
                            Constants.FRAGMENT_EMPLOYEE_DETAILS -> {
                                mEmployee.fullName = updateEmployeeRequest.fullName
                                mEmployee.phone = updateEmployeeRequest.phone
                                findNavController().navigate(EditEmployeeFragmentDirections.actionEditEmployeeFragmentToEmployeeDetailsFragment(mEmployee, args.warehouse, args.constructionSites))
                            }
                        }
                    }
                }
                override fun onFailure(
                    call: Call<BaseResponse>,
                    t: Throwable
                ) {
                    (activity as MainActivity).hideProgressDialog()
                    (activity as MainActivity).showSnackBar(getString(R.string.error_api_update_employee), true)
                    Log.e(requireActivity().javaClass.simpleName, "Error while updating profile.", t)
                }
            })
        }
    }

    private fun validateEntries() : Boolean {

        return when {
            TextUtils.isEmpty(mBinding.etEditEmployeeFullName.text.toString().trim()) -> {
                (activity as MainActivity).showSnackBar(
                    getString(R.string.err_msg_enter_full_name),
                    true
                )
                false
            }
            TextUtils.isEmpty(mBinding.etEditEmployeePhone.text.toString().trim()) -> {
                (activity as MainActivity).showSnackBar(
                    getString(R.string.err_msg_enter_phone),
                    true
                )
                false
            }
            else -> true
        }
    }
}