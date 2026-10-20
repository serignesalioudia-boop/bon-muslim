package com.sourate.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat

class AdhanService : Service() {

    companion object {
        const val ACTION_STOP = "com.sourate.app.action.STOP_ADHAN"
        private const val CHANNEL_ID = "adhan_play_channel"
        private const val NOTIF_ID = 4242
    }

    private var player: MediaPlayer? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopSelf()
            return START_NOT_STICKY
        }
        val prayer = intent?.getStringExtra("PRAYER") ?: "Prière"
        ServiceCompat.startForeground(
            this,
            NOTIF_ID,
            buildNotification(prayer),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK else 0
        )
        play()
        return START_NOT_STICKY
    }

    private fun play() {
        player?.release()
        player = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            try {
                if (AdhanSettings.hasCustom(this@AdhanService)) {
                    setDataSource(AdhanSettings.customFile(this@AdhanService).absolutePath)
                } else {
                    val afd = resources.openRawResourceFd(R.raw.adhan_default)
                    setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                    afd.close()
                }
                setOnCompletionListener { stopSelf() }
                setOnErrorListener { _, _, _ ->
                    stopSelf()
                    true
                }
                prepare()
                start()
            } catch (e: Exception) {
                stopSelf()
            }
        }
    }

    override fun onDestroy() {
        player?.release()
        player = null
        super.onDestroy()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mgr = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Appel à la prière (Adhan)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Lecture de l'adhan à l'heure de la prière"
            }
            mgr.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(prayer: String): Notification {
        val stopPi = PendingIntent.getService(
            this,
            1,
            Intent(this, AdhanService::class.java).setAction(ACTION_STOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val openPi = PendingIntent.getActivity(
            this,
            0,
            Intent(this, PrayerTimesActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_adhan)
            .setContentTitle("Il est l'heure de prier")
            .setContentText("$prayer — écoutez l'appel à la prière")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setContentIntent(openPi)
            .addAction(0, "Arrêter", stopPi)
            .build()
    }
}
