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
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                4065 -> {
                    Log.d("RemoteHelper", "Key 4065 detected -> Opening SmartTube")
                    try {
                        val intent = packageManager.getLaunchIntentForPackage("org.smarttube.stable")
                        if (intent != null) {
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        } else {
                            val fallback = Intent(Intent.ACTION_MAIN).apply {
                                setClassName("org.smarttube.stable", "com.liskovsoft.smartyoutubetv2.tv.ui.main.SplashActivity")
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            startActivity(fallback)
                        }
                    } catch (e: Exception) {
                        Log.e("RemoteHelper", "Failed to launch SmartTube", e)
                    }
                    return true
                }
                4020 -> {
                    Log.d("RemoteHelper", "Key 4020 detected -> Triggering Blackout")
                    try {
                        val intent = Intent(this, BlackoutActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        }
                        startActivity(intent)
                    } catch (e: Exception) {
                        Log.e("RemoteHelper", "Failed to start BlackoutActivity", e)
                    }
                    return true
                }
            }
        }
        return super.onKeyEvent(event)
    }
}
