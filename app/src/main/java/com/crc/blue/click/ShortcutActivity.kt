package com.crc.blue.click

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.crc.blue.R
import kotlinx.android.synthetic.main.activity_shortcut.*
import org.jetbrains.anko.toast

class ShortcutActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        setContentView(R.layout.activity_shortcut)

        toast(R.string.str_currently_unavailable)

        rb_shortcut_call.setOnClickListener(this)
        rb_shortcut_contact.setOnClickListener(this)
        cl_shortcut_call_layout.visibility = View.VISIBLE
        cl_shortcut_contact_layout.visibility = View.GONE

    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.rb_shortcut_call -> {
                cl_shortcut_call_layout.visibility = View.VISIBLE
                cl_shortcut_contact_layout.visibility = View.GONE
            }
            R.id.rb_shortcut_contact -> {
                cl_shortcut_call_layout.visibility = View.GONE
                cl_shortcut_contact_layout.visibility = View.VISIBLE
            }
        }

    }

}