package com.crc.blue.click

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.crc.blue.R
import kotlinx.android.synthetic.main.activity_customize.*
import org.jetbrains.anko.toast

class CustomizeActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        setContentView(R.layout.activity_customize)

        toast(R.string.str_currently_unavailable)

        rb_customize_customize.setOnClickListener(this)
        rb_customize_contact.setOnClickListener(this)
        cl_customize_layout.visibility = View.VISIBLE
        cl_customize_contact_layout.visibility = View.GONE

    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.rb_customize_customize -> {
                cl_customize_layout.visibility = View.VISIBLE
                cl_customize_contact_layout.visibility = View.GONE
            }
            R.id.rb_customize_contact -> {
                cl_customize_layout.visibility = View.GONE
                cl_customize_contact_layout.visibility = View.VISIBLE
            }
        }

    }
}