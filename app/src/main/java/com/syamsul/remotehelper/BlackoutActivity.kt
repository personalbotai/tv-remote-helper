package com.syamsul.remotehelper

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager

class BlackoutActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        val view = View(this).apply {
            setBackgroundColor(0xFF000000.toInt())
            isFocusable = true
            isFocusableInTouchMode = true
        }
        setContentView(view)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Any key press exits blackout mode
        finish()
        return true
    }
}
