package com.crc.blue.click

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.crc.blue.R

class RecordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        setContentView(R.layout.activity_record)
    }
}