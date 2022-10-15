package hr.itrojnar.ezbuild.view.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.ActivityMainBinding
import hr.itrojnar.ezbuild.utils.Constants
import hr.itrojnar.ezbuild.enums.EmployeeType
import hr.itrojnar.ezbuild.view.fragments.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mBinding: ActivityMainBinding

    private lateinit var mNavController: NavController

    private lateinit var mToggle: ActionBarDrawerToggle

    private lateinit var mPreferences: SharedPreferences

    private lateinit var mAppBarConfiguration: AppBarConfiguration

    private var isUpButton = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        mPreferences = getSharedPreferences(Constants.EZBUILD_PREFERENCES, Context.MODE_PRIVATE)
        val userTypeID = mPreferences.getInt(Constants.USER_TYPE_ID, 0)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mToggle = ActionBarDrawerToggle(this, mBinding.drawerLayout, R.string.open, R.string.close)
        mBinding.drawerLayout.addDrawerListener(mToggle)
        mToggle.syncState()

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        mNavController = findNavController(R.id.nav_host_fragment_activity_main)

        if (userTypeID == EmployeeType.DIRECTOR.typeID) {
            mBinding.navBottomView.inflateMenu(R.menu.bottom_nav_menu_director)
            mBinding.navDrawerView.inflateMenu(R.menu.nav_drawer_menu_director)
            mAppBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_construction_sites, R.id.navigation_employees, R.id.navigation_employee_hours_menu, R.id.navigation_meetings, R.id.navigation_warehouse
                ), mBinding.drawerLayout
            )

        } else if (userTypeID == EmployeeType.ENGINEER.typeID) {
            mBinding.navBottomView.inflateMenu(R.menu.bottom_nav_menu_engineer)
            mBinding.navDrawerView.inflateMenu(R.menu.nav_drawer_menu_engineer)
            mAppBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_construction_sites, R.id.navigation_employee_hours_menu, R.id.navigation_meetings
                ), mBinding.drawerLayout
            )
        } else if (userTypeID == EmployeeType.WAREHOUSE_MANAGER.typeID) {
            mBinding.navBottomView.inflateMenu(R.menu.bottom_nav_menu_warehouse_manager)
            mBinding.navDrawerView.inflateMenu(R.menu.nav_drawer_menu_warehouse_engineer)
            mAppBarConfiguration = AppBarConfiguration(
                setOf(R.id.navigation_warehouse, R.id.navigation_meetings), mBinding.drawerLayout
            )
        }

        val graph = mNavController.navInflater.inflate(R.navigation.mobile_navigation)
        if (userTypeID == EmployeeType.WAREHOUSE_MANAGER.typeID) {
            graph.setStartDestination(R.id.navigation_warehouse)
        } else {
            graph.setStartDestination(R.id.navigation_construction_sites)
        }
        mNavController.graph = graph

        setupActionBarWithNavController(mNavController, mAppBarConfiguration)
        mBinding.navBottomView.setupWithNavController(mNavController)

        mBinding.navDrawerView.setupWithNavController(mNavController)
        mBinding.navDrawerView.setNavigationItemSelectedListener(this)

        val headerView = mBinding.navDrawerView.getHeaderView(0)
        headerView.findViewById<TextView>(R.id.nav_drawer_title_user_name).text = mPreferences.getString(Constants.USER_FULL_NAME, "")
        headerView.findViewById<TextView>(R.id.nav_drawer_title_user_email).text = mPreferences.getString(Constants.USER_EMAIL, "")
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(mNavController, null)
    }

    fun hideBottomNavigationView() {
        mBinding.navBottomView.clearAnimation()
        mBinding.navBottomView.animate().translationY(mBinding.navBottomView.height.toFloat()).duration = 300
    }

    fun showBottomNavigationView() {
        mBinding.navBottomView.clearAnimation()
        mBinding.navBottomView.animate().translationY(0f).duration = 300
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (isUpButton && !menuIDs.contains(item.itemId)) {
            mNavController.navigateUp()
            useHamburgerButton()
            return true
        }
        if (mToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun useUpButton() {
        mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        isUpButton = true
    }

    fun useHamburgerButton() {
        mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        isUpButton = false
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_construction_sites -> {
                mBinding.drawerLayout.close()
                mNavController.navigate(R.id.navigation_construction_sites)
            }
            R.id.navigation_employees -> {
                mBinding.drawerLayout.close()
                mNavController.navigate(R.id.navigation_employees)
            }
            R.id.navigation_employee_hours_menu -> {
                mBinding.drawerLayout.close()
                mNavController.navigate(R.id.navigation_employee_hours_menu)
            }
            R.id.navigation_meetings -> {
                mBinding.drawerLayout.close()
                mNavController.navigate(R.id.navigation_meetings)
            }
            R.id.navigation_warehouse -> {
                mBinding.drawerLayout.close()
                mNavController.navigate(R.id.navigation_warehouse)
            }
            R.id.navigation_profile -> {
                mBinding.drawerLayout.close()
                navigateToProfileFragment()
            }
            R.id.navigation_log_out -> {
                mPreferences.edit().clear().apply()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
        }
        return true
    }

    private fun navigateToProfileFragment() {

        when(mNavController.currentDestination?.id) {
            R.id.navigation_construction_sites -> {
                mNavController.navigate(ConstructionSitesFragmentDirections.actionNavigationConstructionSitesToProfileFragment())
            }
            R.id.navigation_employees -> {
                mNavController.navigate(EmployeesFragmentDirections.actionNavigationEmployeesToProfileFragment())
            }
            R.id.navigation_employee_hours_menu -> {
                mNavController.navigate(EmployeeHoursMenuFragmentDirections.actionNavigationEmployeeHoursMenuToProfileFragment())
            }
            R.id.navigation_meetings -> {
                mNavController.navigate(MeetingsFragmentDirections.actionNavigationMeetingsToProfileFragment())
            }
            R.id.navigation_warehouse -> {
                mNavController.navigate(WarehouseFragmentDirections.actionNavigationWarehouseToProfileFragment())
            }
        }
    }

    fun updateNavHeaderView() {
        val headerView = mBinding.navDrawerView.getHeaderView(0)
        headerView.findViewById<TextView>(R.id.nav_drawer_title_user_name).text = mPreferences.getString(Constants.USER_FULL_NAME, "")
    }

    companion object {
        private val menuIDs = listOf(R.id.action_cs_details_edit_cs, R.id.action_cs_details_delete_cs, R.id.action_employee_details_edit,
            R.id.action_employee_details_delete, R.id.action_equipment_add_equipment, R.id.action_equipment_details_edit, R.id.action_equipment_details_delete,
        R.id.action_leased_equipment_lease, R.id.action_leased_equipment_details_edit, R.id.action_leased_equipment_details_delete, R.id.action_profile_edit)
    }
}
