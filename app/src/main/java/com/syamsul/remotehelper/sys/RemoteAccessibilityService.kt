package com.syamsul.remotehelper.sys

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.syamsul.remotehelper.BlackoutActivity

class RemoteAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS
            notificationTimeout = 100
        }
        setServiceInfo(info)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                4065 -> {
                    // SmartTube launch
                    packageManager.getLaunchIntentForPackage("org.smarttube.stable")?.let {
                        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(it)
                    }
                    return true
                }
                4077 -> {
                    // Open Control Center in Aerial (broadcast intent or action)
                    val intent = Intent("app.orchard.tv.CONTROL_CENTER").apply {
                        setPackage("app.orchard.tv")
                    }
                    sendBroadcast(intent)
                    return true
                }
                4020 -> {
                    // Blackout mode
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
