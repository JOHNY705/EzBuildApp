package hr.itrojnar.ezbuild.view.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.FragmentConstructionSiteDetailsBinding
import hr.itrojnar.ezbuild.model.messaging.BaseResponse
import hr.itrojnar.ezbuild.model.viewModels.ConstructionSite
import hr.itrojnar.ezbuild.model.viewModels.ConstructionSiteDiaryEntry
import hr.itrojnar.ezbuild.model.network.EzBuildAPIInterface
import hr.itrojnar.ezbuild.utils.Constants
import hr.itrojnar.ezbuild.enums.EmployeeType
import hr.itrojnar.ezbuild.view.activities.ConstructionSiteMapActivity
import hr.itrojnar.ezbuild.view.activities.MainActivity
import hr.itrojnar.ezbuild.view.adapters.CSDetailsEmployeesAdapter
import hr.itrojnar.ezbuild.view.adapters.CSDiaryEntriesAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConstructionSiteDetailsFragment : Fragment() {

    private lateinit var mBinding: FragmentConstructionSiteDetailsBinding
    private lateinit var mConstructionSiteDetails: ConstructionSite

    private lateinit var mCSDetailsEmployeesAdapter: CSDetailsEmployeesAdapter
    private lateinit var mCSDiaryEntriesAdapter: CSDiaryEntriesAdapter

    private lateinit var mPreferences: SharedPreferences

    private val ezBuildAPIInterface = EzBuildAPIInterface.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentConstructionSiteDetailsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: ConstructionSiteDetailsFragmentArgs by navArgs()
        mConstructionSiteDetails = args.constructionSite

        (activity as MainActivity).useUpButton()

        mPreferences = requireActivity().getSharedPreferences(Constants.EZBUILD_PREFERENCES, Context.MODE_PRIVATE)

        mConstructionSiteDetails.base64Image.let {
            Glide.with(requireActivity())
                .asBitmap()
                .load(Base64.decode(mConstructionSiteDetails.base64Image, Base64.DEFAULT))
                .centerCrop()
                .into(mBinding.ivCsDetailsImage)
        }

        mBinding.tvCsDetailsFullAddress.text = mConstructionSiteDetails.fullAddress
        mBinding.tvCsDetailsCsManager.text = mConstructionSiteDetails.constructionSiteManager?.fullName

        if (mConstructionSiteDetails.isActive!!) {
            mBinding.tvCsDetailsCsStatus.text = getString(R.string.lbl_cs_details_status_active)
        } else {
            mBinding.tvCsDetailsCsStatus.text = getString(R.string.lbl_cs_details_status_inactive)
        }

        mBinding.rvCsDetailsAssignedEmployees.layoutManager = LinearLayoutManager(requireContext())
        mCSDetailsEmployeesAdapter = CSDetailsEmployeesAdapter(this@ConstructionSiteDetailsFragment, mConstructionSiteDetails.employees)
        mBinding.rvCsDetailsAssignedEmployees.adapter = mCSDetailsEmployeesAdapter

        if (mConstructionSiteDetails.employees.isNotEmpty()) {
            mBinding.rvCsDetailsAssignedEmployees.visibility = View.VISIBLE
            mBinding.tvCsDetailsNoAssignedEmployeesAddedYet.visibility = View.GONE
        }

        mConstructionSiteDetails.constructionSiteDiaryEntries.sortByDescending { diaryEntry -> diaryEntry.diaryEntryDate }

        mBinding.rvCsDetailsDiaryEntries.layoutManager = LinearLayoutManager(requireContext())
        mCSDiaryEntriesAdapter = CSDiaryEntriesAdapter(this@ConstructionSiteDetailsFragment, mConstructionSiteDetails.constructionSiteDiaryEntries)
        mBinding.rvCsDetailsDiaryEntries.adapter = mCSDiaryEntriesAdapter

        if (mConstructionSiteDetails.constructionSiteDiaryEntries.isNotEmpty()) {
            mBinding.rvCsDetailsDiaryEntries.visibility = View.VISIBLE
            mBinding.tvCsDetailsNoDiaryEntriesAdded.visibility = View.GONE
        }

        setupListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_construction_site_details, menu)
        if (mPreferences.getInt(Constants.USER_TYPE_ID, 0) != EmployeeType.DIRECTOR.typeID) {
            menu.removeItem(R.id.action_cs_details_edit_cs)
            menu.removeItem(R.id.action_cs_details_delete_cs)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_cs_details_edit_cs -> {
                findNavController().navigate(ConstructionSiteDetailsFragmentDirections.actionConstructionSiteDetailsFragmentToEditConstructionSiteFragment(mConstructionSiteDetails, Constants.FRAGMENT_CONSTRUCTION_SITE_DETAILS))
            }
            R.id.action_cs_details_delete_cs -> {
                showDeleteDialog()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showDeleteDialog() {
        val builder = AlertDialog.Builder(requireActivity(), R.style.AlertDialogTheme)
        builder.setTitle(resources.getString(R.string.title_delete_construction_site))
        builder.setMessage(resources.getString(R.string.msg_delete_construction_site_dialog))
        builder.setIcon(resources.getDrawable(R.drawable.ic_warning_24))
        builder.setPositiveButton(resources.getString(R.string.btn_yes)) { dialogInterface, _ ->

            dialogInterface.dismiss()

            (activity as MainActivity).showProgressDialog(resources.getString(R.string.please_wait))

            ezBuildAPIInterface.deleteConstructionSite(mConstructionSiteDetails.idConstructionSite).enqueue(object:
                Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if(response.body() != null && response.body()!!.isSuccessful) {

                        (activity as MainActivity).hideProgressDialog()
                        (activity as MainActivity).useHamburgerButton()
                        findNavController().navigate(ConstructionSiteDetailsFragmentDirections.actionConstructionSiteDetailsFragmentToNavigationConstructionSites())
                    }
                }

                override fun onFailure(
                    call: Call<BaseResponse>,
                    t: Throwable
                ) {
                    (activity as MainActivity).hideProgressDialog()
                    (activity as MainActivity).showSnackBar(getString(R.string.error_api_delete_construction_site_fail), true)
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

    private fun setupListeners() {
        mBinding.btnCsDetailsShowOnMap.setOnClickListener {
            val intent = Intent(requireContext(), ConstructionSiteMapActivity::class.java)
            intent.putExtra(Constants.EXTRA_CS_DETAILS, mConstructionSiteDetails)
            startActivity(intent)
        }

        mBinding.btnCsDetailsAddCsDiaryEntry.setOnClickListener {
            findNavController().navigate(ConstructionSiteDetailsFragmentDirections.actionConstructionSiteDetailsFragmentToAddDiaryEntryFragment(mConstructionSiteDetails.idConstructionSite!!))
        }
    }

    fun editDiaryEntry(diaryEntry: ConstructionSiteDiaryEntry) {
        findNavController().navigate(ConstructionSiteDetailsFragmentDirections.actionConstructionSiteDetailsFragmentToEditCSDiaryEntryFragment(diaryEntry))
    }

    fun deleteDiaryEntry(diaryEntry: ConstructionSiteDiaryEntry) {
        val builder = AlertDialog.Builder(requireActivity(), R.style.AlertDialogTheme)
        builder.setTitle(resources.getString(R.string.title_delete_diary_entry))
        builder.setMessage(resources.getString(R.string.msg_delete_diary_entry_dialog))
        builder.setIcon(resources.getDrawable(R.drawable.ic_warning_24))
        builder.setPositiveButton(resources.getString(R.string.btn_yes)) { dialogInterface, _ ->

            dialogInterface.dismiss()

            (activity as MainActivity).showProgressDialog(resources.getString(R.string.please_wait))

            ezBuildAPIInterface.deleteConstructionSiteDiaryEntry(diaryEntry.idConstructionSiteDiary).enqueue(object:
                Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if(response.body() != null && response.body()!!.isSuccessful) {

                        mCSDiaryEntriesAdapter.removeDiaryEntry(diaryEntry)

                        if (mConstructionSiteDetails.constructionSiteDiaryEntries.isEmpty()) {
                            mBinding.rvCsDetailsDiaryEntries.visibility = View.GONE
                            mBinding.tvCsDetailsNoDiaryEntriesAdded.visibility = View.VISIBLE
                        }

                        (activity as MainActivity).hideProgressDialog()
                    }
                }

                override fun onFailure(
                    call: Call<BaseResponse>,
                    t: Throwable
                ) {
                    (activity as MainActivity).hideProgressDialog()
                    (activity as MainActivity).showSnackBar(getString(R.string.error_api_delete_diary_entry_fail), true)
                    Log.e(requireActivity().javaClass.simpleName, "Error while deleting diary entry on API.", t)
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
}