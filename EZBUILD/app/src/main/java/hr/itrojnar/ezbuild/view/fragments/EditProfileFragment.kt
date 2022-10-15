package hr.itrojnar.ezbuild.view.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.FragmentEditProfileBinding
import hr.itrojnar.ezbuild.enums.EmployeeType
import hr.itrojnar.ezbuild.model.messaging.BaseResponse
import hr.itrojnar.ezbuild.model.messaging.employee.UpdateEmployeeRequest
import hr.itrojnar.ezbuild.model.network.EzBuildAPIInterface
import hr.itrojnar.ezbuild.utils.Constants
import hr.itrojnar.ezbuild.view.activities.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileFragment : Fragment() {

    private lateinit var mBinding: FragmentEditProfileBinding

    private lateinit var mPreferences: SharedPreferences

    private val ezBuildAPIInterface = EzBuildAPIInterface.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPreferences = requireActivity().getSharedPreferences(Constants.EZBUILD_PREFERENCES, Context.MODE_PRIVATE)
        (activity as MainActivity).useUpButton()
        setupView()
    }

    private fun setupView() {
        val fullName = mPreferences.getString(Constants.USER_FULL_NAME, "")
        val email = mPreferences.getString(Constants.USER_EMAIL, "")
        val phoneNumber = mPreferences.getString(Constants.USER_PHONE_NUMBER, "")

        mBinding.etEditProfileFullName.setText(fullName)
        mBinding.tvProfileEmail.text = email

        when (mPreferences.getInt(Constants.USER_TYPE_ID, 0)) {
            EmployeeType.DIRECTOR.typeID -> mBinding.tvProfileEmployeeType.text = getString(R.string.type_director)
            EmployeeType.ENGINEER.typeID -> mBinding.tvProfileEmployeeType.text = getString(R.string.type_engineer)
            EmployeeType.PHYSICAL_WORKER.typeID -> mBinding.tvProfileEmployeeType.text = getString(R.string.type_physical_worker)
            EmployeeType.WAREHOUSE_MANAGER.typeID -> mBinding.tvProfileEmployeeType.text = getString(
                R.string.type_warehouse_manager)
        }

        if (!phoneNumber.isNullOrEmpty()) {
            mBinding.etEditProfilePhone.setText(phoneNumber)
        }

        mBinding.btnProfileSaveChanges.setOnClickListener {
            if (validateEntries()) {

                (activity as MainActivity).showProgressDialog(getString(R.string.please_wait))

                val updateEmployeeRequest = UpdateEmployeeRequest(
                    mPreferences.getInt(Constants.USER_API_ID, 0),
                    mBinding.etEditProfileFullName.text.toString().trim(),
                    mBinding.etEditProfilePhone.text.toString().trim()
                )

                ezBuildAPIInterface.updateEmployee(updateEmployeeRequest).enqueue(object:
                    Callback<BaseResponse> {
                    override fun onResponse(
                        call: Call<BaseResponse>,
                        response: Response<BaseResponse>
                    ) {
                        if(response.body() != null && response.body()!!.isSuccessful) {

                            (activity as MainActivity).hideProgressDialog()
                            (activity as MainActivity).showSnackBar(getString(R.string.success_api_update_profile), false)
                            val editor = mPreferences.edit()
                            editor.putString(Constants.USER_FULL_NAME, updateEmployeeRequest.fullName)
                            editor.putString(Constants.USER_PHONE_NUMBER, updateEmployeeRequest.phone)
                            editor.apply()
                            (activity as MainActivity).updateNavHeaderView()
                            findNavController().navigate(EditProfileFragmentDirections.actionEditProfileFragmentToProfileFragment())
                        }
                    }
                    override fun onFailure(
                        call: Call<BaseResponse>,
                        t: Throwable
                    ) {
                        (activity as MainActivity).hideProgressDialog()
                        (activity as MainActivity).showSnackBar(getString(R.string.error_api_update_profile), true)
                        Log.e(requireActivity().javaClass.simpleName, "Error while updating profile.", t)
                    }
                })
            }
        }
    }

    private fun validateEntries(): Boolean {
        return when {
            TextUtils.isEmpty(mBinding.etEditProfileFullName.text.toString().trim()) -> {
                (activity as MainActivity).showSnackBar(getString(R.string.err_msg_enter_full_name), true)
                false
            }
            TextUtils.isEmpty(mBinding.etEditProfilePhone.text.toString().trim()) -> {
                (activity as MainActivity).showSnackBar(getString(R.string.err_msg_enter_phone), true)
                false
            }
            else -> true
        }
    }
}