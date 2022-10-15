package hr.itrojnar.ezbuild.view.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.databinding.ActivitySplashBinding
import hr.itrojnar.ezbuild.utils.Constants

class SplashActivity : AppCompatActivity() {

    private lateinit var mPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashBinding: ActivitySplashBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(splashBinding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        mPreferences = getSharedPreferences(Constants.EZBUILD_PREFERENCES, Context.MODE_PRIVATE)

        val splashAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_splash)
        splashBinding.tvAppName.animation = splashAnimation

        val firebaseUid = mPreferences.getString(Constants.USER_FIREBASE_ID, "")

        splashAnimation.setAnimationListener(object: Animation.AnimationListener{
            override fun onAnimationStart(p0: Animation?) {}

            override fun onAnimationEnd(p0: Animation?) {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (!firebaseUid.isNullOrEmpty()) {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    } else {
                        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                        finish()
                    }
                }, 1000)
            }

            override fun onAnimationRepeat(p0: Animation?) {}
        })
    }
}