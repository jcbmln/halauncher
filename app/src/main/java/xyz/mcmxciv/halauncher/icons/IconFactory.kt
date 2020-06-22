package xyz.mcmxciv.halauncher.icons

import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PaintFlagsDrawFilter
import android.graphics.Rect
import xyz.mcmxciv.halauncher.device.DeviceProfile
import xyz.mcmxciv.halauncher.di.AppScope
import xyz.mcmxciv.halauncher.utils.ResourceProvider
import javax.inject.Inject

@AppScope
class IconFactory @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val packageManager: PackageManager,
    private val deviceProfile: DeviceProfile,
    private val launcherApps: LauncherApps
) {
    private var wrapperBackgroundColor = DEFAULT_WRAPPER_BACKGROUND
    private val canvas = Canvas()
    private val oldBounds = Rect()

    init {
        canvas.drawFilter = PaintFlagsDrawFilter(Paint.DITHER_FLAG, Paint.FILTER_BITMAP_FLAG)
    }



    companion object {
        private const val DEFAULT_WRAPPER_BACKGROUND = Color.WHITE
        private const val BLUR_FACTOR = 0.5F / 48
    }
}
