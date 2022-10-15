package hr.itrojnar.ezbuild.view.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.FragmentEmployeeHoursMenuBinding
import hr.itrojnar.ezbuild.enums.EmployeeType
import hr.itrojnar.ezbuild.model.messaging.constructionSite.ConstructionSitesResponse
import hr.itrojnar.ezbuild.model.network.EzBuildAPIInterface
import hr.itrojnar.ezbuild.model.viewModels.ConstructionSite
import hr.itrojnar.ezbuild.utils.Constants
import hr.itrojnar.ezbuild.view.activities.ConstructionSiteMapActivity
import hr.itrojnar.ezbuild.view.activities.MainActivity
import hr.itrojnar.ezbuild.view.adapters.ConstructionSiteAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmployeeHoursMenuFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var mBinding: FragmentEmployeeHoursMenuBinding

    private lateinit var mPreferences: SharedPreferences
    private val ezBuildAPIInterface = EzBuildAPIInterface.create()

    private lateinit var mConstructionSiteAdapter: ConstructionSiteAdapter
    private lateinit var mConstructionSites: ArrayList<ConstructionSite>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentEmployeeHoursMenuBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mPreferences = requireActivity().getSharedPreferences(Constants.EZBUILD_PREFERENCES, Context.MODE_PRIVATE)
        loadConstructionSites()

        if (mPreferences.getInt(Constants.USER_TYPE_ID, 0) == EmployeeType.ENGINEER.typeID) {
            mBinding.llEmployeeHoursMenuEmployees.visibility = View.GONE
        }

        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is MainActivity) {
            (activity as MainActivity).useHamburgerButton()
            (activity as MainActivity?)!!.showBottomNavigationView()
        }
    }

    private fun setupListeners() {

        mBinding.ehSwipeRefresh.setOnRefreshListener(this)

        mBinding.btnWorkHoursEngineers.setOnClickListener{
            findNavController().navigate(EmployeeHoursMenuFragmentDirections.actionNavigationEmployeeHoursMenuToEmployeeHoursForEmployeeTypesFragment(EmployeeType.ENGINEER.typeID))

            if (requireActivity() is MainActivity) {
                (activity as MainActivity).hideBottomNavigationView()
            }
        }
        mBinding.btnWorkHoursPhysicalWorkers.setOnClickListener{
            findNavController().navigate(EmployeeHoursMenuFragmentDirections.actionNavigationEmployeeHoursMenuToEmployeeHoursForEmployeeTypesFragment(EmployeeType.PHYSICAL_WORKER.typeID))

            if (requireActivity() is MainActivity) {
                (activity as MainActivity).hideBottomNavigationView()
            }
        }
        mBinding.btnWorkHoursWarehouseManager.setOnClickListener{
            findNavController().navigate(EmployeeHoursMenuFragmentDirections.actionNavigationEmployeeHoursMenuToEmployeeHoursForEmployeeTypesFragment(EmployeeType.WAREHOUSE_MANAGER.typeID))

            if (requireActivity() is MainActivity) {
                (activity as MainActivity).hideBottomNavigationView()
            }
        }
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

    private  fun setupConstructionSitesRecyclerView() {

        mBinding.rvWorkHoursConstructionSites.layoutManager = LinearLayoutManager(requireContext())
        mConstructionSiteAdapter = ConstructionSiteAdapter(this@EmployeeHoursMenuFragment, mConstructionSites, mPreferences.getInt(Constants.USER_TYPE_ID, 1))
        mBinding.rvWorkHoursConstructionSites.adapter = mConstructionSiteAdapter

        if (mConstructionSites.isNotEmpty()) {
            mBinding.tvWorkHoursNoConstructionSites.visibility = View.GONE
            mBinding.rvWorkHoursConstructionSites.visibility = View.VISIBLE
        } else {
            mBinding.tvWorkHoursNoConstructionSites.visibility = View.VISIBLE
            mBinding.rvWorkHoursConstructionSites.visibility = View.GONE
        }
    }

    fun employeeHoursForConstructionSite(constructionSite: ConstructionSite) {
        findNavController().navigate(EmployeeHoursMenuFragmentDirections.actionNavigationEmployeeHoursMenuToEmployeeHoursForConstructionSiteFragment(constructionSite))

        if (activity is MainActivity) {
            (activity as MainActivity).hideBottomNavigationView()
        }
    }

    override fun onRefresh() {
        mBinding.ehSwipeRefresh.isRefreshing = true
        refreshConstructionSites()
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
                    mBinding.ehSwipeRefresh.isRefreshing = false
                    setupConstructionSitesRecyclerView()
                }
            }

            override fun onFailure(
                call: Call<ConstructionSitesResponse>,
                t: Throwable
            ) {
                mBinding.ehSwipeRefresh.isRefreshing = false
                (activity as MainActivity).showSnackBar(getString(R.string.error_api_construction_sites_loading_fail), true)
                Log.e(requireActivity().javaClass.simpleName, "Error while getting construction sites from API.", t)
            }
        })
    }

    fun showConstructionSiteOnMap(constructionSite: ConstructionSite) {
        val intent = Intent(requireContext(), ConstructionSiteMapActivity::class.java)
        intent.putExtra(Constants.EXTRA_CS_DETAILS, constructionSite)
        startActivity(intent)
    }
}