package hr.itrojnar.ezbuild.view.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.FragmentLoginBinding
import hr.itrojnar.ezbuild.model.messaging.employee.RegisteredEmployeeDetailsResponse
import hr.itrojnar.ezbuild.model.network.EzBuildAPIInterface
import hr.itrojnar.ezbuild.utils.Constants
import hr.itrojnar.ezbuild.view.activities.LoginActivity
import hr.itrojnar.ezbuild.view.activities.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    private lateinit var mBinding: FragmentLoginBinding

    private lateinit var mFirebaseFirestore : FirebaseFirestore
    private lateinit var mFirebaseAuth: FirebaseAuth

    private val ezBuildAPIInterface = EzBuildAPIInterface.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentLoginBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFirebaseFirestore = FirebaseFirestore.getInstance()
        mFirebaseAuth = FirebaseAuth.getInstance()
        login()
    }

    private fun login() {
        mBinding.btnLoginLogin.setOnClickListener {
            if (validateLoginDetails()) {

                (activity as LoginActivity).showProgressDialog(resources.getString(R.string.please_wait))

                mFirebaseAuth.signInWithEmailAndPassword(mBinding.etLoginEmail.text.toString().trim(), mBinding.etLoginPassword.text.toString().trim())
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {

                            mFirebaseFirestore.collection(Constants.USERS)
                                .document(mFirebaseAuth.currentUser!!.uid)
                                .get()
                                .addOnSuccessListener {

                                    ezBuildAPIInterface.getRegisteredEmployeeDetails(mFirebaseAuth.currentUser!!.uid).enqueue(object: Callback<RegisteredEmployeeDetailsResponse> {
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

                                        override fun onFailure(
                                            call: Call<RegisteredEmployeeDetailsResponse>,
                                            t: Throwable
                                        ) {
                                            (activity as LoginActivity).hideProgressDialog()
                                            (activity as LoginActivity).showSnackBar(getString(R.string.error_api_login), true)
                                            //Log.e(requireActivity().javaClass.simpleName, "Error while getting user details from API.", t)
                                        }
                                    })
                                }
                                .addOnFailureListener { e ->
                                    (activity as LoginActivity).hideProgressDialog()
                                    (activity as LoginActivity).showSnackBar(getString(R.string.error_api_login), true)
                                    //joLog.e(requireActivity().javaClass.simpleName, "Error while getting user details.", e)
                                }
                        }
                        else {
                            (activity as LoginActivity).hideProgressDialog()
                            (activity as LoginActivity).showSnackBar(task.exception!!.message.toString(), true)
                        }
                    }
            }
        }
    }
    private fun validateLoginDetails(): Boolean {

        return when  {
            TextUtils.isEmpty(mBinding.etLoginEmail.text.toString().trim()) || !Patterns.EMAIL_ADDRESS.matcher(mBinding.etLoginEmail.text.toString().trim()).matches() -> {
                (activity as LoginActivity).showSnackBar(getString(R.string.err_msg_enter_login_email), true)
                false
            }

            mBinding.etLoginPassword.text.toString().trim().length < 6 -> {
                (activity as LoginActivity).showSnackBar(getString(R.string.err_msg_enter_valid_password), true)
                false
            }
            else -> true
        }
    }
}