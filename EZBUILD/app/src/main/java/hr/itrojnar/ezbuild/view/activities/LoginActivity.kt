package hr.itrojnar.ezbuild.view.activities

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.viewpager2.widget.ViewPager2
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.ActivityLoginBinding
import hr.itrojnar.ezbuild.view.adapters.LoginAdapter


class LoginActivity : BaseActivity() {

    private lateinit var mBinding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initViewPager()
        initLoginAdapter()
        initOnClickListeners()
        setStatusBarColorTransparent()
    }

    private lateinit var viewPager: ViewPager2

    private fun initViewPager() {
        viewPager = mBinding.loginViewPager

        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when(position) {
                    0 -> {
                        mBinding.tvAction1.text = resources.getText(R.string.lbl_register)
                        mBinding.tvAction2.text = resources.getText(R.string.lbl_change_password)
                    }
                    1 -> {
                        mBinding.tvAction1.text = resources.getText(R.string.lbl_login)
                        mBinding.tvAction2.text = resources.getText(R.string.lbl_change_password)
                    }
                    2 -> {
                        mBinding.tvAction1.text = resources.getText(R.string.lbl_login)
                        mBinding.tvAction2.text = resources.getText(R.string.lbl_register)
                    }
                }
                super.onPageSelected(position)
            }
        })
    }

    private fun initLoginAdapter() {
        viewPager.adapter = LoginAdapter(this, 3)
        viewPager.isUserInputEnabled = true
    }

    private fun initOnClickListeners() {
        mBinding.tvAction1.setOnClickListener {
            when (viewPager.currentItem) {
                2 -> viewPager.setCurrentItem(0, true)
                0 -> viewPager.setCurrentItem(1, true)
                1 -> viewPager.setCurrentItem(0, true)
            }
        }
        mBinding.tvAction2.setOnClickListener {
            when (viewPager.currentItem) {
                2 -> viewPager.setCurrentItem(1, true)
                0 -> viewPager.setCurrentItem(2, true)
                1 -> viewPager.setCurrentItem(2, true)
            }
        }
    }

    private fun setStatusBarColorTransparent() {
        val window: Window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.setStatusBarColor(Color.TRANSPARENT)
    }
}