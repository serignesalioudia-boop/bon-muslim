package com.sourate.app

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sourate.app.databinding.ActivityXassidaDetailBinding
import com.sourate.app.databinding.ItemXassidaImageBinding
import com.sourate.app.databinding.ItemXassidaVerseBinding

class XassidaDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityXassidaDetailBinding
    private lateinit var adapter: XassidaDetailAdapter

    private lateinit var slug: String
    private lateinit var title: String
    private var detail = XassidaDetail("", "")

    private var player: MediaPlayer? = null
    private var prepared = false
    private var isPlaying = false
    private var playIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityXassidaDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        slug = intent.getStringExtra("slug") ?: ""
        title = intent.getStringExtra("title") ?: slug
        supportActionBar?.title = title
        binding.tvTitle.text = title

        adapter = XassidaDetailAdapter(this, emptyList(), emptyList())
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.toggle.check(R.id.btnTraduction)
        binding.toggle.addOnButtonCheckedListener { _, id, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            adapter.mode =
                if (id == R.id.btnArabe) XassidaDetailAdapter.MODE_ARABE
                else XassidaDetailAdapter.MODE_TRANSLATION
        }

        binding.btnPlay.setOnClickListener { togglePlay() }
        binding.btnDownload.setOnClickListener {
            if (detail.audioUrls.isNotEmpty() &&
                detail.audioUrls.indices.all {
                    XassidaRepository.isAudioDownloaded(this, slug, it)
                }
            ) deleteAudio() else startDownload()
        }

        load()
    }

    private fun load() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvEmpty.visibility = View.GONE
        XassidaRepository.getDetail(
            this,
            slug,
            title,
            onProgress = { b ->
                runOnUiThread {
                    binding.tvAudioStatus.text = "Chargement des versets... (blocs ${b})"
                }
            },
            onDone = { d ->
                binding.progressBar.visibility = View.GONE
                detail = d
                adapter.update(d.arabicImages, d.verses)
                binding.toggle.check(
                    if (d.arabicImages.isNotEmpty()) R.id.btnArabe else R.id.btnTraduction
                )
                updateAudioStatus()
                binding.tvEmpty.visibility =
                    if (d.verses.isEmpty() && d.arabicImages.isEmpty()) View.VISIBLE else View.GONE
            }
        )
    }

    // ----------------------------------------------------------------- audio

    private fun updateAudioStatus() {
        val n = detail.audioUrls.size
        val downloaded =
            n > 0 && detail.audioUrls.indices.all {
                XassidaRepository.isAudioDownloaded(this, slug, it)
            }
        binding.btnPlay.text = if (isPlaying) "Pause" else "Écouter"
        binding.btnDownload.text = if (downloaded) "Téléchargé ✓" else "Hors ligne"
        binding.tvAudioStatus.text = when {
            n == 0 -> "Audio non disponible pour cette khassida"
            downloaded -> "Audio disponible hors ligne"
            else -> "Téléchargez pour écouter sans internet"
        }
    }

    private fun togglePlay() {
        if (detail.audioUrls.isEmpty()) {
            Toast.makeText(this, "Audio non disponible", Toast.LENGTH_SHORT).show()
            return
        }
        if (isPlaying) {
            player?.pause()
            isPlaying = false
            updateAudioStatus()
            return
        }
        if (player != null && prepared) {
            player?.start()
            isPlaying = true
            updateAudioStatus()
            return
        }
        playAt(0)
    }

    private fun playAt(index: Int) {
        if (index >= detail.audioUrls.size) {
            isPlaying = false
            prepared = false
            player?.release()
            player = null
            playIndex = 0
            updateAudioStatus()
            return
        }
        playIndex = index
        prepared = false
        player?.release()
        player = MediaPlayer()
        val downloaded = XassidaRepository.isAudioDownloaded(this, slug, index)
        try {
            if (downloaded) {
                player?.setDataSource(
                    XassidaRepository.audioFile(this, slug, index).absolutePath
                )
            } else {
                player?.setDataSource(detail.audioUrls[index])
                binding.tvAudioStatus.text =
                    "Lecture en streaming (téléchargez pour hors ligne)"
            }
            player?.setOnPreparedListener {
                prepared = true
                it.start()
                isPlaying = true
                updateAudioStatus()
            }
            player?.setOnCompletionListener {
                playAt(index + 1)
            }
            player?.setOnErrorListener { _, _, _ ->
                isPlaying = false
                prepared = false
                updateAudioStatus()
                Toast.makeText(this, "Lecture impossible", Toast.LENGTH_SHORT).show()
                true
            }
            player?.prepareAsync()
        } catch (e: Exception) {
            Toast.makeText(this, "Erreur audio: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startDownload() {
        if (detail.audioUrls.isEmpty()) return
        binding.progressDownload.visibility = View.VISIBLE
        binding.progressDownload.progress = 0
        binding.tvAudioStatus.text = "Téléchargement..."
        binding.btnDownload.isEnabled = false
        downloadNext(0)
    }

    private fun downloadNext(i: Int) {
        if (i >= detail.audioUrls.size) {
            binding.btnDownload.isEnabled = true
            binding.progressDownload.visibility = View.GONE
            Toast.makeText(this, "Téléchargement terminé", Toast.LENGTH_SHORT).show()
            updateAudioStatus()
            return
        }
        val total = detail.audioUrls.size
        XassidaRepository.downloadAudio(
            this, detail.audioUrls[i], slug, i,
            onProgress = { p ->
                runOnUiThread { binding.progressDownload.progress = (i * 100 + p) / total }
            },
            onResult = { runOnUiThread { downloadNext(i + 1) } }
        )
    }

    private fun deleteAudio() {
        detail.audioUrls.indices.forEach { XassidaRepository.deleteAudio(this, slug, it) }
        updateAudioStatus()
        Toast.makeText(this, "Audio supprimé", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

class XassidaDetailAdapter(
    private val context: Context,
    private var images: List<String>,
    private var verses: List<XassidaVerse>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val MODE_ARABE = 0
        const val MODE_TRANSLATION = 1
        private const val TYPE_IMAGE = 0
        private const val TYPE_VERSE = 1
    }

    var mode = MODE_TRANSLATION
        set(value) {
            if (field == value) return
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int =
        if (mode == MODE_ARABE) images.size else verses.size

    override fun getItemViewType(position: Int): Int =
        if (mode == MODE_ARABE) TYPE_IMAGE else TYPE_VERSE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == TYPE_IMAGE) {
            val b = ItemXassidaImageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            ImageVH(b)
        } else {
            val b = ItemXassidaVerseBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            VerseVH(b)
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ImageVH) {
            if (position >= images.size) return
            Glide.with(holder.binding.root.context)
                .load(images[position])
                .into(holder.binding.ivArabic)
        } else if (holder is VerseVH) {
            if (position >= verses.size) return
            val v = verses[position]
            holder.binding.tvNumber.text = "﴿ ${position + 1} ﴾"
            holder.binding.tvTranslit.text = v.translit
            holder.binding.tvTranslit.visibility =
                if (v.translit.isBlank()) View.GONE else View.VISIBLE
            holder.binding.tvFrench.text = v.french
            holder.binding.tvFrench.visibility =
                if (v.french.isBlank()) View.GONE else View.VISIBLE
            holder.binding.dividerFr.visibility =
                if (v.french.isBlank() || v.translit.isBlank()) View.GONE else View.VISIBLE
        }
    }

    fun update(newImages: List<String>, newVerses: List<XassidaVerse>) {
        images = newImages
        verses = newVerses
        notifyDataSetChanged()
    }

    inner class ImageVH(val binding: ItemXassidaImageBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class VerseVH(val binding: ItemXassidaVerseBinding) :
        RecyclerView.ViewHolder(binding.root)
}
