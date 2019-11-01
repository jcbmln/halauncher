/*
 * Copyright (C) 2008 The Android Open Source Project
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

package xyz.mcmxciv.halauncher

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.Surface
import android.view.WindowManager
import xyz.mcmxciv.halauncher.utils.Utilities

//import com.android.launcher3.CellLayout.ContainerType
//import com.android.launcher3.graphics.IconShape
//import com.android.launcher3.icons.DotRenderer
//import com.android.launcher3.icons.IconNormalizer
import kotlin.math.max
import kotlin.math.min

class DeviceProfile(
    context: Context, private val inv: InvariantDeviceProfile,
    minSize: Point, maxSize: Point, private val widthPx: Int, private val heightPx: Int,
    private val isLandscape: Boolean, private val isMultiWindowMode: Boolean
) {
    // Device properties
    val isTablet: Boolean
    val isLargeTablet: Boolean
    val isPhone: Boolean
    val transposeLayoutWithOrientation: Boolean
    val availableWidthPx: Int
    val availableHeightPx: Int

    val aspectRatio: Float

    // Workspace
    val desiredWorkspaceLeftRightMarginPx: Int
    val cellLayoutPaddingLeftRightPx: Int
    val cellLayoutBottomPaddingPx: Int
    val edgeMarginPx: Int
    var workspaceSpringLoadShrinkFactor: Float = 0.toFloat()
    val workspaceSpringLoadedBottomSpace: Int

    // Drag handle
    val verticalDragHandleSizePx: Int
    private val verticalDragHandleOverlapWorkspace: Int

    // Workspace icons
    var iconSizePx: Int = 0
    var iconTextSizePx: Int = 0
    var iconDrawablePaddingPx: Int = 0
    var iconDrawablePaddingOriginalPx: Int = 0

    var cellWidthPx: Int = 0
    var cellHeightPx: Int = 0
    var workspaceCellPaddingXPx: Int = 0

    // All apps
    var allAppsCellHeightPx: Int = 0
    var allAppsIconSizePx: Int = 0
    var allAppsIconDrawablePaddingPx: Int = 0
    var allAppsIconTextSizePx: Float = 0.toFloat()

    // Drop Target
    var dropTargetBarSizePx: Int = 0

    // Insets
    /**
     * The current device insets. This is generally same as the insets being dispatched to
     * [Insettable] elements, but can differ if the element is using a different profile.
     */
    val insets = Rect()
    val workspacePadding = Rect()
    // When true, nav bar is on the left side of the screen.
    private var mIsSeascape: Boolean = false

    /**
     * Inverse of [.getMultiWindowProfile]
     * @return device profile corresponding to the current orientation in non multi-window mode.
     */
    val fullScreenProfile: DeviceProfile
        get() = if (isLandscape) inv.landscapeProfile else inv.portraitProfile

    // Since we are only concerned with the overall padding, layout direction does
    // not matter.
    val cellSize: Point
        get() {
            val result = Point()
            val padding = totalWorkspacePadding
            result.x = calculateCellWidth(
                availableWidthPx - padding.x
                        - cellLayoutPaddingLeftRightPx * 2, inv.numColumns
            )
            result.y = calculateCellHeight(
                (availableHeightPx - padding.y
                        - cellLayoutBottomPaddingPx), inv.numColumns
            )
            return result
        }

    val totalWorkspacePadding: Point
        get() {
            updateWorkspacePadding()
            return Point(
                workspacePadding.left + workspacePadding.right,
                workspacePadding.top + workspacePadding.bottom
            )
        }

    /**
     * When `true`, the device is in landscape mode and the hotseat is on the right column.
     * When `false`, either device is in portrait mode or the device is in landscape mode and
     * the hotseat is on the bottom row.
     */
    val isVerticalBarLayout: Boolean
        get() = isLandscape && transposeLayoutWithOrientation

    val isSeascape: Boolean
        get() = isVerticalBarLayout && mIsSeascape

    init {
        var orientationContext = context

        if (isLandscape) {
            availableWidthPx = maxSize.x
            availableHeightPx = minSize.y
        } else {
            availableWidthPx = minSize.x
            availableHeightPx = maxSize.y
        }

        var res = orientationContext.resources
        val dm = res.displayMetrics

        // Constants from resources
        isTablet = res.getBoolean(R.bool.is_tablet)
        isLargeTablet = res.getBoolean(R.bool.is_large_tablet)
        isPhone = !isTablet && !isLargeTablet

        aspectRatio = (max(widthPx, heightPx).toFloat()) / min(widthPx, heightPx)
        val isTallDevice =
            aspectRatio.compareTo(TALL_DEVICE_ASPECT_RATIO_THRESHOLD) >= 0

        // Some more constants
        transposeLayoutWithOrientation =
            res.getBoolean(R.bool.hotseat_transpose_layout_with_orientation)

        orientationContext = getContext(
            context, if (isVerticalBarLayout)
                Configuration.ORIENTATION_LANDSCAPE
            else
                Configuration.ORIENTATION_PORTRAIT
        )
        res = orientationContext.resources

        edgeMarginPx = res.getDimensionPixelSize(R.dimen.dynamic_grid_edge_margin)
        desiredWorkspaceLeftRightMarginPx = if (isVerticalBarLayout) 0 else edgeMarginPx

        val cellLayoutPaddingLeftRightMultiplier = if (!isVerticalBarLayout && isTablet)
                PORTRAIT_TABLET_LEFT_RIGHT_PADDING_MULTIPLIER
            else
                1
        val cellLayoutPadding = res.getDimensionPixelSize(R.dimen.dynamic_grid_cell_layout_padding)

        if (isLandscape) {
            cellLayoutPaddingLeftRightPx = 0
            cellLayoutBottomPaddingPx = cellLayoutPadding
        }
        else {
            cellLayoutPaddingLeftRightPx = cellLayoutPaddingLeftRightMultiplier * cellLayoutPadding
            cellLayoutBottomPaddingPx = 0
        }

        verticalDragHandleSizePx = res.getDimensionPixelSize(R.dimen.vertical_drag_handle_size)
        verticalDragHandleOverlapWorkspace =
            res.getDimensionPixelSize(R.dimen.vertical_drag_handle_overlap_workspace)
        iconDrawablePaddingOriginalPx =
            res.getDimensionPixelSize(R.dimen.dynamic_grid_icon_drawable_padding)
        dropTargetBarSizePx = res.getDimensionPixelSize(R.dimen.dynamic_grid_drop_target_size)
        workspaceSpringLoadedBottomSpace =
            res.getDimensionPixelSize(R.dimen.dynamic_grid_min_spring_loaded_space)

        workspaceCellPaddingXPx = res.getDimensionPixelSize(R.dimen.dynamic_grid_cell_padding_x)

        // Calculate all of the remaining variables.
        updateAvailableDimensions(dm, res)
        updateWorkspacePadding()
    }

    fun copy(context: Context): DeviceProfile {
        val size = Point(availableWidthPx, availableHeightPx)
        return DeviceProfile(
            context, inv, size, size, widthPx, heightPx, isLandscape,
            isMultiWindowMode
        )
    }

    fun getMultiWindowProfile(context: Context, mwSize: Point): DeviceProfile {
        // We take the minimum sizes of this profile and it's multi-window variant to ensure that
        // the system decor is always excluded.
        mwSize.set(min(availableWidthPx, mwSize.x), min(availableHeightPx, mwSize.y))

        // In multi-window mode, we can have widthPx = availableWidthPx
        // and heightPx = availableHeightPx because Launcher uses the InvariantDeviceProfiles'
        // widthPx and heightPx values where it's needed.
        val profile = DeviceProfile(
            context, inv, mwSize, mwSize, mwSize.x, mwSize.y,
            isLandscape, true
        )

        // If there isn't enough vertical cell padding with the labels displayed, hide the labels.
        val workspaceCellPaddingY = (profile.cellSize.y - profile.iconSizePx
                - iconDrawablePaddingPx - profile.iconTextSizePx).toFloat()
        if (workspaceCellPaddingY < profile.iconDrawablePaddingPx * 2) {
            profile.adjustToHideWorkspaceLabels()
        }

        profile.updateWorkspacePadding()

        return profile
    }

    /**
     * Adjusts the profile so that the labels on the Workspace are hidden.
     * It is important to call this method after the All Apps variables have been set.
     */
    private fun adjustToHideWorkspaceLabels() {
        iconTextSizePx = 0
        iconDrawablePaddingPx = 0
        cellHeightPx = iconSizePx

        // In normal cases, All Apps cell height should equal the Workspace cell height.
        // Since we are removing labels from the Workspace, we need to manually compute the
        // All Apps cell height.
        val topBottomPadding = allAppsIconDrawablePaddingPx * (if (isVerticalBarLayout) 2 else 1)
        allAppsCellHeightPx = (allAppsIconSizePx + allAppsIconDrawablePaddingPx
                + Utilities.calculateTextHeight(allAppsIconTextSizePx)
                + topBottomPadding * 2)
    }

    private fun updateAvailableDimensions(dm: DisplayMetrics, res: Resources) {
        updateIconSize(1f, res, dm)

        // Check to see if the icons fit within the available height.  If not, then scale down.
        val usedHeight = (cellHeightPx * inv.numColumns)
        val maxHeight = (availableHeightPx - totalWorkspacePadding.y)
        if (usedHeight > maxHeight) {
            val scale = maxHeight / usedHeight
            updateIconSize(scale.toFloat(), res, dm)
        }
    }

    private fun updateIconSize(scale: Float, res: Resources, dm: DisplayMetrics) {
        // Workspace
        val isVerticalLayout = isVerticalBarLayout
        val invIconSizePx = if (isVerticalLayout) inv.landscapeIconSize else inv.iconSize
        iconSizePx = max(1, (Utilities.pxFromDp(invIconSizePx, dm) * scale).toInt())
        iconTextSizePx = (Utilities.pxFromSp(inv.iconTextSize, dm) * scale).toInt()
        iconDrawablePaddingPx = (iconDrawablePaddingOriginalPx * scale).toInt()

        cellHeightPx = (iconSizePx + iconDrawablePaddingPx
                + Utilities.calculateTextHeight(iconTextSizePx.toFloat()))
        val cellYPadding = (cellSize.y - cellHeightPx) / 2
        if ((iconDrawablePaddingPx > cellYPadding && !isVerticalLayout
                    && !isMultiWindowMode)
        ) {
            // Ensures that the label is closer to its corresponding icon. This is not an issue
            // with vertical bar layout or multi-window mode since the issue is handled separately
            // with their calls to {@link #adjustToHideWorkspaceLabels}.
            cellHeightPx -= (iconDrawablePaddingPx - cellYPadding)
            iconDrawablePaddingPx = cellYPadding
        }
        cellWidthPx = iconSizePx + iconDrawablePaddingPx

        // All apps
        allAppsIconTextSizePx = iconTextSizePx.toFloat()
        allAppsIconSizePx = iconSizePx
        allAppsIconDrawablePaddingPx = iconDrawablePaddingPx
        allAppsCellHeightPx = cellSize.y

        if (isVerticalLayout) {
            // Always hide the Workspace text with vertical bar layout.
            adjustToHideWorkspaceLabels()
        }

        workspaceSpringLoadShrinkFactor = if (!isVerticalLayout) {
            val expectedWorkspaceHeight = (availableHeightPx -
                    verticalDragHandleSizePx - edgeMarginPx)
            val minRequiredHeight =
                (dropTargetBarSizePx + workspaceSpringLoadedBottomSpace).toFloat()
            min(
                res.getInteger(R.integer.config_workspaceSpringLoadShrinkPercentage) / 100.0f,
                1 - (minRequiredHeight / expectedWorkspaceHeight)
            )
        } else {
            res.getInteger(R.integer.config_workspaceSpringLoadShrinkPercentage) / 100.0f
        }
    }

    fun updateInsets(insets: Rect) {
        this.insets.set(insets)
        updateWorkspacePadding()
    }

    /**
     * Updates [.workspacePadding] as a result of any internal value change to reflect the
     * new workspace padding
     */
    private fun updateWorkspacePadding() {
        val padding = workspacePadding
        if (isVerticalBarLayout) {
            padding.top = 0
            padding.bottom = edgeMarginPx
            if (isSeascape) {
                padding.left = 0
                padding.right = verticalDragHandleSizePx
            } else {
                padding.left = verticalDragHandleSizePx
                padding.right = 0
            }
        } else {
            val paddingBottom =
                (verticalDragHandleSizePx - verticalDragHandleOverlapWorkspace)
            if (isTablet) {
                // Pad the left and right of the workspace to ensure consistent spacing
                // between all icons
                // The amount of screen space available for left/right padding.
                var availablePaddingX = max(
                    0,
                    (widthPx - (((inv.numColumns * cellWidthPx) + ((inv.numColumns - 1) * cellWidthPx))))
                )
                availablePaddingX = min(
                    availablePaddingX.toFloat(),
                    widthPx * MAX_HORIZONTAL_PADDING_PERCENT
                ).toInt()
                val availablePaddingY = max(
                    0, (heightPx - edgeMarginPx - paddingBottom
                            - (2 * cellHeightPx))
                )
                padding.set(
                    availablePaddingX / 2, edgeMarginPx + availablePaddingY / 2,
                    availablePaddingX / 2, paddingBottom + availablePaddingY / 2
                )
            } else {
                // Pad the top and bottom of the workspace with search/hotseat bar sizes
                padding.set(
                    desiredWorkspaceLeftRightMarginPx,
                    edgeMarginPx,
                    desiredWorkspaceLeftRightMarginPx,
                    paddingBottom
                )
            }
        }
    }

    /**
     * Updates orientation information and returns true if it has changed from the previous value.
     */
    fun updateIsSeascape(wm: WindowManager): Boolean {
        if (isVerticalBarLayout) {
            val isSeascape = wm.defaultDisplay.rotation == Surface.ROTATION_270
            if (mIsSeascape != isSeascape) {
                mIsSeascape = isSeascape
                return true
            }
        }
        return false
    }

    fun shouldFadeAdjacentWorkspaceScreens(): Boolean {
        return isVerticalBarLayout || isLargeTablet
    }

//    fun getCellHeight(@ContainerType containerType: Int): Int {
//        when (containerType) {
//            CellLayout.WORKSPACE -> return cellHeightPx
//            else ->
//                // ??
//                return 0
//        }
//    }

    /**
     * Callback when a component changes the DeviceProfile associated with it, as a result of
     * configuration change
     */
    interface OnDeviceProfileChangeListener {

        /**
         * Called when the device profile is reassigned. Note that for layout and measurements, it
         * is sufficient to listen for inset changes. Use this callback when you need to perform
         * a one time operation.
         */
        fun onDeviceProfileChanged(dp: DeviceProfile)
    }

    companion object {

        /**
         * The maximum amount of left/right workspace padding as a percentage of the screen width.
         * To be clear, this means that up to 7% of the screen width can be used as left padding, and
         * 7% of the screen width can be used as right padding.
         */
        private const val MAX_HORIZONTAL_PADDING_PERCENT = 0.14f
        private const val TALL_DEVICE_ASPECT_RATIO_THRESHOLD = 2.0f

        // To evenly space the icons, increase the left/right margins for tablets in portrait mode.
        private const val PORTRAIT_TABLET_LEFT_RIGHT_PADDING_MULTIPLIER = 4

        fun calculateCellWidth(width: Int, countX: Int): Int {
            return width / countX
        }

        fun calculateCellHeight(height: Int, countY: Int): Int {
            return height / countY
        }

        private fun getContext(c: Context, orientation: Int): Context {
            val context = Configuration(c.resources.configuration)
            context.orientation = orientation
            return c.createConfigurationContext(context)
        }
    }
}
