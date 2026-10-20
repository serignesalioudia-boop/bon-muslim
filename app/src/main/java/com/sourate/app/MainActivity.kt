package com.sourate.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.sourate.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "  BOM MUSLIM"

        binding.cardQuran.setOnClickListener {
            startActivity(Intent(this, QuranActivity::class.java))
        }
        binding.cardPrayer.setOnClickListener {
            startActivity(Intent(this, PrayerTimesActivity::class.java))
        }
        binding.cardQibla.setOnClickListener {
            startActivity(Intent(this, QiblaActivity::class.java))
        }
        binding.cardKhassida.setOnClickListener {
            startActivity(Intent(this, XassidaActivity::class.java))
        }

        val categories = IslamicRepository.getCategories()
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.adapter = CategoryAdapter(categories) { category ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("CATEGORY_ID", category.id)
            startActivity(intent)
        }
    }
}
