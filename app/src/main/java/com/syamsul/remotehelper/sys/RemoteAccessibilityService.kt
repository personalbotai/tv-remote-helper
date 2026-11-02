package com.syamsul.remotehelper.sys

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.syamsul.remotehelper.BlackoutActivity

class RemoteAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("RemoteHelper", "RemoteAccessibilityService connected!")
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS
            notificationTimeout = 100
        }
        setServiceInfo(info)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {
        Log.d("RemoteHelper", "RemoteAccessibilityService interrupted!")
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        Log.d("RemoteHelper", "onKeyEvent received: keyCode=${event.keyCode}, action=${event.action}")
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                4065 -> {
                    Log.d("RemoteHelper", "Key 4065 detected -> Opening SmartTube")
                    packageManager.getLaunchIntentForPackage("org.smarttube.stable")?.let {
                        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(it)
                    }
                    return true
                }
                4077 -> {
                    Log.d("RemoteHelper", "Key 4077 detected -> Opening Control Center")
                    val intent = Intent("app.orchard.tv.CONTROL_CENTER").apply {
                        setPackage("app.orchard.tv")
                    }
                    sendBroadcast(intent)
                    return true
                }
                4020 -> {
                    Log.d("RemoteHelper", "Key 4020 detected -> Triggering Blackout")
                    val intent = Intent(this, BlackoutActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    }
                    startActivity(intent)
                    return true
                }
            }
        }
        return super.onKeyEvent(event)
    }
}
