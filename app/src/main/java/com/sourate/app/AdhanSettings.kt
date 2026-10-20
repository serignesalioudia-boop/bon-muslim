package com.sourate.app

import android.content.Context
import android.net.Uri
import java.io.File

object AdhanSettings {

    private const val PREFS = "prayer_prefs"
    private const val KEY_SOUND = "adhan_sound"

    private fun prefs(ctx: Context) =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun isSoundEnabled(ctx: Context): Boolean =
        prefs(ctx).getBoolean(KEY_SOUND, true)

    fun setSoundEnabled(ctx: Context, enabled: Boolean) {
        prefs(ctx).edit().putBoolean(KEY_SOUND, enabled).apply()
    }

    fun customFile(ctx: Context): File = File(ctx.filesDir, "adhan_custom.dat")

    fun hasCustom(ctx: Context): Boolean =
        customFile(ctx).let { it.exists() && it.length() > 0 }

    fun setCustom(ctx: Context, uri: Uri): Boolean {
        return try {
            val tmp = File(ctx.filesDir, "adhan_custom.tmp")
            ctx.contentResolver.openInputStream(uri)?.use { input ->
                tmp.outputStream().use { output -> input.copyTo(output) }
            } ?: return false
            val dest = customFile(ctx)
            if (dest.exists()) dest.delete()
            tmp.renameTo(dest)
            dest.exists() && dest.length() > 0
        } catch (e: Exception) {
            false
        }
    }

    fun clearCustom(ctx: Context) {
        customFile(ctx).delete()
    }
}
