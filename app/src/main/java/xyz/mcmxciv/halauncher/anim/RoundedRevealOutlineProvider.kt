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

package xyz.mcmxciv.halauncher.anim

import android.graphics.Rect

/**
 * A [RevealOutlineAnimation] that provides an outline that interpolates between two radii
 * and two [Rect]s.
 *
 * An example usage of this provider is an outline that starts out as a circle and ends
 * as a rounded rectangle.
 */
open class RoundedRectRevealOutlineProvider(
    private val startRadius: Float, private val endRadius: Float, private val startRect: Rect,
    private val endRect: Rect
) : RevealOutlineAnimation() {

    override fun shouldRemoveElevationDuringAnimation(): Boolean {
        return false
    }

    override fun setProgress(progress: Float) {
        radius = (1 - progress) * startRadius + progress * endRadius

        outline.left = ((1 - progress) * startRect.left + progress * endRect.left).toInt()
        outline.top = ((1 - progress) * startRect.top + progress * endRect.top).toInt()
        outline.right = ((1 - progress) * startRect.right + progress * endRect.right).toInt()
        outline.bottom = ((1 - progress) * startRect.bottom + progress * endRect.bottom).toInt()
    }
}
