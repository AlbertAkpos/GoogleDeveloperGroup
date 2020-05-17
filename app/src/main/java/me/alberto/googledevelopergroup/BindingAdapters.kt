package me.alberto.googledevelopergroup

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import me.alberto.googledevelopergroup.network.GdgChapter
import me.alberto.googledevelopergroup.search.GdgListAdapter

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<GdgChapter>?){
    val adapter = recyclerView.adapter as GdgListAdapter
    adapter.submitList(data){
        recyclerView.scrollToPosition(0)
    }
}

@BindingAdapter("showOnlyWhenEmpty")
fun View.showOnlyWhenEmpty(data: List<GdgChapter>?){
    visibility = when {
        data == null || data.isEmpty() -> View.VISIBLE
        else -> View.GONE
    }
}