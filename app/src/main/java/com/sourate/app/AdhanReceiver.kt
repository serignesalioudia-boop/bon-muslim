package com.sourate.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class AdhanReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val prayer = intent.getStringExtra("PRAYER") ?: "Prière"
        showNotification(context, prayer)
        if (AdhanSettings.isSoundEnabled(context)) {
            try {
                val service = Intent(context, AdhanService::class.java).putExtra("PRAYER", prayer)
                ContextCompat.startForegroundService(context, service)
            } catch (_: Exception) {
            }
        }
        PrayerNotificationScheduler.scheduleNext(context)
    }

    private fun showNotification(context: Context, prayer: String) {
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "adhan_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Horaires de prières",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications aux heures des cinq prières"
            }
            manager.createNotificationChannel(channel)
        }

        val openIntent = Intent(context, PrayerTimesActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val contentIntent = PendingIntent.getActivity(context, 0, openIntent, flags)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_stat_adhan)
            .setContentTitle("Il est l'heure de prier")
            .setContentText("C'est l'heure de la prière : $prayer")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .setColor(context.resources.getColor(R.color.primary))
            .build()

        manager.notify(prayer.hashCode(), notification)
    }
}
