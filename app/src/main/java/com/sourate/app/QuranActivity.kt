package com.sourate.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sourate.app.databinding.ActivityQuranBinding

class QuranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuranBinding
    private lateinit var allSurahs: List<Surah>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Le Saint Coran"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        allSurahs = QuranRepository.getSurahs(this)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = SurahAdapter(allSurahs) { surah ->
            val intent = Intent(this, SurahDetailActivity::class.java)
            intent.putExtra("SURAH_NUMBER", surah.n)
            startActivity(intent)
        }

        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {
                val q = s?.toString()?.trim()?.lowercase() ?: ""
                val filtered = if (q.isEmpty()) allSurahs else allSurahs.filter {
                    it.name.lowercase().contains(q) ||
                        it.meaning.lowercase().contains(q) ||
                        it.n.toString() == q ||
                        it.ar.contains(q)
                }
                (binding.recyclerView.adapter as SurahAdapter).update(filtered)
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
