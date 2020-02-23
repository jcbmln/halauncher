package xyz.mcmxciv.halauncher.data.repositories

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import xyz.mcmxciv.halauncher.icons.IconFactory
import xyz.mcmxciv.halauncher.models.ActivityInfo
import xyz.mcmxciv.halauncher.models.ActivityInfoDao
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class ActivityRepository @Inject constructor(
    private val activityInfoDao: ActivityInfoDao
) {
    suspend fun getActivities(): List<ActivityInfo> =
        activityInfoDao.getActivities()

    suspend fun addActivityInfo(activityInfo: ActivityInfo) {
        activityInfoDao.insert(activityInfo)
    }

//    private fun saveBitmap(name: String, bitmap: Bitmap): String {
//        val directory = context.getDir("imageDir", Context.MODE_PRIVATE)
//        val file = File(directory, "$name.jpg")
//
//        FileOutputStream(file).use { output ->
//            try {
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
//            } catch (ex: IOException) {
//                Timber.e(ex)
//            }
//        }
//
//        return file.absolutePath
//    }
}