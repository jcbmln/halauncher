package xyz.mcmxciv.halauncher.utils

import android.graphics.Rect
import android.graphics.Region
import android.graphics.RegionIterator

object GraphicsUtils {
    fun getArea(region: Region): Int {
        val iterator = RegionIterator(region)
        var area = 0
        val tempRect = Rect()

        while (iterator.next(tempRect)) {
            area += tempRect.width() * tempRect.height()
        }

        return area
    }
}