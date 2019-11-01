/**
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.mcmxciv.halauncher.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Point
import android.hardware.display.DisplayManager
import android.hardware.display.DisplayManager.DisplayListener
import android.os.Handler
import android.util.Log
import android.view.Display
import android.view.WindowManager

//import com.android.launcher3.MainThreadExecutor

/**
 * [BroadcastReceiver] which watches configuration changes and
 * notifies the callback in case changes which affect the device profile occur.
 */
class ConfigMonitor(private val mContext: Context,
                    private var callback: (context: Context) -> Unit) :
    BroadcastReceiver(), DisplayListener {

    private val mTmpPoint1 = Point()
    private val mTmpPoint2 = Point()
    private val mFontScale: Float
    private val mDensity: Int

    private val mDisplayId: Int
    private val mRealSize: Point
    private val mSmallestSize: Point
    private val mLargestSize: Point

    init {

        val config = mContext.resources.configuration
        mFontScale = config.fontScale
        mDensity = config.densityDpi

        val display = getDefaultDisplay(mContext)
        mDisplayId = display.displayId

        mRealSize = Point()
        display.getRealSize(mRealSize)

        mSmallestSize = Point()
        mLargestSize = Point()
        display.getCurrentSizeRange(mSmallestSize, mLargestSize)

        // Listen for configuration change
        mContext.registerReceiver(this, IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED))

        // Listen for display manager change
        mContext.getSystemService(DisplayManager::class.java)!!
            .registerDisplayListener(this, Handler(UiThreadHelper.backgroundLooper))
    }

    override fun onReceive(context: Context, intent: Intent) {
        val config = context.resources.configuration
        if (mFontScale != config.fontScale || mDensity != config.densityDpi) {
            Log.d(TAG, "Configuration changed.")
            notifyChange()
        }
    }

    override fun onDisplayAdded(displayId: Int) {}

    override fun onDisplayRemoved(displayId: Int) {}

    override fun onDisplayChanged(displayId: Int) {
        if (displayId != mDisplayId) {
            return
        }
        val display = getDefaultDisplay(mContext)
        display.getRealSize(mTmpPoint1)

        if (mRealSize != mTmpPoint1 && !mRealSize.equals(mTmpPoint1.y, mTmpPoint1.x)) {
            Log.d(TAG, String.format("Display size changed from %s to %s", mRealSize, mTmpPoint1))
            notifyChange()
            return
        }

        display.getCurrentSizeRange(mTmpPoint1, mTmpPoint2)
        if (mSmallestSize != mTmpPoint1 || mLargestSize != mTmpPoint2) {
            Log.d(
                TAG, String.format(
                    "Available size changed from [%s, %s] to [%s, %s]",
                    mSmallestSize, mLargestSize, mTmpPoint1, mTmpPoint2
                )
            )
            notifyChange()
        }
    }

    @Synchronized
    private fun notifyChange() {
        MainThreadExecutor().execute { callback }
    }

    private fun getDefaultDisplay(context: Context): Display {
        return context.getSystemService(WindowManager::class.java)!!.defaultDisplay
    }

//    fun unregister() {
//        try {
//            mContext.unregisterReceiver(this)
//            mContext.getSystemService(DisplayManager::class.java)!!.unregisterDisplayListener(this)
//        } catch (e: Exception) {
//            Log.e(TAG, "Failed to unregister config monitor", e)
//        }
//
//    }

    companion object {
        private const val TAG = "ConfigMonitor"
    }
}
