package hr.itrojnar.ezbuild.view.adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import hr.itrojnar.ezbuild.view.fragments.ChangePasswordFragment
import hr.itrojnar.ezbuild.view.fragments.LoginFragment
import hr.itrojnar.ezbuild.view.fragments.RegisterFragment

class LoginAdapter(activity: AppCompatActivity, private val itemsCount: Int) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = itemsCount

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> LoginFragment()
            1 -> RegisterFragment()
            2 -> ChangePasswordFragment()
            else -> RegisterFragment()
        }
    }
}