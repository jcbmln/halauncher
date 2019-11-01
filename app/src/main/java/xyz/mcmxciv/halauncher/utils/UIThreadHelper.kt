/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.mcmxciv.halauncher.utils

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Process
import android.view.inputmethod.InputMethodManager

/**
 * Utility class for offloading some class from UI thread
 */
object UiThreadHelper {

    private var sHandlerThread: HandlerThread? = null
    private var sHandler: Handler? = null

    private const val MSG_HIDE_KEYBOARD = 1
    private const val MSG_SET_ORIENTATION = 2
    private const val MSG_RUN_COMMAND = 3

    val backgroundLooper: Looper
        get() {
            if (sHandlerThread == null) {
                sHandlerThread = HandlerThread("UiThreadHelper", Process.THREAD_PRIORITY_FOREGROUND)
                sHandlerThread!!.start()
            }
            return sHandlerThread!!.looper
        }

    private fun getHandler(context: Context): Handler {
        if (sHandler == null) {
            sHandler = Handler(
                backgroundLooper,
                UiCallbacks(context.applicationContext)
            )
        }
        return sHandler as Handler
    }

    fun hideKeyboardAsync(context: Context, token: IBinder) {
        Message.obtain(getHandler(context), MSG_HIDE_KEYBOARD, token).sendToTarget()
    }

    fun setOrientationAsync(activity: Activity, orientation: Int) {
        Message.obtain(getHandler(activity), MSG_SET_ORIENTATION, orientation, 0, activity)
            .sendToTarget()
    }

    fun runAsyncCommand(context: Context, command: AsyncCommand, arg1: Int, arg2: Int) {
        Message.obtain(getHandler(context), MSG_RUN_COMMAND, arg1, arg2, command).sendToTarget()
    }

    private class UiCallbacks internal constructor(context: Context) : Handler.Callback {
        private val mIMM: InputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        override fun handleMessage(message: Message): Boolean {
            when (message.what) {
                MSG_HIDE_KEYBOARD -> {
                    mIMM.hideSoftInputFromWindow(message.obj as IBinder, 0)
                    return true
                }
                MSG_SET_ORIENTATION -> {
                    (message.obj as Activity).requestedOrientation = message.arg1
                    return true
                }
                MSG_RUN_COMMAND -> {
                    (message.obj as AsyncCommand).execute(message.arg1, message.arg2)
                    return true
                }
            }
            return false
        }
    }

    interface AsyncCommand {
        fun execute(arg1: Int, arg2: Int)
    }
}
