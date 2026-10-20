package com.sourate.app

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

object AudioRepository {

    private const val BASE =
        "https://server11.mp3quran.net/afs"

    private val executor = Executors.newSingleThreadExecutor()

    fun audioFile(context: Context, surah: Int): File {
        val dir = File(context.filesDir, "audio")
        if (!dir.exists()) dir.mkdirs()
        return File(dir, "surah_$surah.mp3")
    }

    fun audioUrl(surah: Int): String =
        "$BASE/${surah.toString().padStart(3, '0')}.mp3"

    fun isDownloaded(context: Context, surah: Int): Boolean =
        audioFile(context, surah).exists() && audioFile(context, surah).length() > 0

    fun download(
        context: Context,
        surah: Int,
        onProgress: (Int) -> Unit,
        onResult: (Boolean) -> Unit
    ) {
        executor.execute {
            val dest = audioFile(context, surah)
            val tmp = File(dest.parentFile, dest.name + ".part")
            var conn: HttpURLConnection? = null
            try {
                val url = URL(audioUrl(surah))
                conn = (url.openConnection() as HttpURLConnection).apply {
                    connectTimeout = 30000
                    readTimeout = 60000
                    connect()
                }
                val total = conn.contentLength
                conn.inputStream.use { input ->
                    FileOutputStream(tmp).use { output ->
                        val buffer = ByteArray(8192)
                        var read: Int
                        var done = 0L
                        while (input.read(buffer).also { read = it } != -1) {
                            output.write(buffer, 0, read)
                            done += read
                            if (total > 0) onProgress(((done * 100) / total).toInt())
                        }
                        output.flush()
                    }
                }
                if (dest.exists()) dest.delete()
                tmp.renameTo(dest)
                onResult(true)
            } catch (e: Exception) {
                tmp.delete()
                onResult(false)
            } finally {
                conn?.disconnect()
            }
        }
    }

    fun deleteDownload(context: Context, surah: Int) {
        audioFile(context, surah).delete()
    }
}
