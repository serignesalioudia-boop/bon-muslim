package com.sourate.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sourate.app.databinding.ItemSurahBinding

class SurahAdapter(
    private var surahs: List<Surah>,
    private val onClick: (Surah) -> Unit
) : RecyclerView.Adapter<SurahAdapter.SurahViewHolder>() {

    inner class SurahViewHolder(val binding: ItemSurahBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurahViewHolder {
        val binding = ItemSurahBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SurahViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SurahViewHolder, position: Int) {
        val surah = surahs[position]
        holder.binding.tvNumber.text = surah.n.toString()
        holder.binding.tvName.text = surah.name
        holder.binding.tvArabicName.text = surah.ar
        holder.binding.tvMeta.text = "${surah.meaning} • ${surah.count} versets • ${surah.type}"
        holder.binding.root.setOnClickListener { onClick(surah) }
    }

    override fun getItemCount() = surahs.size

    fun update(list: List<Surah>) {
        surahs = list
        notifyDataSetChanged()
    }
}
