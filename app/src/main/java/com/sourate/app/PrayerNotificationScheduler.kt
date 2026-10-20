package com.sourate.app

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.Madhab
import com.batoulapps.adhan.PrayerTimes
import com.batoulapps.adhan.data.DateComponents
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

object PrayerNotificationScheduler {

    private const val PREFS = "prayer_prefs"
    private const val KEY_ENABLED = "notif_enabled"
    private const val KEY_LAT = "lat"
    private const val KEY_LNG = "lng"
    private const val REQ = 9001

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun setEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_ENABLED, enabled).apply()
        if (enabled) scheduleNext(context) else cancel(context)
    }

    fun isEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_ENABLED, false)

    fun saveLocation(context: Context, lat: Double, lng: Double) {
        prefs(context).edit().putFloat(KEY_LAT, lat.toFloat())
            .putFloat(KEY_LNG, lng.toFloat()).apply()
    }

    fun hasLocation(context: Context): Boolean =
        prefs(context).contains(KEY_LAT) && prefs(context).contains(KEY_LNG)

    fun getLocation(context: Context): Pair<Double, Double>? {
        val p = prefs(context)
        if (!p.contains(KEY_LAT)) return null
        return p.getFloat(KEY_LAT, Defaults.LATITUDE.toFloat()).toDouble() to
            p.getFloat(KEY_LNG, Defaults.LONGITUDE.toFloat()).toDouble()
    }

    fun scheduleNext(context: Context) {
        if (!isEnabled(context)) return
        val loc = getLocation(context) ?: (Defaults.LATITUDE to Defaults.LONGITUDE)
        val coordinates = Coordinates(loc.first, loc.second)
        val params = CalculationMethod.MUSLIM_WORLD_LEAGUE.parameters
        params.madhab = Madhab.SHAFI
        val tz = TimeZone.getTimeZone(Defaults.TIMEZONE)
        val cal = Calendar.getInstance(tz)
        val today = PrayerTimes(coordinates, DateComponents(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)), params)
        val now = cal.time
        val candidates = listOf(
            "Fajr" to today.fajr,
            "Dhouhr" to today.dhuhr,
            "Asr" to today.asr,
            "Maghrib" to today.maghrib,
            "Isha" to today.isha
        )
        var next = candidates.firstOrNull { it.second.after(now) }
        if (next == null) {
            val cal2 = Calendar.getInstance(tz)
            cal2.add(Calendar.DAY_OF_YEAR, 1)
            val tmr = PrayerTimes(coordinates, DateComponents(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH) + 1, cal2.get(Calendar.DAY_OF_MONTH)), params)
            next = "Fajr" to tmr.fajr
        }
        scheduleAt(context, next.first, next.second)
    }

    private fun scheduleAt(context: Context, prayer: String, time: Date) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AdhanReceiver::class.java).putExtra("PRAYER", prayer)
        val piFlags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pi = PendingIntent.getBroadcast(context, REQ, intent, piFlags)
        val openIntent = Intent(context, PrayerTimesActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val showIntent = PendingIntent.getActivity(context, REQ + 1, openIntent, piFlags)

        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || am.canScheduleExactAlarms()) {
                // AlarmClock = alarme visible par l'utilisateur : exemptée du Doze et
                // autorisée à démarrer un service au premier plan depuis l'arrière-plan.
                am.setAlarmClock(AlarmManager.AlarmClockInfo(time.time, showIntent), pi)
            } else {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time.time, pi)
            }
        } catch (e: SecurityException) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time.time, pi)
        }
    }

    fun cancel(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AdhanReceiver::class.java)
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pi = PendingIntent.getBroadcast(context, REQ, intent, flags)
        am.cancel(pi)
    }
}
