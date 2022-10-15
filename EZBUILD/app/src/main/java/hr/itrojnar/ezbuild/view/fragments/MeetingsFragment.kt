package hr.itrojnar.ezbuild.view.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.FragmentMeetingsBinding
import hr.itrojnar.ezbuild.model.messaging.BaseResponse
import hr.itrojnar.ezbuild.model.messaging.meeting.MeetingsForEmployeeResponse
import hr.itrojnar.ezbuild.model.network.EzBuildAPIInterface
import hr.itrojnar.ezbuild.model.viewModels.Meeting
import hr.itrojnar.ezbuild.utils.Constants
import hr.itrojnar.ezbuild.view.activities.MainActivity
import hr.itrojnar.ezbuild.view.adapters.MeetingAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

class MeetingsFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var mBinding: FragmentMeetingsBinding
    private lateinit var mSharedPreferences: SharedPreferences
    private val ezBuildAPIInterface = EzBuildAPIInterface.create()

    private lateinit var mMeetingsTodayAdapter: MeetingAdapter
    private lateinit var mMeetingsSelectedDateAdapter: MeetingAdapter

    private lateinit var mSelectedCalendarViewDate: LocalDate

    private var mMeetings : ArrayList<Meeting> = arrayListOf()
    private var mMeetingsToday : List<Meeting> = arrayListOf()
    private var mMeetingsSelectedDate : List<Meeting> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentMeetingsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mSharedPreferences = requireActivity().getSharedPreferences(Constants.EZBUILD_PREFERENCES, Context.MODE_PRIVATE)

        mBinding.meetingsSwipeRefresh.setOnRefreshListener(this)

        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.")
        val formattedDate = LocalDate.now().format(formatter)
        val currentDate = LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.titlecase() } + ", " + formattedDate

        mBinding.tvMeetingsCurrentDate.text = currentDate
        mBinding.tvMeetingsSelectedDate.text = currentDate

        mSelectedCalendarViewDate = LocalDate.now()

        mBinding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            mSelectedCalendarViewDate = LocalDate.of(year, month + 1, dayOfMonth)

            val newFormattedDate = mSelectedCalendarViewDate.format(formatter)
            val newSelectedDate = mSelectedCalendarViewDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.titlecase() } + ", " + newFormattedDate
            mBinding.tvMeetingsSelectedDate.text = newSelectedDate

            val apiFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

            mMeetingsSelectedDate = mMeetings.filter {
                LocalDate.parse(it.meetingDate, apiFormatter) == mSelectedCalendarViewDate
            }

            setupMeetingsSelectedDateAdapter()
        }

        mBinding.calendarView.visibility = View.GONE

        mBinding.btnToggleCalendar.setOnClickListener {
            if (mBinding.calendarView.isVisible) {
                mBinding.calendarView.visibility = View.GONE
                mBinding.btnToggleCalendar.text = getString(R.string.btn_show_calendar)
            } else {
                mBinding.calendarView.visibility = View.VISIBLE
                mBinding.btnToggleCalendar.text = getString(R.string.btn_hide_calendar)
            }
        }

        loadMeetingsForEmployee()
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is MainActivity) {
            (activity as MainActivity).useHamburgerButton()
            (activity as MainActivity).showBottomNavigationView()
        }
    }

    private fun setupMeetingsSelectedDateAdapter() {
        mBinding.rvMeetingsForSelectedDate.layoutManager = LinearLayoutManager(requireContext())
        mMeetingsSelectedDateAdapter = MeetingAdapter(this@MeetingsFragment, mMeetingsSelectedDate)
        mBinding.rvMeetingsForSelectedDate.adapter = mMeetingsSelectedDateAdapter

        toggleRecyclerViewVisibility()
    }

    private fun loadMeetingsForEmployee() {

        (activity as MainActivity).showProgressDialog(getString(R.string.please_wait))

        ezBuildAPIInterface.getMeetingsForEmployee(mSharedPreferences.getInt(Constants.USER_API_ID, 0)).enqueue(object:
            Callback<MeetingsForEmployeeResponse> {
            override fun onResponse(
                call: Call<MeetingsForEmployeeResponse>,
                response: Response<MeetingsForEmployeeResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {
                    mMeetings = response.body()!!.meetings
                    setupData()
                    (activity as MainActivity).hideProgressDialog()
                }
            }

            override fun onFailure(call: Call<MeetingsForEmployeeResponse>, t: Throwable) {
                (activity as MainActivity).hideProgressDialog()
                Log.e("MEETINGS ERROR", "Error fetching meetings for employee.")
            }
        })
    }

    private fun setupData() {

        mMeetings.sortedBy { it.meetingDate + it.meetingStartTime }

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val localDate = LocalDate.now()

        mMeetingsToday = mMeetings.filter {
            LocalDate.parse(it.meetingDate, formatter) == localDate
        }

        mMeetingsSelectedDate = mMeetings.filter {
            LocalDate.parse(it.meetingDate, formatter) == mSelectedCalendarViewDate
        }

        setupRecyclerViewAdapters()
    }

    private fun setupRecyclerViewAdapters() {

        mBinding.rvMeetingsForToday.layoutManager = LinearLayoutManager(requireContext())
        mMeetingsTodayAdapter = MeetingAdapter(this@MeetingsFragment, mMeetingsToday)
        mBinding.rvMeetingsForToday.adapter = mMeetingsTodayAdapter

        mBinding.rvMeetingsForSelectedDate.layoutManager = LinearLayoutManager(requireContext())
        mMeetingsSelectedDateAdapter = MeetingAdapter(this@MeetingsFragment, mMeetingsSelectedDate)
        mBinding.rvMeetingsForSelectedDate.adapter = mMeetingsSelectedDateAdapter

        toggleRecyclerViewVisibility()
    }

    private fun toggleRecyclerViewVisibility() {
        if (mMeetingsToday.isNotEmpty()) {
            mBinding.rvMeetingsForToday.visibility = View.VISIBLE
            mBinding.tvMeetingsNoMeetingsToday.visibility = View.GONE
        }
        else {
            mBinding.rvMeetingsForToday.visibility = View.GONE
            mBinding.tvMeetingsNoMeetingsToday.visibility = View.VISIBLE
        }

        if (mMeetingsSelectedDate.isNotEmpty()) {
            mBinding.rvMeetingsForSelectedDate.visibility = View.VISIBLE
            mBinding.tvMeetingsNoMeetingsSelectedDate.visibility = View.GONE
        }
        else {
            mBinding.rvMeetingsForSelectedDate.visibility = View.GONE
            mBinding.tvMeetingsNoMeetingsSelectedDate.visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_meetings, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_meetings_add_meeting -> {
                findNavController().navigate(MeetingsFragmentDirections.actionNavigationMeetingsToAddMeetingFragment())

                if (requireActivity() is MainActivity) {
                    (activity as MainActivity).hideBottomNavigationView()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun deleteMeeting(meeting: Meeting) {

        val builder = AlertDialog.Builder(requireActivity(), R.style.AlertDialogTheme)
        builder.setTitle(resources.getString(R.string.title_delete_meeting))
        builder.setMessage(resources.getString(R.string.msg_delete_meeting_dialog))
        builder.setIcon(resources.getDrawable(R.drawable.ic_warning_24))
        builder.setPositiveButton(resources.getString(R.string.btn_yes)) { dialogInterface, _ ->

            dialogInterface.dismiss()

            (activity as MainActivity).showProgressDialog(resources.getString(R.string.please_wait))

            ezBuildAPIInterface.deleteMeeting(meeting.idMeeting!!).enqueue(object:
                Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if(response.body() != null && response.body()!!.isSuccessful) {
                        mMeetings.remove(meeting)
                        setupData()
                        (activity as MainActivity).hideProgressDialog()
                    }
                }

                override fun onFailure(
                    call: Call<BaseResponse>,
                    t: Throwable
                ) {
                    (activity as MainActivity).hideProgressDialog()
                    (activity as MainActivity).showSnackBar(getString(R.string.error_api_delete_meeting_fail), true)
                    Log.e(requireActivity().javaClass.simpleName, "Error while deleting construction site on API.", t)
                }
            })
        }

        builder.setNegativeButton(resources.getString(R.string.btn_no)) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun editMeeting(meeting: Meeting) {
        findNavController().navigate(MeetingsFragmentDirections.actionNavigationMeetingsToEditMeetingFragment(meeting))

        if (requireActivity() is MainActivity) {
            (activity as MainActivity).hideBottomNavigationView()
        }
    }

    override fun onRefresh() {
        mBinding.meetingsSwipeRefresh.isRefreshing = true
        refreshMeetingsForEmployee()
    }

    private fun refreshMeetingsForEmployee() {
        ezBuildAPIInterface.getMeetingsForEmployee(mSharedPreferences.getInt(Constants.USER_API_ID, 0)).enqueue(object:
            Callback<MeetingsForEmployeeResponse> {
            override fun onResponse(
                call: Call<MeetingsForEmployeeResponse>,
                response: Response<MeetingsForEmployeeResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {
                    mMeetings = response.body()!!.meetings
                    setupData()
                    mBinding.meetingsSwipeRefresh.isRefreshing = false
                }
            }

            override fun onFailure(call: Call<MeetingsForEmployeeResponse>, t: Throwable) {
                mBinding.meetingsSwipeRefresh.isRefreshing = false
                Log.e("MEETINGS ERROR", "Error fetching meetings for employee.")
            }
        })
    }
}