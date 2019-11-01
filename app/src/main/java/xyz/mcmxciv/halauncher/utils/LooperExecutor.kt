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
 * limitations under the License
 */

package xyz.mcmxciv.halauncher.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.TimeUnit

/**
 * Extension of [AbstractExecutorService] which executed on a provided looper.
 */
open class LooperExecutor(looper: Looper) : AbstractExecutorService() {

    private val handler: Handler = Handler(looper)

    override fun execute(runnable: Runnable) {
        if (handler.looper == Looper.myLooper()) {
            runnable.run()
        } else {
            handler.post(runnable)
        }
    }

    /**
     * Not supported and throws an exception when used.
     */
    @Deprecated("", ReplaceWith("throw UnsupportedOperationException()"))
    override fun shutdown() {
        throw UnsupportedOperationException()
    }

    /**
     * Not supported and throws an exception when used.
     */
    @Deprecated("", ReplaceWith("throw UnsupportedOperationException()"))
    override fun shutdownNow(): List<Runnable> {
        throw UnsupportedOperationException()
    }

    override fun isShutdown(): Boolean {
        return false
    }

    override fun isTerminated(): Boolean {
        return false
    }

    /**
     * Not supported and throws an exception when used.
     */
    @Deprecated("", ReplaceWith("throw UnsupportedOperationException()"))
    @Throws(InterruptedException::class)
    override fun awaitTermination(l: Long, timeUnit: TimeUnit): Boolean {
        throw UnsupportedOperationException()
    }
}
