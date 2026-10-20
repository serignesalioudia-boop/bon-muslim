package com.sourate.app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sourate.app.databinding.ActivityDetailBinding
import com.sourate.app.databinding.ItemSectionBinding
import com.sourate.app.databinding.ItemContentBinding
import java.io.File
import java.io.FileOutputStream

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val categoryId = intent.getIntExtra("CATEGORY_ID", 1)
        val category = IslamicRepository.getCategoryById(categoryId) ?: return

        supportActionBar?.title = category.title
        binding.tvSubtitle.text = category.subtitle

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = SectionAdapter(category.sections)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

class SectionAdapter(private val sections: List<Section>) :
    RecyclerView.Adapter<SectionAdapter.SectionViewHolder>() {

    inner class SectionViewHolder(val binding: ItemSectionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val binding = ItemSectionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val section = sections[position]
        holder.binding.tvHeading.text = section.heading

        holder.binding.recyclerViewItems.layoutManager =
            LinearLayoutManager(holder.itemView.context)
        holder.binding.recyclerViewItems.adapter = ContentAdapter(section.items)
        holder.binding.recyclerViewItems.isNestedScrollingEnabled = false
    }

    override fun getItemCount() = sections.size
}

class ContentAdapter(private val items: List<ContentItem>) :
    RecyclerView.Adapter<ContentAdapter.ContentViewHolder>() {

    inner class ContentViewHolder(val binding: ItemContentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val binding = ItemContentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ContentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvTitle.text = item.title

        if (item.arabic != null) {
            holder.binding.tvArabic.visibility = android.view.View.VISIBLE
            holder.binding.tvArabic.text = item.arabic
        } else {
            holder.binding.tvArabic.visibility = android.view.View.GONE
        }

        if (item.translit != null) {
            holder.binding.tvTranslit.visibility = android.view.View.VISIBLE
            holder.binding.tvTranslit.text = item.translit
        } else {
            holder.binding.tvTranslit.visibility = android.view.View.GONE
        }

        if (item.imageRes != null) {
            holder.binding.imgStep.visibility = android.view.View.VISIBLE
            holder.binding.imgStep.setImageResource(item.imageRes)
        } else if (item.assetImage != null) {
            holder.binding.imgStep.visibility = android.view.View.VISIBLE
            holder.binding.imgStep.scaleType = android.widget.ImageView.ScaleType.FIT_CENTER
            val lp = holder.binding.imgStep.layoutParams
            lp.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT
            lp.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            holder.binding.imgStep.layoutParams = lp
            Glide.with(holder.itemView.context)
                .load("file:///android_asset/${item.assetImage}")
                .into(holder.binding.imgStep)
        } else {
            holder.binding.imgStep.visibility = android.view.View.GONE
        }

        if (item.pdfAsset != null) {
            holder.binding.root.setOnClickListener {
                openPdf(holder.itemView.context, item.pdfAsset)
            }
        } else {
            holder.binding.root.setOnClickListener(null)
        }
    }

    private fun openPdf(context: android.content.Context, assetName: String) {
        val outFile = File(context.cacheDir, assetName)
        if (!outFile.exists()) {
            context.assets.open(assetName).use { input ->
                FileOutputStream(outFile).use { output ->
                    input.copyTo(output)
                }
            }
        }
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            outFile
        )
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(intent)
    }

    override fun getItemCount() = items.size
}
