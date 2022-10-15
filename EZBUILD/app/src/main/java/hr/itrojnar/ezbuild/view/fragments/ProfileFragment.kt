package hr.itrojnar.ezbuild.view.fragments

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import androidx.fragment.app.Fragment
import androidx.annotation.NonNull
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.DialogChangePasswordBinding
import hr.itrojnar.ezbuild.databinding.FragmentProfileBinding
import hr.itrojnar.ezbuild.enums.EmployeeType
import hr.itrojnar.ezbuild.utils.Constants
import hr.itrojnar.ezbuild.view.activities.LoginActivity
import hr.itrojnar.ezbuild.view.activities.MainActivity

class ProfileFragment : Fragment() {

    private lateinit var mBinding: FragmentProfileBinding

    private lateinit var mPreferences: SharedPreferences

    private lateinit var mFirebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentProfileBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPreferences = requireActivity().getSharedPreferences(Constants.EZBUILD_PREFERENCES, Context.MODE_PRIVATE)
        (activity as MainActivity).useUpButton()
        (activity as MainActivity).hideBottomNavigationView()
        mFirebaseAuth = FirebaseAuth.getInstance()
        setupView()
    }

    private fun setupView() {

        val fullName = mPreferences.getString(Constants.USER_FULL_NAME, "")
        val email = mPreferences.getString(Constants.USER_EMAIL, "")
        val phoneNumber = mPreferences.getString(Constants.USER_PHONE_NUMBER, "")

        mBinding.tvProfileFullName.text = fullName
        mBinding.tvProfileEmail.text = email

        when (mPreferences.getInt(Constants.USER_TYPE_ID, 0)) {
            EmployeeType.DIRECTOR.typeID -> mBinding.tvProfileEmployeeType.text = getString(R.string.type_director)
            EmployeeType.ENGINEER.typeID -> mBinding.tvProfileEmployeeType.text = getString(R.string.type_engineer)
            EmployeeType.PHYSICAL_WORKER.typeID -> mBinding.tvProfileEmployeeType.text = getString(R.string.type_physical_worker)
            EmployeeType.WAREHOUSE_MANAGER.typeID -> mBinding.tvProfileEmployeeType.text = getString(R.string.type_warehouse_manager)
        }

        if (!phoneNumber.isNullOrEmpty()) {
            mBinding.tvProfilePhoneNumber.text = phoneNumber
        }

        mBinding.btnProfileChangePassword.setOnClickListener {
            showPasswordChangeDialog()
        }
    }

    private fun showPasswordChangeDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val binding = DialogChangePasswordBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.setContentView(binding.root)
        dialog.window?.setLayout((resources.displayMetrics.widthPixels * 0.9).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)

        val email = mPreferences.getString(Constants.USER_EMAIL, "")

        binding.btnDialogChangePasswordConfirm.setOnClickListener {
            val user = mFirebaseAuth.currentUser

            if (validateEntries(binding)) {

                val currentPassword = binding.etCurrentPassword.text.toString().trim()
                val newPassword = binding.etNewPassword.text.toString().trim()

                email?.let {

                    (activity as MainActivity).showProgressDialog(getString(R.string.please_wait))

                    val credential = EmailAuthProvider.getCredential(email, currentPassword)

                    user?.reauthenticate(credential)?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            user.updatePassword(newPassword).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    (activity as MainActivity).showSnackBar(getString(R.string.success_password_changed), false)
                                } else {
                                    (activity as MainActivity).showSnackBar(getString(R.string.error_password_change), true)
                                }
                                dialog.dismiss()
                                (activity as MainActivity).hideProgressDialog()
                            }
                        } else {
                            (activity as MainActivity).showSnackBar(getString(R.string.error_wrong_password), true)
                            dialog.dismiss()
                            (activity as MainActivity).hideProgressDialog()
                        }
                    }
                }
            }
        }

        binding.btnDialogChangePasswordCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun validateEntries(binding:  DialogChangePasswordBinding): Boolean {
        return when {
            TextUtils.isEmpty(binding.etCurrentPassword.text.toString().trim()) -> {
                (activity as MainActivity).showSnackBar(getString(R.string.err_msg_enter_current_password), true)
                false
            }

            TextUtils.isEmpty(binding.etNewPassword.text.toString().trim()) -> {
                (activity as MainActivity).showSnackBar(getString(R.string.err_msg_enter_new_password), true)
                false
            }

            TextUtils.isEmpty(binding.etNewPassword.text.toString().trim()) -> {
                (activity as MainActivity).showSnackBar(getString(R.string.err_msg_enter_new_confirm_password), true)
                false
            }

            binding.etNewPassword.text.toString().trim().length < 6 -> {
                (activity as MainActivity).showSnackBar(getString(R.string.err_msg_password_must_be_longer), true)
                false
            }

            binding.etNewPassword.text.toString().trim() != binding.etConfirmNewPassword.text.toString().trim() -> {
                (activity as MainActivity).showSnackBar(getString(R.string.err_msg_password_must_match), true)
                false
            }

            binding.etNewPassword.text.toString().trim() == binding.etCurrentPassword.text.toString().trim() -> {
                (activity as MainActivity).showSnackBar(getString(R.string.error_current_and_new_password_cannot_match), true)
                false
            }

            else -> true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_profile, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_profile_edit -> {
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment())
            }
        }
        return super.onOptionsItemSelected(item)
    }

}