package com.sourate.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sourate.app.databinding.ItemXassidaBinding

class XassidaAdapter(
    private var items: List<XassidaMeta>,
    private val onClick: (XassidaMeta) -> Unit
) : RecyclerView.Adapter<XassidaAdapter.VH>() {

    inner class VH(val binding: ItemXassidaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemXassidaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.binding.tvTitle.text = item.title
        holder.binding.tvIcon.text = item.title.trim().take(1).uppercase()
        holder.binding.root.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size

    fun update(list: List<XassidaMeta>) {
        items = list
        notifyDataSetChanged()
    }
}
