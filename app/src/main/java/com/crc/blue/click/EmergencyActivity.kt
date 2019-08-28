package com.crc.blue.click

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.crc.blue.R
import kotlinx.android.synthetic.main.activity_emergency.*
import org.jetbrains.anko.toast

class EmergencyActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        setContentView(R.layout.activity_emergency)

        rb_emergency_emergency.setOnClickListener(this)
        rb_emergency_contact.setOnClickListener(this)
        cl_emergency_layout.visibility = View.VISIBLE
        cl_emergency_contact_layout.visibility = View.GONE

    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.rb_emergency_emergency -> {
                cl_emergency_layout.visibility = View.VISIBLE
                cl_emergency_contact_layout.visibility = View.GONE
            }
            R.id.rb_emergency_contact -> {
                cl_emergency_layout.visibility = View.GONE
                cl_emergency_contact_layout.visibility = View.VISIBLE
            }
        }

    }
}