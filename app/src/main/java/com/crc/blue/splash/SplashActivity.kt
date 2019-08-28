package com.crc.blue.splash

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.crc.blue.main.MainActivity
import com.crc.blue.R
import com.crc.blue.base.Constants
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if(Build.VERSION.SDK_INT < 21) {
//            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
//        } else {
//            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
//            actionBar?.hide()
//        }
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        setContentView(R.layout.activity_splash)

        toast(R.string.str_currently_unavailable)
    }

    override fun onStart() {
        super.onStart()

        Handler().postDelayed({
            startActivity<MainActivity>()

        }, Constants.SPLASH_LOADING_TIME)
    }
}