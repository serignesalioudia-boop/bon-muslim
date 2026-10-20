package com.sourate.app

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.LinkedHashMap
import java.util.TreeMap
import java.util.regex.Pattern

object XassidaRepository {

    private const val BASE = "https://www.ixassida.com"
    private const val UA =
        "Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120 Mobile Safari/537.36"
    private const val CHARSET = "ISO-8859-1"

    private val gson = Gson()
    private val io = Executors.newFixedThreadPool(3)
    private val main = Handler(Looper.getMainLooper())
    private val pool = Executors.newFixedThreadPool(6)
    private val PLACEHOLDER = Regex("(?i)(aidez-?nous|faire la transcription|help us|transcription.*soon|coming soon)")

    data class ListWrapper(val items: List<XassidaMeta> = emptyList())

    // ------------------------------------------------------------------ list

    fun getList(context: Context, onDone: (List<XassidaMeta>) -> Unit) {
        io.execute {
            val cache = File(context.filesDir, "xassida_list_cache.json")
            var result: List<XassidaMeta>? = null
            try {
                val doc = fetch("$BASE/")
                val items = parseList(doc)
                if (items.isNotEmpty()) {
                    cache.writeText(gson.toJson(ListWrapper(items)))
                    result = items
                }
            } catch (_: Exception) {
            }
            if (result == null && cache.exists()) {
                result = try {
                    gson.fromJson(cache.readText(), ListWrapper::class.java).items
                } catch (_: Exception) {
                    null
                }
            }
            if (result == null) {
                result = readAssetList(context)
            }
            val r = result ?: emptyList()
            main.post { onDone(r) }
        }
    }

    private fun parseList(doc: Document): List<XassidaMeta> {
        val re = Regex("""goToXassida\('([^']+)'\)""")
        val map = LinkedHashMap<String, XassidaMeta>()
        doc.select("a[onclick*=goToXassida]").forEach { a ->
            val slug = re.find(a.attr("onclick"))?.groupValues?.get(1)?.trim() ?: return@forEach
            if (slug.isEmpty() || map.containsKey(slug)) return@forEach
            val title = a.selectFirst(".item-title")?.text()?.trim()
                ?.takeIf { it.isNotEmpty() } ?: slug
            map[slug] = XassidaMeta(slug, title)
        }
        return map.values.toList()
    }

    private fun readAssetList(context: Context): List<XassidaMeta> {
        return try {
            context.assets.open("xassida_list.json").use { input ->
                gson.fromJson(
                    input.bufferedReader().readText(),
                    ListWrapper::class.java
                ).items
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    // ---------------------------------------------------------------- detail

    fun getDetail(
        context: Context,
        slug: String,
        title: String,
        onProgress: ((Int) -> Unit)? = null,
        onDone: (XassidaDetail) -> Unit
    ) {
        io.execute {
            val cache = File(context.filesDir, "xassida_$slug.json")
            var result: XassidaDetail? = null
            try {
                result = fetchAllPages(slug, title, onProgress)
                if (result.verses.isNotEmpty() || result.arabicImages.isNotEmpty() || result.audioUrls.isNotEmpty()) {
                    cache.writeText(gson.toJson(result))
                } else result = null
            } catch (_: Exception) {
            }
            if (result == null && cache.exists()) {
                result = try {
                    gson.fromJson(cache.readText(), XassidaDetail::class.java)
                } catch (_: Exception) {
                    null
                }
            }
            if (result == null) result = XassidaDetail(slug, title)
            main.post { onDone(result) }
        }
    }

    data class PageResult(
        val audio: List<String> = emptyList(),
        val images: List<String> = emptyList(),
        val verses: Map<Int, XassidaVerse> = emptyMap()
    )

    private fun clean(text: String): String {
        val t = text.replace(Regex("<[^>]+>"), " ").replace(Regex("\\s+"), " ").trim()
        if (t.isEmpty()) return ""
        if (PLACEHOLDER.containsMatchIn(t)) return ""
        return t
    }

    private fun fetchAllPages(
        slug: String,
        title: String,
        onProgress: ((Int) -> Unit)?
    ): XassidaDetail {
        val audioSet = LinkedHashSet<String>()
        val imageSet = LinkedHashSet<String>()
        val verseMap = TreeMap<Int, XassidaVerse>()
        var b = 1
        val maxBlocks = 120
        while (b <= maxBlocks) {
            val batchSize = 6
            val batchTasks = ArrayList<Pair<Int, Future<PageResult?>>>(batchSize)
            var count = 0
            while (count < batchSize && b <= maxBlocks) {
                val n = 5 * b
                val idx = b
                val fut = pool.submit<PageResult?> {
                    try {
                        val doc = fetch("$BASE/lecture/$slug/$n/")
                        parsePage(doc, slug)
                    } catch (_: Exception) {
                        null
                    }
                }
                batchTasks.add(idx to fut)
                b++
                count++
            }
            var allEmpty = true
            for (task in batchTasks) {
                try {
                    val page = task.second.get()
                    if (page != null && page.verses.isNotEmpty()) {
                        allEmpty = false
                        audioSet.addAll(page.audio)
                        imageSet.addAll(page.images)
                        page.verses.forEach { (z, v) -> if (!verseMap.containsKey(z)) verseMap[z] = v }
                    }
                    onProgress?.invoke(task.first)
                } catch (_: Exception) {
                    onProgress?.invoke(task.first)
                }
            }
            if (allEmpty && batchTasks.isNotEmpty()) break
        }
        val versesList = verseMap.values.toMutableList()
        if (versesList.isEmpty()) {
            // try to get at least from block 1 (N=5) without looping heavily? already done
        }
        return XassidaDetail(
            slug,
            title,
            audioSet.toMutableList(),
            imageSet.toMutableList(),
            versesList
        )
    }

    private fun parsePage(doc: Document, slug: String): PageResult {
        val audio = doc.select("audio[src]").mapNotNull {
            it.absUrl("src").takeIf { u -> u.isNotEmpty() }
        }.distinct()

        val images = doc.select("img[src*=x_images]").mapNotNull {
            it.absUrl("src").takeIf { u -> u.isNotEmpty() }
        }.distinct()

        val translit = TreeMap<Int, String>()
        doc.select("tr[id^=resource_transliteration] span.editableText").forEach { s ->
            val z = verseZ(s.id()) ?: return@forEach
            val c = clean(s.text())
            if (c.isNotEmpty()) translit[z] = join(translit[z], c)
        }
        val french = TreeMap<Int, String>()
        doc.select("tr[id*=translation_francaiscode] span.editableText").forEach { s ->
            val z = verseZ(s.id()) ?: return@forEach
            val c = clean(s.text())
            if (c.isNotEmpty()) french[z] = join(french[z], c)
        }

        val keys = TreeMap<Int, XassidaVerse>()
        val allSet = linkedSetOf<Int>().apply { addAll(translit.keys); addAll(french.keys) }
        allSet.sorted().forEach { z ->
            val t = translit[z].orEmpty()
            val f = french[z].orEmpty()
            if (t.isNotBlank() || f.isNotBlank()) keys[z] = XassidaVerse(t, f)
        }
        return PageResult(audio, images, keys)
    }

    private fun verseZ(id: String?): Int? {
        if (id.isNullOrEmpty()) return null
        val p = id.split("_")
        if (p.size < 5 || p[0] != "text") return null
        return p[3].toIntOrNull()
    }

    private fun join(a: String?, b: String): String { if (a.isNullOrEmpty()) return b; return "$a $b" }

    // ----------------------------------------------------------------- audio

    private fun audioDir(context: Context): File {
        val dir = File(context.filesDir, "xassida_audio")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun audioFile(context: Context, slug: String, index: Int): File =
        File(audioDir(context), "${slug}_$index.mp3")

    fun isAudioDownloaded(context: Context, slug: String, index: Int): Boolean =
        audioFile(context, slug, index).let { it.exists() && it.length() > 0 }

    fun downloadAudio(
        context: Context,
        url: String,
        slug: String,
        index: Int,
        onProgress: (Int) -> Unit,
        onResult: (Boolean) -> Unit
    ) {
        io.execute {
            val dest = audioFile(context, slug, index)
            val tmp = File(dest.parentFile, dest.name + ".part")
            var conn: HttpURLConnection? = null
            try {
                conn = (URL(url).openConnection() as HttpURLConnection).apply {
                    connectTimeout = 20000
                    readTimeout = 30000
                    setRequestProperty("User-Agent", UA)
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
                            if (total > 0) {
                                main.post { onProgress(((done * 100) / total).toInt()) }
                            }
                        }
                        output.flush()
                    }
                }
                if (dest.exists()) dest.delete()
                tmp.renameTo(dest)
                main.post { onResult(true) }
            } catch (_: Exception) {
                tmp.delete()
                main.post { onResult(false) }
            } finally {
                conn?.disconnect()
            }
        }
    }

    fun deleteAudio(context: Context, slug: String, index: Int) {
        audioFile(context, slug, index).delete()
    }

    // ----------------------------------------------------------------- fetch

    private fun fetch(url: String): Document {
        val res = Jsoup.connect(url)
            .userAgent(UA)
            .timeout(30000)
            .maxBodySize(0)
            .execute()
        val html = String(res.bodyAsBytes(), java.nio.charset.Charset.forName(CHARSET))
        return Jsoup.parse(html, url)
    }
}
