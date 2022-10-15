package hr.itrojnar.ezbuild.view.fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.FragmentChangePasswordBinding
import hr.itrojnar.ezbuild.view.activities.LoginActivity

class ChangePasswordFragment : Fragment() {

    private lateinit var mBinding: FragmentChangePasswordBinding

    private lateinit var mFirebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFirebaseAuth = FirebaseAuth.getInstance()
        changePassword()
    }

    private fun changePassword() {
        mBinding.btnChangePasswordConfirm.setOnClickListener {
            val email: String = mBinding.etChangePasswordEmail.text.toString().trim()
            if (validateEmail()) {
                (activity as LoginActivity).showProgressDialog(resources.getString(R.string.please_wait))
                mFirebaseAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        (activity as LoginActivity).hideProgressDialog()

                        if (task.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.lbl_email_to_reset_password_sent),
                                Toast.LENGTH_LONG
                            ).show()
                            mBinding.etChangePasswordEmail.setText("")
                        }
                        else {
                            (activity as LoginActivity).showSnackBar(task.exception!!.message.toString(), true)
                            (activity as LoginActivity).hideProgressDialog()
                        }
                    }
            }
        }
    }

    private fun validateEmail(): Boolean {

        return when  {
            TextUtils.isEmpty(mBinding.etChangePasswordEmail.text.toString().trim()) || !Patterns.EMAIL_ADDRESS.matcher(mBinding.etChangePasswordEmail.text.toString().trim()).matches() -> {
                (activity as LoginActivity).showSnackBar(getString(R.string.err_msg_enter_login_email), true)
                false
            }

            else -> true
        }
    }
}