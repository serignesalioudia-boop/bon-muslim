package com.sourate.app

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sourate.app.databinding.ActivityXassidaBinding

class XassidaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityXassidaBinding
    private var all: List<XassidaMeta> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityXassidaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Khassidas"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = XassidaAdapter(emptyList()) { meta ->
            val intent = Intent(this, XassidaDetailActivity::class.java)
            intent.putExtra("slug", meta.slug)
            intent.putExtra("title", meta.title)
            startActivity(intent)
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {
                val q = s?.toString()?.trim()?.lowercase().orEmpty()
                val filtered = if (q.isEmpty()) all else all.filter {
                    it.title.lowercase().contains(q) || it.slug.contains(q)
                }
                (binding.recyclerView.adapter as XassidaAdapter).update(filtered)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        load()
    }

    private fun load() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvEmpty.visibility = View.GONE
        XassidaRepository.getList(this) { list ->
            binding.progressBar.visibility = View.GONE
            all = list
            (binding.recyclerView.adapter as XassidaAdapter).update(list)
            binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
