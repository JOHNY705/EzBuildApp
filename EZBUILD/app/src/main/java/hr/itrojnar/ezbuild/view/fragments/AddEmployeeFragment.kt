package hr.itrojnar.ezbuild.view.fragments

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.DialogCustomListBinding
import hr.itrojnar.ezbuild.databinding.FragmentAddEmployeeBinding
import hr.itrojnar.ezbuild.enums.EmployeeType
import hr.itrojnar.ezbuild.model.messaging.BaseResponse
import hr.itrojnar.ezbuild.model.messaging.employee.CreateEmployeeRequest
import hr.itrojnar.ezbuild.model.network.EzBuildAPIInterface
import hr.itrojnar.ezbuild.model.viewModels.User
import hr.itrojnar.ezbuild.utils.Constants
import hr.itrojnar.ezbuild.view.activities.LoginActivity
import hr.itrojnar.ezbuild.view.activities.MainActivity
import hr.itrojnar.ezbuild.view.adapters.CustomListItemAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class AddEmployeeFragment : Fragment() {

    private lateinit var mBinding : FragmentAddEmployeeBinding

    private lateinit var mSharedPreferences: SharedPreferences
    private val ezBuildAPIInterface = EzBuildAPIInterface.create()
    private lateinit var mFirebaseFirestore : FirebaseFirestore
    private lateinit var mFirebaseAuth: FirebaseAuth

    private lateinit var mCurrentLocale: String

    private lateinit var mCustomListDialog: Dialog
    private var mEmployeeTypeID: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentAddEmployeeBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).useUpButton()

        mSharedPreferences = requireActivity().getSharedPreferences(Constants.EZBUILD_PREFERENCES, Context.MODE_PRIVATE)
        mFirebaseFirestore = FirebaseFirestore.getInstance()
        mFirebaseAuth = FirebaseAuth.getInstance()

        mCurrentLocale = Locale.getDefault().displayLanguage
        setupListeners()
    }

    private fun setupListeners() {
        mBinding.etAddEmployeeType.setOnClickListener {
            if (mCurrentLocale == Constants.LOCALE_CROATIAN) {
                customItemsDialog(getString(R.string.title_select_employee_type), Constants.employeeTypesHR())
            } else {
                customItemsDialog(getString(R.string.title_select_employee_type), Constants.employeeTypesEN())
            }
        }
        mBinding.btnAddEmployee.setOnClickListener {
            createEmployee()
        }
    }

    private fun createEmployee() {
        if (validateEntries()) {

            val fullName = mBinding.etAddEmployeeFullName.text.toString().trim()
            val email = mBinding.etAddEmployeeEmail.text.toString().trim()
            val phoneNumber = mBinding.etAddEmployeePhone.text.toString().trim()
            val password = mBinding.etAddEmployeePassword.text.toString().trim()

            if (mEmployeeTypeID == EmployeeType.PHYSICAL_WORKER.typeID) {

                val createEmployeeRequest = CreateEmployeeRequest(
                    null,
                    fullName,
                    email,
                    phoneNumber,
                    mSharedPreferences.getInt(Constants.USER_FIRM_ID, 0),
                    mEmployeeTypeID
                )

                (activity as MainActivity).showProgressDialog(getString(R.string.please_wait))

                ezBuildAPIInterface.createEmployee(createEmployeeRequest).enqueue(object:
                    Callback<BaseResponse> {
                    override fun onResponse(
                        call: Call<BaseResponse>,
                        response: Response<BaseResponse>
                    ) {
                        if(response.body() != null && response.body()!!.isSuccessful) {

                            (activity as MainActivity).showSnackBar(getString(R.string.employee_creation_successful), false)
                            (activity as MainActivity).hideProgressDialog()
                            findNavController().navigate(AddEmployeeFragmentDirections.actionAddEmployeeFragmentToNavigationEmployees())
                        }
                    }

                    override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                        (activity as LoginActivity).hideProgressDialog()
                        Log.e("CREATE ERROR", "Error creating the user on API.")
                    }
                })
            }
            else {
                (activity as MainActivity).showProgressDialog(getString(R.string.please_wait))

                mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        val firebaseUser: FirebaseUser = task.result!!.user!!

                        val user = User(
                            firebaseUser.uid,
                            email,
                        )

                        mFirebaseFirestore.collection(Constants.USERS)
                            .document(user.id)
                            .set(user, SetOptions.merge())
                            .addOnSuccessListener {

                                val createEmployeeRequest = CreateEmployeeRequest(
                                    user.id,
                                    fullName,
                                    email,
                                    phoneNumber,
                                    mSharedPreferences.getInt(Constants.USER_FIRM_ID, 0),
                                    mEmployeeTypeID
                                )

                                ezBuildAPIInterface.createEmployee(createEmployeeRequest).enqueue(object: Callback<BaseResponse> {
                                    override fun onResponse(
                                        call: Call<BaseResponse>,
                                        response: Response<BaseResponse>
                                    ) {
                                        if(response.body() != null && response.body()!!.isSuccessful) {

                                            (activity as MainActivity).showSnackBar(getString(R.string.employee_creation_successful), false)
                                            (activity as MainActivity).hideProgressDialog()
                                            findNavController().navigate(AddEmployeeFragmentDirections.actionAddEmployeeFragmentToNavigationEmployees())
                                        }
                                    }

                                    override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                                        (activity as MainActivity).hideProgressDialog()
                                        Log.e("REGISTER ERROR", "Error creating the user on API.")
                                    }
                                })
                            }
                            .addOnFailureListener{ e ->
                                (activity as MainActivity).hideProgressDialog()
                                Log.e("REGISTER ERROR", "Error registering the user on Fireabase.", e)
                            }
                    } else {
                        (activity as MainActivity).hideProgressDialog()
                        (activity as MainActivity).showSnackBar(task.exception!!.message.toString(), true)
                    }
                }
            }


        }
    }

    private fun validateEntries() : Boolean {

        return when {
            TextUtils.isEmpty(mBinding.etAddEmployeeFullName.text.toString().trim()) -> {
                (activity as MainActivity).showSnackBar(getString(R.string.err_msg_enter_full_name), true)
                false
            }

            TextUtils.isEmpty(mBinding.etAddEmployeeType.text.toString().trim()) -> {
                (activity as MainActivity).showSnackBar(getString(R.string.err_msg_select_employee_type), true)
                false
            }

            TextUtils.isEmpty(mBinding.etAddEmployeeEmail.text.toString().trim()) && mEmployeeTypeID != EmployeeType.PHYSICAL_WORKER.typeID -> {
                (activity as MainActivity).showSnackBar(getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(mBinding.etAddEmployeePhone.text.toString().trim())-> {
                (activity as MainActivity).showSnackBar(getString(R.string.err_msg_enter_phone), true)
                false
            }

            TextUtils.isEmpty(mBinding.etAddEmployeePassword.text.toString().trim())  && mEmployeeTypeID != EmployeeType.PHYSICAL_WORKER.typeID  -> {
                (activity as MainActivity).showSnackBar(getString(R.string.err_msg_enter_password_add_employee), true)
                false
            }

            TextUtils.isEmpty(mBinding.etAddEmployeeConfirmPassword.text.toString().trim())  && mEmployeeTypeID != EmployeeType.PHYSICAL_WORKER.typeID  -> {
                (activity as MainActivity).showSnackBar(getString(R.string.err_msg_enter_confirm_password), true)
                false
            }

            mBinding.etAddEmployeePassword.text.toString().trim().length < 6  && mEmployeeTypeID != EmployeeType.PHYSICAL_WORKER.typeID-> {
                (activity as MainActivity).showSnackBar(getString(R.string.err_msg_password_must_be_longer), true)
                false
            }

            mBinding.etAddEmployeePassword.text.toString().trim() != mBinding.etAddEmployeeConfirmPassword.text.toString().trim() && mEmployeeTypeID != EmployeeType.PHYSICAL_WORKER.typeID -> {
                (activity as MainActivity).showSnackBar(getString(R.string.err_msg_password_must_match), true)
                false
            }

            else -> true
        }
    }

    private fun customItemsDialog(title: String, itemsList: ArrayList<String>) {

        mCustomListDialog = Dialog(requireContext())
        val binding : DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)

        mCustomListDialog.setContentView(binding.root)
        binding.tvDialogCustomListTitle.text = title

        binding.rvDialogCustomList.layoutManager = LinearLayoutManager(requireContext())

        val adapter = CustomListItemAdapter(requireActivity(), this@AddEmployeeFragment, itemsList, Constants.EMPLOYEE_TYPE)
        binding.rvDialogCustomList.adapter = adapter
        mCustomListDialog.show()
    }

    fun selectedEmployeeType(type: String, position: Int) {

        mCustomListDialog.dismiss()

        if (position == 3) {
            mBinding.tilAddEmployeePassword.visibility = View.GONE
            mBinding.etAddEmployeePassword.setText("")
            mBinding.tilAddEmployeeConfirmPassword.visibility = View.GONE
            mBinding.etAddEmployeeConfirmPassword.setText("")
        } else {
            mBinding.tilAddEmployeePassword.visibility = View.VISIBLE
            mBinding.tilAddEmployeeConfirmPassword.visibility = View.VISIBLE
        }

        when (position) {
            0 -> mEmployeeTypeID = 1
            1 -> mEmployeeTypeID = 2
            2 -> mEmployeeTypeID = 3
            3 -> mEmployeeTypeID = 4
        }

        mBinding.etAddEmployeeType.setText(type)
    }
}