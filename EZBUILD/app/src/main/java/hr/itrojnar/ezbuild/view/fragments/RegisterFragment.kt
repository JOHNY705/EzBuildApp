package hr.itrojnar.ezbuild.view.fragments

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.FragmentRegisterBinding
import hr.itrojnar.ezbuild.model.messaging.employee.RegisterFirmAndUserRequest
import hr.itrojnar.ezbuild.model.messaging.employee.RegisteredEmployeeDetailsResponse
import hr.itrojnar.ezbuild.model.viewModels.User
import hr.itrojnar.ezbuild.model.network.EzBuildAPIInterface
import hr.itrojnar.ezbuild.view.activities.LoginActivity
import hr.itrojnar.ezbuild.utils.Constants
import hr.itrojnar.ezbuild.view.activities.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterFragment : Fragment() {

    private lateinit var mBinding: FragmentRegisterBinding

    private lateinit var mFirebaseFirestore : FirebaseFirestore
    private lateinit var mFirebaseAuth: FirebaseAuth

    private val ezBuildAPIInterface = EzBuildAPIInterface.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentRegisterBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFirebaseFirestore = FirebaseFirestore.getInstance()
        mFirebaseAuth = FirebaseAuth.getInstance()
        register()
    }

    private fun register() {
        mBinding.btnRegisterRegister.setOnClickListener {
            if (validateRegisterDetails()) {

                (activity as LoginActivity).showProgressDialog(resources.getString(R.string.please_wait))

                mFirebaseAuth.createUserWithEmailAndPassword(
                    mBinding.etRegisterEmail.text.toString(),
                    mBinding.etRegisterPassword.text.toString()
                ).addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        val firebaseUser: FirebaseUser = task.result!!.user!!

                        val user = User(
                            firebaseUser.uid,
                            mBinding.etRegisterEmail.text.toString().trim(),
                        )

                        mFirebaseFirestore.collection(Constants.USERS)
                            .document(user.id)
                            .set(user, SetOptions.merge())
                            .addOnSuccessListener {

                                val userAndFirmDetails = RegisterFirmAndUserRequest(
                                    mBinding.etRegisterFullName.text.toString().trim(),
                                    user.id,
                                    user.email, mBinding.etRegisterCompanyName.text.toString().trim()
                                )

                                ezBuildAPIInterface.registerFirmAndUser(userAndFirmDetails).enqueue(object: Callback<RegisteredEmployeeDetailsResponse> {
                                    override fun onResponse(
                                        call: Call<RegisteredEmployeeDetailsResponse>,
                                        response: Response<RegisteredEmployeeDetailsResponse>
                                    ) {
                                        if(response.body() != null && response.body()!!.isSuccessful) {

                                            val employee = response.body()!!.employee

                                            val sharedPreferences = requireActivity().getSharedPreferences(Constants.EZBUILD_PREFERENCES, Context.MODE_PRIVATE)

                                            val editor: SharedPreferences.Editor = sharedPreferences.edit()

                                            editor.putInt(Constants.USER_API_ID, employee.idEmployee!!)
                                            editor.putString(Constants.USER_FIREBASE_ID, employee.firebaseID)
                                            editor.putInt(Constants.USER_FIRM_ID, employee.firmID!!)
                                            editor.putInt(Constants.USER_TYPE_ID, employee.employeeTypeID!!)
                                            editor.putString(Constants.USER_EMAIL, employee.email)
                                            editor.putString(Constants.USER_FULL_NAME, employee.fullName)
                                            editor.putString(Constants.USER_PHONE_NUMBER, employee.phone)

                                            editor.apply()

                                            (activity as LoginActivity).hideProgressDialog()

                                            startActivity(Intent(activity, MainActivity::class.java))
                                        }
                                    }

                                    override fun onFailure(call: Call<RegisteredEmployeeDetailsResponse>, t: Throwable) {
                                        (activity as LoginActivity).hideProgressDialog()
                                        Log.e("REGISTER ERROR", "Error registering the user on API.")
                                    }
                                })
                            }
                            .addOnFailureListener{ e ->
                                (activity as LoginActivity).hideProgressDialog()
                                Log.e("REGISTER ERROR", "Error registering the user on Fireabase.", e)
                            }
                    } else {
                        (activity as LoginActivity).hideProgressDialog()
                        (activity as LoginActivity).showSnackBar(task.exception!!.message.toString(), true)
                    }
                }
            }
        }
    }

    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(mBinding.etRegisterFullName.text.toString().trim()) -> {
                (activity as LoginActivity).showSnackBar(getString(R.string.err_msg_enter_full_name), true)
                false
            }

            TextUtils.isEmpty(mBinding.etRegisterCompanyName.text.toString().trim()) -> {
                (activity as LoginActivity).showSnackBar(getString(R.string.err_msg_enter_company_name), true)
                false
            }

            TextUtils.isEmpty(mBinding.etRegisterEmail.text.toString().trim()) -> {
                (activity as LoginActivity).showSnackBar(getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(mBinding.etRegisterPassword.text.toString().trim()) -> {
                (activity as LoginActivity).showSnackBar(getString(R.string.err_msg_enter_password), true)
                false
            }

            TextUtils.isEmpty(mBinding.etRegisterConfirmPassword.text.toString().trim()) -> {
                (activity as LoginActivity).showSnackBar(getString(R.string.err_msg_enter_confirm_password), true)
                false
            }

            mBinding.etRegisterPassword.text.toString().trim().length < 6 -> {
                (activity as LoginActivity).showSnackBar(getString(R.string.err_msg_password_must_be_longer), true)
                false
            }

            mBinding.etRegisterPassword.text.toString().trim() != mBinding.etRegisterConfirmPassword.text.toString().trim() -> {
                (activity as LoginActivity).showSnackBar(getString(R.string.err_msg_password_must_match), true)
                false
            }

            else -> {
                //(activity as LoginActivity).showErrorSnackBar(getString(R.string.register_successful), false)
                true
            }
        }
    }
}