package com.crc.blue.preferences

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.crc.blue.R

class PreferencesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        setContentView(R.layout.activity_preferences)
    }
}