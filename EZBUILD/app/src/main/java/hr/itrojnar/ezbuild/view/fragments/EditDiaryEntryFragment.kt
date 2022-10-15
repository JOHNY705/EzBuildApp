package hr.itrojnar.ezbuild.view.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.FragmentEditDiaryEntryBinding
import hr.itrojnar.ezbuild.model.messaging.BaseResponse
import hr.itrojnar.ezbuild.model.messaging.constructionSite.GetConstructionSiteResponse
import hr.itrojnar.ezbuild.model.messaging.diaryEntry.UpdateDiaryEntryRequest
import hr.itrojnar.ezbuild.model.viewModels.ConstructionSiteDiaryEntry
import hr.itrojnar.ezbuild.model.network.EzBuildAPIInterface
import hr.itrojnar.ezbuild.utils.Constants
import hr.itrojnar.ezbuild.view.activities.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class EditDiaryEntryFragment : Fragment() {

    private lateinit var mBinding: FragmentEditDiaryEntryBinding
    private lateinit var mDiaryEntry: ConstructionSiteDiaryEntry

    private lateinit var mFormattedSelectedDate: String
    private var mCalendar = Calendar.getInstance()
    private lateinit var mDateSetListener: DatePickerDialog.OnDateSetListener

    private lateinit var mPreferences: SharedPreferences
    private val ezBuildAPIInterface = EzBuildAPIInterface.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        mDateSetListener = DatePickerDialog.OnDateSetListener {
                view, year, month, dayOfMonth ->
            mCalendar.set(Calendar.YEAR, year)
            mCalendar.set(Calendar.MONTH, month)
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDiaryEntryDate()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentEditDiaryEntryBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mPreferences = requireActivity().getSharedPreferences(Constants.EZBUILD_PREFERENCES, Context.MODE_PRIVATE)
        (activity as MainActivity).useUpButton()
        setupListeners()

        val args: EditDiaryEntryFragmentArgs by navArgs()
        mDiaryEntry = args.diaryEntry

        mBinding.etDiaryEntryEdit.setText(mDiaryEntry.diaryEntry)
        mBinding.etEditDiaryEntryDate.setText(initialFormatDate(mDiaryEntry.diaryEntryDate!!))
    }

    private fun setupListeners() {
        mBinding.etEditDiaryEntryDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                mDateSetListener,
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        mBinding.btnSaveDiaryEntry.setOnClickListener {
            validateEntries()
        }
    }

    private fun initialFormatDate(date: String) : String {
        mFormattedSelectedDate = date
        return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date)!!).toString()
    }

    private fun validateEntries() {

        val diaryEntryDate = mBinding.etEditDiaryEntryDate.text.toString().trim()
        val diaryEntry = mBinding.etDiaryEntryEdit.text.toString().trim()

        when {
            TextUtils.isEmpty(diaryEntryDate) -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_select_diary_entry_date),
                    Toast.LENGTH_SHORT
                ).show()
            }

            TextUtils.isEmpty(diaryEntry) -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_insert_diary_entry),
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                val csDiaryEntryRequest = UpdateDiaryEntryRequest(
                    mDiaryEntry.idConstructionSiteDiary,
                    diaryEntry,
                    mFormattedSelectedDate,
                    mPreferences.getInt(Constants.USER_API_ID, 0)
                )
                updateConstructionSiteDiary(csDiaryEntryRequest)
            }
        }
    }

    private fun updateConstructionSiteDiary(csDiaryEntryRequest: UpdateDiaryEntryRequest) {

        (activity as MainActivity).showProgressDialog(resources.getString(R.string.please_wait))

        ezBuildAPIInterface.updateDiaryEntryForConstructionSite(csDiaryEntryRequest).enqueue(object:
            Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {

                    ezBuildAPIInterface.getConstructionSite(mDiaryEntry.constructionSiteID!!).enqueue(object:
                        Callback<GetConstructionSiteResponse> {
                        override fun onResponse(
                            call: Call<GetConstructionSiteResponse>,
                            response: Response<GetConstructionSiteResponse>
                        ) {
                            if(response.body() != null && response.body()!!.isSuccessful) {

                                (activity as MainActivity).hideProgressDialog()
                                findNavController().navigate(EditDiaryEntryFragmentDirections.actionEditCSDiaryEntryFragmentToConstructionSiteDetailsFragment(response.body()!!.constructionSite))
                            }
                        }

                        override fun onFailure(call: Call<GetConstructionSiteResponse>, t: Throwable) {
                            (activity as MainActivity).hideProgressDialog()
                            Log.e("CS FETCH ERROR", "Error fetching construction site.")
                        }
                    })
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                (activity as MainActivity).hideProgressDialog()
                Log.e("DIARY ENTRY ERROR", "Error updating construction site diary entry.")
            }
        })
    }

    private fun updateDiaryEntryDate() {
        val format = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        mBinding.etEditDiaryEntryDate.setText(sdf.format(mCalendar.time).toString())

        val apiFormat = "yyyy-MM-dd'T'HH:mm:ss"
        val apiSdf = SimpleDateFormat(apiFormat, Locale.getDefault())
        mFormattedSelectedDate = apiSdf.format(mCalendar.time)
    }
}