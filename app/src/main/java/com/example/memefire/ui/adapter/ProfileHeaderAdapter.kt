package com.example.memefire.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.memefire.databinding.ItemProfileHeaderBinding

class ProfileHeaderAdapter: RecyclerView.Adapter<ProfileHeaderAdapter.ProfileHeaderVH>() {

    private var memeCount: Int = 0

    fun setCount(memeCount: Int) {
        this.memeCount = memeCount
        notifyDataSetChanged()
    }

    inner class ProfileHeaderVH(private val binding: ItemProfileHeaderBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(memeCount: Int) {
            binding.tvFavouriteMemeCount.text = memeCount.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileHeaderVH {
        val binding = ItemProfileHeaderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProfileHeaderVH(binding)
    }

    override fun onBindViewHolder(holder: ProfileHeaderVH, position: Int) {
        holder.bind(memeCount)

        val layoutParams = holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
        layoutParams.isFullSpan = true

    }

    override fun getItemCount(): Int {
        return 1
    }
}