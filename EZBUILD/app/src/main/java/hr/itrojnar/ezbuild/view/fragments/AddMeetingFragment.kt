package hr.itrojnar.ezbuild.view.fragments

import android.app.DatePickerDialog
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
import android.widget.TimePicker
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.FragmentAddMeetingBinding
import hr.itrojnar.ezbuild.model.messaging.BaseResponse
import hr.itrojnar.ezbuild.model.messaging.meeting.CreateMeetingRequest
import hr.itrojnar.ezbuild.model.network.EzBuildAPIInterface
import hr.itrojnar.ezbuild.utils.Constants
import hr.itrojnar.ezbuild.utils.EzBuildButton
import hr.itrojnar.ezbuild.view.activities.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class AddMeetingFragment : Fragment() {

    private lateinit var mBinding: FragmentAddMeetingBinding

    private lateinit var mPreferences: SharedPreferences
    private lateinit var mFormattedSelectedDate: String
    private var mCalendar = Calendar.getInstance()
    private lateinit var mDateSetListener: DatePickerDialog.OnDateSetListener

    private val ezBuildAPIInterface = EzBuildAPIInterface.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDateSetListener = DatePickerDialog.OnDateSetListener {
                _, year, month, dayOfMonth ->
            mCalendar.set(Calendar.YEAR, year)
            mCalendar.set(Calendar.MONTH, month)
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDiaryEntryDate()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAddMeetingBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).useUpButton()
        mPreferences = requireActivity().getSharedPreferences(Constants.EZBUILD_PREFERENCES, Context.MODE_PRIVATE)
        setupListeners()
    }

    private fun setupListeners() {

        mBinding.etAddMeetingDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                mDateSetListener,
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH))
                .show()
        }

        mBinding.etAddMeetingStartTime.setOnClickListener {
            val timePickerDialog = Dialog(requireActivity(), R.style.CustomDialog)
            timePickerDialog.setContentView(R.layout.dialog_time_picker)
            val timePicker = timePickerDialog.findViewById<TimePicker>(R.id.time_picker)
            timePickerDialog.window?.setLayout((resources.displayMetrics.widthPixels * 0.85).toInt(), (resources.displayMetrics.heightPixels * 0.85).toInt())
            timePicker.setIs24HourView(true)
            val timePickerCancelButton = timePickerDialog.findViewById<EzBuildButton>(R.id.btn_time_picker_cancel)
            timePickerCancelButton.setOnClickListener {
                timePickerDialog.dismiss()
            }
            val timePickerConfirmButton = timePickerDialog.findViewById<EzBuildButton>(R.id.btn_time_picker_confirm)
            timePickerConfirmButton.setOnClickListener {
                if (timePicker.minute < 10) {
                    val selectedTime = "${timePicker.hour}:0${timePicker.minute}"
                    mBinding.etAddMeetingStartTime.setText(selectedTime)
                } else {
                    val selectedTime = "${timePicker.hour}:${timePicker.minute}"
                    mBinding.etAddMeetingStartTime.setText(selectedTime)
                }
                timePickerDialog.dismiss()
            }
            timePickerDialog.show()
        }

        mBinding.btnAddMeeting.setOnClickListener {
            validateEntriesAndSave()
        }
    }

    private fun validateEntriesAndSave() {

        val meetingTitle = mBinding.etAddMeetingTitle.text.toString().trim()
        val meetingDate = mBinding.etAddMeetingDate.text.toString().trim()
        val meetingStartTime = mBinding.etAddMeetingStartTime.text.toString().trim()
        val meetingDuration = mBinding.etAddMeetingDuration.text.toString().trim()
        val meetingDescription = mBinding.etAddMeetingDescription.text.toString().trim()

        when {
            TextUtils.isEmpty(meetingTitle) -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_enter_meeting_title),
                    Toast.LENGTH_SHORT
                ).show()
            }
            TextUtils.isEmpty(meetingDate) -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_select_meeting_date),
                    Toast.LENGTH_SHORT
                ).show()
            }
            TextUtils.isEmpty(meetingStartTime) -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_select_meeting_start_time),
                    Toast.LENGTH_SHORT
                ).show()
            }
            TextUtils.isEmpty(meetingDuration) -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_select_meeting_duration),
                    Toast.LENGTH_SHORT
                ).show()
            }
            TextUtils.isEmpty(meetingDescription) -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_select_meeting_description),
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                val meetingRequest = CreateMeetingRequest(
                    meetingTitle,
                    mFormattedSelectedDate,
                    meetingStartTime,
                    meetingDuration,
                    meetingDescription,
                    mPreferences.getInt(Constants.USER_API_ID, 0)
                )
                uploadMeeting(meetingRequest)
            }
        }
    }

    private fun uploadMeeting(meetingRequest: CreateMeetingRequest) {

        (activity as MainActivity).showProgressDialog(getString(R.string.please_wait))

        ezBuildAPIInterface.createMeeting(meetingRequest).enqueue(object:
            Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    (activity as MainActivity).hideProgressDialog()
                    (activity as MainActivity).showSnackBar(getString(R.string.add_meeting_success), false)
                    findNavController().navigate(AddMeetingFragmentDirections.actionAddMeetingFragmentToNavigationMeetings())
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                (activity as MainActivity).hideProgressDialog()
                (activity as MainActivity).showSnackBar(getString(R.string.create_meeting_error), true)
                Log.e("MEETING ERROR", "Error creating meeting.")
            }
        })
    }

    private fun updateDiaryEntryDate() {
        val format = "dd.MM.yyyy."
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        mBinding.etAddMeetingDate.setText(sdf.format(mCalendar.time).toString())

        val apiFormat = "yyyy-MM-dd'T'HH:mm:ss"
        val apiSdf = SimpleDateFormat(apiFormat, Locale.getDefault())
        mFormattedSelectedDate = apiSdf.format(mCalendar.time)
    }
}