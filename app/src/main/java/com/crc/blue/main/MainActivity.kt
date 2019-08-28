package com.crc.blue.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.crc.blue.R
import com.crc.blue.click.ClickActivity
import com.crc.blue.bluetooth.ConnectionActivity
import com.crc.blue.help.HelpActivity
import com.crc.blue.notification.NotificationActivity
import com.crc.blue.preferences.PreferencesActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        setContentView(R.layout.activity_main)

        bt_main_connection.setOnClickListener(this)
        bt_main_notification.setOnClickListener(this)
        bt_main_click.setOnClickListener(this)
        bt_main_preferences.setOnClickListener(this)
        bt_main_help_review.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.bt_main_connection -> {
                startActivity<ConnectionActivity>()
            }
            R.id.bt_main_notification -> {
                startActivity<NotificationActivity>()
            }
            R.id.bt_main_click -> {
                startActivity<ClickActivity>()
            }
            R.id.bt_main_help_review -> {
                startActivity<HelpActivity>()
            }
            R.id.bt_main_preferences -> {
                startActivity<PreferencesActivity>()
            }
        }

    }

}
