package com.sourate.app

import android.content.Context

object PrayerOffsets {

    private const val PREFS = "prayer_prefs"

    val PRAYERS = listOf("Fajr", "Dhouhr", "Asr", "Maghrib", "Isha")

    // Décalages par défaut adaptés au Sénégal (région de Thiès)
    private val DEFAULTS = mapOf(
        "Fajr" to 0,
        "Dhouhr" to 60,
        "Asr" to 35,
        "Maghrib" to 0,
        "Isha" to 0
    )

    private fun prefs(ctx: Context) =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun get(ctx: Context, prayer: String): Int =
        prefs(ctx).getInt("offset_$prayer", DEFAULTS[prayer] ?: 0)

    fun set(ctx: Context, prayer: String, minutes: Int) {
        prefs(ctx).edit().putInt("offset_$prayer", minutes).apply()
    }

    fun reset(ctx: Context) {
        val e = prefs(ctx).edit()
        DEFAULTS.forEach { (k, v) -> e.putInt("offset_$k", v) }
        e.apply()
    }

    fun default(prayer: String): Int = DEFAULTS[prayer] ?: 0
}
