package com.syamsul.remotehelper.sys

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.os.SystemClock
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
        Log.d("RemoteHelper", "Key received: keyCode=${event.keyCode}")
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                // 1. YouTube -> SmartTube
                4065, KeyEvent.KEYCODE_TV -> {
                    launchPkg("org.smarttube.stable", "com.liskovsoft.smartyoutubetv2.tv.ui.main.SplashActivity")
                    return true
                }
                // 2. Netflix -> IDLIX
                190, 192, 288, KeyEvent.KEYCODE_STB_INPUT -> {
                    launchPkg("com.personalbotai.idlix", null)
                    return true
                }
                // 3. Vidio -> MovieBox / MBox
                188, 189, KeyEvent.KEYCODE_VIDEO_APP_1 -> {
                    launchPkg("com.community.mbox.tv", null)
                    return true
                }
                // 4. TCL Channel / Prime -> Blackout mode (4020)
                4020, 290, KeyEvent.KEYCODE_POWER -> {
                    val intent = Intent(this, BlackoutActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    }
                    startActivity(intent)
                    return true
                }
                // 5. WeTV -> TV Bro Browser
                291, KeyEvent.KEYCODE_CALCULATOR -> {
                    launchPkg("com.phlox.tvwebbrowser", null)
                    return true
                }
                // 6. Settings / 4077 -> Open Aerial Control Center
                4077, KeyEvent.KEYCODE_SETTINGS -> {
                    try {
                        // 1. Bring Aerial home to front
                        val intent = packageManager.getLaunchIntentForPackage("app.orchard.tv")
                        if (intent != null) {
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                            startActivity(intent)
                        }
                        // 2. Simulate DPAD_UP then DPAD_CENTER to open Control Center in Aerial
                        val inputManager = getSystemService(INPUT_SERVICE) as? android.hardware.input.InputManager
                        val injectMethod = inputManager?.javaClass?.getMethod(
                            "injectInputEvent",
                            android.view.InputEvent::class.java,
                            Int::class.javaPrimitiveType
                        )
                        if (injectMethod != null) {
                            val now = SystemClock.uptimeMillis()
                            val upDown = KeyEvent(now, now, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP, 0)
                            val upUp = KeyEvent(now, now, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_UP, 0)
                            injectMethod.invoke(inputManager, upDown, 0)
                            injectMethod.invoke(inputManager, upUp, 0)

                            SystemClock.sleep(200)

                            val centerDown = KeyEvent(now, now, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_CENTER, 0)
                            val centerUp = KeyEvent(now, now, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_CENTER, 0)
                            injectMethod.invoke(inputManager, centerDown, 0)
                            injectMethod.invoke(inputManager, centerUp, 0)
                        }
                    } catch (e: Exception) {
                        Log.e("RemoteHelper", "Failed to open Control Center", e)
                    }
                    return true
                }
            }
        }
        return super.onKeyEvent(event)
    }

    private fun launchPkg(pkg: String, cls: String?) {
        try {
            val intent = packageManager.getLaunchIntentForPackage(pkg)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else if (cls != null) {
                val fallback = Intent(Intent.ACTION_MAIN).apply {
                    setClassName(pkg, cls)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(fallback)
            }
        } catch (e: Exception) {
            Log.e("RemoteHelper", "Failed to launch $pkg", e)
        }
    }
}
