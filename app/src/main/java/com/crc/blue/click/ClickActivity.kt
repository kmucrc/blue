package com.crc.blue.click

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.crc.blue.R
import com.crc.blue.notification.CallActivity
import kotlinx.android.synthetic.main.activity_click.*
import org.jetbrains.anko.startActivity

class ClickActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        setContentView(R.layout.activity_click)

        bt_click_camera.setOnClickListener(this)
        bt_click_record_voice.setOnClickListener(this)
        bt_click_shortcut_call.setOnClickListener(this)
        bt_click_emergency.setOnClickListener(this)
        bt_click_light_control.setOnClickListener(this)
        bt_click_music_play.setOnClickListener(this)
        bt_click_manner_mode.setOnClickListener(this)
        bt_click_find_phone.setOnClickListener(this)
        bt_click_customize.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.bt_click_camera -> {
                startActivity<CameraActivity>()
            }
            R.id.bt_click_record_voice -> {
                startActivity<RecordActivity>()
            }
            R.id.bt_click_shortcut_call -> {
                startActivity<ShortcutActivity>()
            }
            R.id.bt_click_emergency -> {
                startActivity<EmergencyActivity>()
            }
            R.id.bt_click_light_control -> {
                startActivity<LightActivity>()
            }
            R.id.bt_click_music_play -> {
                startActivity<MusicActivity>()
            }
            R.id.bt_click_manner_mode -> {
                startActivity<MannerActivity>()
            }
            R.id.bt_click_find_phone -> {
                startActivity<FindphoneActivity>()
            }
            R.id.bt_click_customize -> {
                startActivity<CustomizeActivity>()
            }
        }
    }

}