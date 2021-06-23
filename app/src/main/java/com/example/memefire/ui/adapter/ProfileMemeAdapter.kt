package com.example.memefire.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.memefire.R
import com.example.memefire.databinding.ItemProfileMemeBinding
import com.example.memefire.model.Meme

class ProfileMemeAdapter(
    private val onFavouriteClick: (Meme?) -> Unit,
    private val onShareClick: (Meme?) -> Unit
): RecyclerView.Adapter<ProfileMemeAdapter.MemeVH>() {

    private var memeList = mutableListOf<Meme>()

    fun setData(memeList: List<Meme>) {
        this.memeList = memeList.toMutableList()
        notifyDataSetChanged()
    }

    fun deleteItem(meme: Meme) {
        val pos = memeList.indexOf(meme)
        memeList.removeAt(pos)
        notifyItemRemoved(pos)
        notifyItemRangeChanged(pos, memeList.size)
    }

    inner class MemeVH(private val binding: ItemProfileMemeBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(meme: Meme) {
            with(binding) {

                ivMeme.load(meme.url) {
                    placeholder(R.drawable.ic_meme_placeholder)
                }

                ivFavourite.setOnClickListener {
                    onFavouriteClick(meme)
                }

                ivShare.setOnClickListener {
                    onShareClick(meme)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeVH {
        val binding = ItemProfileMemeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MemeVH(binding)
    }

    override fun onBindViewHolder(holder: MemeVH, position: Int) {
        holder.bind(memeList[position])
    }

    override fun getItemCount(): Int {
        return memeList.size
    }

}