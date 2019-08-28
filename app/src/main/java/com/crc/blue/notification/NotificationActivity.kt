package com.crc.blue.notification

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.telecom.Call
import android.view.View
import com.crc.blue.R
import kotlinx.android.synthetic.main.activity_notification.*
import org.jetbrains.anko.startActivity

class NotificationActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        setContentView(R.layout.activity_notification)

        bt_noti_call.setOnClickListener(this)
        bt_noti_message.setOnClickListener(this)
        bt_noti_restaurant.setOnClickListener(this)
        bt_noti_attraction.setOnClickListener(this)
        bt_noti_reminders.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.bt_noti_call -> {
                startActivity<CallActivity>()
            }
            R.id.bt_noti_message -> {
                startActivity<MessageActivity>()
            }
            R.id.bt_noti_restaurant -> {
                startActivity<RestaurantActivity>()
            }
            R.id.bt_noti_attraction -> {
                startActivity<AttractionActivity>()
            }
            R.id.bt_noti_reminders -> {
                startActivity<ReminderActivity>()
            }
        }
    }

}