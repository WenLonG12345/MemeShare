package com.example.memefire.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.memefire.R
import com.example.memefire.databinding.ItemMemeBinding
import com.example.memefire.model.Meme

class MemeListAdapter(

): ListAdapter<Meme, MemeListAdapter.MemeVH>(DiffUtils) {

    inner class MemeVH(private val binding: ItemMemeBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(meme: Meme?) {
            with(binding) {
                tvAuthor.text = meme?.author
                tvUps.text = meme?.ups.toString()

                ivMeme.load(meme?.url) {
                    placeholder(R.drawable.ic_meme_placeholder)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: MemeVH, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeVH {
       val binding = ItemMemeBinding.inflate(
           LayoutInflater.from(parent.context),
           parent,
           false
       )
        return MemeVH(binding)
    }

    companion object{
        private val DiffUtils = object: DiffUtil.ItemCallback<Meme>() {
            override fun areItemsTheSame(oldItem: Meme, newItem: Meme): Boolean {
                return oldItem.url == newItem.url
            }

            override fun areContentsTheSame(oldItem: Meme, newItem: Meme): Boolean {
                return oldItem == newItem
            }
        }
    }


}