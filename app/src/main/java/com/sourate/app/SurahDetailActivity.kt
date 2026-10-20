package com.sourate.app

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sourate.app.databinding.ActivitySurahDetailBinding
import com.sourate.app.databinding.ItemAyahBinding

class SurahDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySurahDetailBinding
    private lateinit var surah: Surah
    private lateinit var ayahAdapter: AyahAdapter

    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var isPrepared = false

    private var showTranslit = true
    private var showFrench = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurahDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val number = intent.getIntExtra("SURAH_NUMBER", 1)
        surah = QuranRepository.getSurah(this, number) ?: run { finish(); return }

        supportActionBar?.title = "${surah.n}. ${surah.name}"
        binding.tvHeaderArabic.text = surah.ar
        binding.tvHeaderInfo.text = "${surah.meaning} • ${surah.count} versets • ${surah.type}"

        ayahAdapter = AyahAdapter(surah)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = ayahAdapter

        setupAudio()
    }

    private fun setupAudio() {
        updateAudioStatus()
        binding.btnPlay.setOnClickListener { togglePlay() }
        binding.btnDownload.setOnClickListener {
            if (AudioRepository.isDownloaded(this, surah.n)) {
                AudioRepository.deleteDownload(this, surah.n)
                updateAudioStatus()
                Toast.makeText(this, "Audio supprimé", Toast.LENGTH_SHORT).show()
            } else {
                startDownload()
            }
        }
    }

    private fun updateAudioStatus() {
        val downloaded = AudioRepository.isDownloaded(this, surah.n)
        binding.btnDownload.text = if (downloaded) "Téléchargé ✓" else "Hors ligne"
        binding.btnPlay.text = if (isPlaying) "Pause" else "Écouter"
        if (isPlaying && isPrepared) {
            binding.tvAudioStatus.text = "Lecture en cours..."
            return
        }
        binding.tvAudioStatus.text = if (downloaded) {
            "Audio disponible hors ligne"
        } else {
            "Téléchargez pour écouter sans internet"
        }
    }

    private fun startDownload() {
        binding.progressDownload.visibility = View.VISIBLE
        binding.progressDownload.progress = 0
        binding.tvAudioStatus.text = "Téléchargement..."
        binding.btnDownload.isEnabled = false
        AudioRepository.download(
            this, surah.n,
            onProgress = { p ->
                runOnUiThread { binding.progressDownload.progress = p }
            },
            onResult = { ok ->
                runOnUiThread {
                    binding.progressDownload.visibility = View.GONE
                    binding.btnDownload.isEnabled = true
                    if (ok) {
                        Toast.makeText(this, "Téléchargement terminé", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Échec du téléchargement", Toast.LENGTH_SHORT).show()
                    }
                    updateAudioStatus()
                }
            }
        )
    }

    private fun togglePlay() {
        if (isPlaying) {
            mediaPlayer?.pause()
            isPlaying = false
            updateAudioStatus()
            return
        }
        val mp = mediaPlayer
        if (mp != null && isPrepared) {
            mp.start()
            isPlaying = true
            updateAudioStatus()
            return
        }
        if (mp != null) {
            Toast.makeText(this, "Chargement en cours...", Toast.LENGTH_SHORT).show()
            return
        }
        startPlayback()
    }

    private fun startPlayback() {
        val mp = MediaPlayer()
        mediaPlayer = mp
        isPrepared = false
        mp.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        // Les listeners DOIVENT être enregistrés avant prepareAsync(),
        // sinon on peut rater la préparation et l'audio ne démarre jamais.
        mp.setOnPreparedListener {
            isPrepared = true
            it.start()
            isPlaying = true
            updateAudioStatus()
        }
        mp.setOnCompletionListener {
            isPlaying = false
            it.seekTo(0)
            updateAudioStatus()
        }
        mp.setOnErrorListener { _, _, _ ->
            isPlaying = false
            isPrepared = false
            releasePlayer()
            updateAudioStatus()
            Toast.makeText(this, "Lecture impossible. Vérifiez votre connexion.", Toast.LENGTH_LONG).show()
            true
        }
        try {
            if (AudioRepository.isDownloaded(this, surah.n)) {
                mp.setDataSource(AudioRepository.audioFile(this, surah.n).absolutePath)
            } else {
                mp.setDataSource(AudioRepository.audioUrl(surah.n))
            }
            binding.tvAudioStatus.text = "Chargement de l'audio..."
            mp.prepareAsync()
        } catch (e: Exception) {
            releasePlayer()
            Toast.makeText(this, "Erreur audio: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun releasePlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
        isPrepared = false
        isPlaying = false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_surah, menu)
        menu.findItem(R.id.action_translit).isChecked = showTranslit
        menu.findItem(R.id.action_french).isChecked = showFrench
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_translit -> {
                item.isChecked = !item.isChecked
                showTranslit = item.isChecked
                ayahAdapter.setOptions(showTranslit, showFrench)
                return true
            }
            R.id.action_french -> {
                item.isChecked = !item.isChecked
                showFrench = item.isChecked
                ayahAdapter.setOptions(showTranslit, showFrench)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

class AyahAdapter(private val surah: Surah) :
    RecyclerView.Adapter<AyahAdapter.AyahViewHolder>() {

    private var showTranslit = true
    private var showFrench = true

    private val items: List<Any> = buildList {
        if (surah.n != 1 && surah.n != 9) {
            add("BISMILLAH")
        }
        addAll(surah.ayahs)
    }

    inner class AyahViewHolder(val binding: ItemAyahBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun setOptions(translit: Boolean, french: Boolean) {
        showTranslit = translit
        showFrench = french
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AyahViewHolder {
        val binding = ItemAyahBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AyahViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AyahViewHolder, position: Int) {
        val item = items[position]
        if (item is String) {
            holder.binding.tvNumber.text = ""
            holder.binding.tvArabic.text = "بِسْمِ ٱللَّٰهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ"
            holder.binding.tvTranslit.text = "Bismillaahir Rahmaanir Raheem"
            holder.binding.tvTranslit.visibility = if (showTranslit) View.VISIBLE else View.GONE
            holder.binding.tvFrench.visibility = View.GONE
            holder.binding.dividerFr.visibility = View.GONE
        } else {
            val ayah = item as Ayah
            holder.binding.tvNumber.text = "﴿ ${ayah.i} ﴾"
            holder.binding.tvArabic.text = ayah.ar
            holder.binding.tvTranslit.text = ayah.tr
            holder.binding.tvTranslit.visibility = if (showTranslit) View.VISIBLE else View.GONE
            holder.binding.tvFrench.text = ayah.fr
            holder.binding.tvFrench.visibility = if (showFrench) View.VISIBLE else View.GONE
            holder.binding.dividerFr.visibility = if (showFrench) View.VISIBLE else View.GONE
        }
    }

    override fun getItemCount() = items.size
}
