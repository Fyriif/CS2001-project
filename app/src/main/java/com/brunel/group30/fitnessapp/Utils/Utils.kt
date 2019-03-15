package com.brunel.group30.fitnessapp.Utils

import android.os.Build
import java.util.*


object Utils {
    val isEmulator: Boolean
        get() = (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || "google_sdk" == Build.PRODUCT)

    fun isDateToday(milliSeconds: Long): Boolean {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds

        val getDate = calendar.time

        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        val startDate = calendar.time

        return getDate > startDate
    }

    fun getTimeDateInMillis(): Long {
        val calendar = Calendar.getInstance()

        return calendar.timeInMillis
    }
}
