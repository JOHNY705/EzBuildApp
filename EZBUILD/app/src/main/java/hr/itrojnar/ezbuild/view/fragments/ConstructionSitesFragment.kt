package hr.itrojnar.ezbuild.view.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.FragmentConstructionSitesBinding
import hr.itrojnar.ezbuild.model.messaging.BaseResponse
import hr.itrojnar.ezbuild.model.messaging.constructionSite.ConstructionSitesResponse
import hr.itrojnar.ezbuild.model.viewModels.ConstructionSite
import hr.itrojnar.ezbuild.model.network.EzBuildAPIInterface
import hr.itrojnar.ezbuild.utils.Constants
import hr.itrojnar.ezbuild.enums.EmployeeType
import hr.itrojnar.ezbuild.view.activities.ConstructionSiteMapActivity
import hr.itrojnar.ezbuild.view.activities.MainActivity
import hr.itrojnar.ezbuild.view.adapters.ConstructionSiteAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConstructionSitesFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var mBinding: FragmentConstructionSitesBinding

    private lateinit var mPreferences: SharedPreferences
    private val ezBuildAPIInterface = EzBuildAPIInterface.create()

    private lateinit var mConstructionSiteAdapter: ConstructionSiteAdapter
    private lateinit var mConstructionSites: ArrayList<ConstructionSite>
    private lateinit var mActiveConstructionSites: ArrayList<ConstructionSite>
    private lateinit var mInactiveConstructionSites: ArrayList<ConstructionSite>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentConstructionSitesBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPreferences = requireActivity().getSharedPreferences(Constants.EZBUILD_PREFERENCES, Context.MODE_PRIVATE)
        mBinding.csSwipeRefresh.setOnRefreshListener(this)
    }

    fun showAddConstructionSiteFragment() {
        findNavController().navigate(ConstructionSitesFragmentDirections.actionNavigationConstructionSitesToAddConstructionSiteFragment())

        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.hideBottomNavigationView()
        }
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is MainActivity) {
            (activity as MainActivity).useHamburgerButton()
            (activity as MainActivity).showBottomNavigationView()
        }
        loadConstructionSites()
    }

    override fun onRefresh() {
        mBinding.csSwipeRefresh.isRefreshing = true
        refreshConstructionSites()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_construction_sites, menu)
        if (mPreferences.getInt(Constants.USER_TYPE_ID, 0) != EmployeeType.DIRECTOR.typeID) {
            menu.removeItem(R.id.action_add_construction_site)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_add_construction_site -> {
                showAddConstructionSiteFragment()
            }
            R.id.action_view_construction_sites_on_map -> {
                val intent = Intent(requireContext(), ConstructionSiteMapActivity::class.java)
                val constructionSiteSites = mConstructionSites
                constructionSiteSites.forEach {
                    it.base64Image = ""
                }
                intent.putParcelableArrayListExtra(Constants.EXTRA_ALL_CS_DETAILS, constructionSiteSites)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun loadConstructionSites() {
        (activity as MainActivity).showProgressDialog(resources.getString(R.string.please_wait))

        ezBuildAPIInterface.getConstructionSitesForFirm(mPreferences.getInt(Constants.USER_FIRM_ID, 0)).enqueue(object:
            Callback<ConstructionSitesResponse> {
            override fun onResponse(
                call: Call<ConstructionSitesResponse>,
                response: Response<ConstructionSitesResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {
                    if (mPreferences.getInt(Constants.USER_TYPE_ID, 0) == EmployeeType.DIRECTOR.typeID) {
                        mConstructionSites = response.body()!!.constructionSites
                    }
                    else {
                        mConstructionSites = ArrayList()
                        response.body()!!.constructionSites.forEach {
                            if (it.constructionSiteManager?.idEmployee == mPreferences.getInt(Constants.USER_API_ID, 0)) {
                                mConstructionSites.add(it)
                            }
                        }
                    }
                    (activity as MainActivity).hideProgressDialog()
                    setupConstructionSitesRecyclerView()
                }
            }

            override fun onFailure(
                call: Call<ConstructionSitesResponse>,
                t: Throwable
            ) {
                (activity as MainActivity).hideProgressDialog()
                (activity as MainActivity).showSnackBar(getString(R.string.error_api_construction_sites_loading_fail), true)
                Log.e(requireActivity().javaClass.simpleName, "Error while getting construction sites from API.", t)
            }
        })
    }

    private fun refreshConstructionSites() {

        ezBuildAPIInterface.getConstructionSitesForFirm(mPreferences.getInt(Constants.USER_FIRM_ID, 0)).enqueue(object:
            Callback<ConstructionSitesResponse> {
            override fun onResponse(
                call: Call<ConstructionSitesResponse>,
                response: Response<ConstructionSitesResponse>
            ) {
                if(response.body() != null && response.body()!!.isSuccessful) {
                    if (mPreferences.getInt(Constants.USER_TYPE_ID, 0) == EmployeeType.DIRECTOR.typeID) {
                        mConstructionSites = response.body()!!.constructionSites
                    }
                    else {
                        mConstructionSites = ArrayList()
                        response.body()!!.constructionSites.forEach {
                            if (it.constructionSiteManager?.idEmployee == mPreferences.getInt(Constants.USER_API_ID, 0)) {
                                mConstructionSites.add(it)
                            }
                        }
                    }
                    mBinding.csSwipeRefresh.isRefreshing = false
                    setupConstructionSitesRecyclerView()
                }
            }

            override fun onFailure(
                call: Call<ConstructionSitesResponse>,
                t: Throwable
            ) {
                mBinding.csSwipeRefresh.isRefreshing = false
                (activity as MainActivity).showSnackBar(getString(R.string.error_api_construction_sites_loading_fail), true)
                Log.e(requireActivity().javaClass.simpleName, "Error while getting construction sites from API.", t)
            }
        })
    }

    private fun setupConstructionSitesRecyclerView() {

        mActiveConstructionSites = ArrayList()
        mInactiveConstructionSites = ArrayList()

        mConstructionSites.forEach { cs ->
            if (cs.isActive!!) {
                mActiveConstructionSites.add(cs)
            } else {
                mInactiveConstructionSites.add(cs)
            }
        }

        mBinding.rvConstructionSitesListActive.layoutManager = LinearLayoutManager(requireContext())
        mConstructionSiteAdapter = ConstructionSiteAdapter(this@ConstructionSitesFragment, mActiveConstructionSites, mPreferences.getInt(Constants.USER_TYPE_ID, 1))
        mBinding.rvConstructionSitesListActive.adapter = mConstructionSiteAdapter

        var csAdapter = ConstructionSiteAdapter(this@ConstructionSitesFragment, mInactiveConstructionSites, mPreferences.getInt(Constants.USER_TYPE_ID, 1))

        mBinding.rvConstructionSitesListInactive.layoutManager = LinearLayoutManager(requireContext())
        mConstructionSiteAdapter = ConstructionSiteAdapter(this@ConstructionSitesFragment, mInactiveConstructionSites, mPreferences.getInt(Constants.USER_TYPE_ID, 1))
        mBinding.rvConstructionSitesListInactive.adapter = mConstructionSiteAdapter

        toggleRecyclerViewsVisibility()
    }

    private fun toggleRecyclerViewsVisibility() {
        if (mActiveConstructionSites.isNotEmpty()) {
            mBinding.tvCsFragmentNoActiveCsAvailable.visibility = View.GONE
            mBinding.rvConstructionSitesListActive.visibility = View.VISIBLE
        } else {
            mBinding.tvCsFragmentNoActiveCsAvailable.visibility = View.VISIBLE
            mBinding.rvConstructionSitesListActive.visibility = View.GONE
        }

        if (mInactiveConstructionSites.isNotEmpty()) {
            mBinding.tvCsFragmentNoInactiveCsAvailable.visibility = View.GONE
            mBinding.rvConstructionSitesListInactive.visibility = View.VISIBLE
        } else {
            mBinding.tvCsFragmentNoInactiveCsAvailable.visibility = View.VISIBLE
            mBinding.rvConstructionSitesListInactive.visibility = View.GONE
        }
    }

    fun constructionSiteDetails(constructionSite: ConstructionSite) {

        if (requireActivity() is MainActivity) {
            (activity as MainActivity).hideBottomNavigationView()
        }

        findNavController().navigate(ConstructionSitesFragmentDirections.actionNavigationConstructionSitesToConstructionSiteDetailsFragment(constructionSite))
    }

    fun editConstructionSite(constructionSite: ConstructionSite) {

        if (requireActivity() is MainActivity) {
            (activity as MainActivity).hideBottomNavigationView()
        }

        findNavController().navigate(ConstructionSitesFragmentDirections.actionNavigationConstructionSitesToEditConstructionSiteFragment(constructionSite, Constants.FRAGMENT_CONSTRUCTION_SITES))
    }

    fun deleteConstructionSite(constructionSite: ConstructionSite) {
        val builder = AlertDialog.Builder(requireActivity(), R.style.AlertDialogTheme)
        builder.setTitle(resources.getString(R.string.title_delete_construction_site))
        builder.setMessage(resources.getString(R.string.msg_delete_construction_site_dialog))
        builder.setIcon(resources.getDrawable(R.drawable.ic_warning_24))
        builder.setPositiveButton(resources.getString(R.string.btn_yes)) { dialogInterface, _ ->

            dialogInterface.dismiss()

            (activity as MainActivity).showProgressDialog(resources.getString(R.string.please_wait))

            ezBuildAPIInterface.deleteConstructionSite(constructionSite.idConstructionSite!!).enqueue(object:
                Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if(response.body() != null && response.body()!!.isSuccessful) {

                        mConstructionSites.remove(constructionSite)

                        if (constructionSite.isActive!!) {
                            mActiveConstructionSites.remove(constructionSite)
                            mBinding.rvConstructionSitesListActive.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                        } else {
                            mInactiveConstructionSites.remove(constructionSite)
                            mBinding.rvConstructionSitesListInactive.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                        }

                        toggleRecyclerViewsVisibility()

                        (activity as MainActivity).hideProgressDialog()
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

    fun showConstructionSiteOnMap(constructionSite: ConstructionSite) {
        val intent = Intent(requireContext(), ConstructionSiteMapActivity::class.java)
        intent.putExtra(Constants.EXTRA_CS_DETAILS, constructionSite)
        startActivity(intent)
    }
}