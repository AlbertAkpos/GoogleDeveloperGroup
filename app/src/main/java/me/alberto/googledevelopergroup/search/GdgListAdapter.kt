package me.alberto.googledevelopergroup.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.alberto.googledevelopergroup.databinding.ListItemBinding
import me.alberto.googledevelopergroup.network.GdgChapter

class GdgListAdapter(private val clickListener: GdgClickListener): ListAdapter<GdgChapter, GdgListAdapter.GdgListViewHolder>(DiffCallback()) {

    class DiffCallback: DiffUtil.ItemCallback<GdgChapter>() {
        override fun areItemsTheSame(oldItem: GdgChapter, newItem: GdgChapter): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: GdgChapter, newItem: GdgChapter): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GdgListViewHolder {
        return GdgListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: GdgListViewHolder, position: Int) {
       holder.bind(getItem(position), clickListener)
    }

    class GdgListViewHolder(private val binding: ListItemBinding ): RecyclerView.ViewHolder(binding.root) {
        fun bind(chapter: GdgChapter, clickListener: GdgClickListener){
            binding.chapter = chapter
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): GdgListViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemBinding.inflate(layoutInflater, parent, false)
                return GdgListViewHolder(binding)
            }
        }
    }
}

class GdgClickListener(val clickListener: (chapter: GdgChapter) -> Unit) {
    fun onClick(chapter: GdgChapter) = clickListener(chapter)
}
