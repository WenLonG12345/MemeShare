package com.example.memefire.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import coil.load
import com.example.memefire.R
import com.example.memefire.databinding.ItemFooterBinding
import com.example.memefire.databinding.ItemMemeBinding
import com.example.memefire.model.Meme

class MemeListAdapter(
    private val onFavouriteClick: (Meme?) -> Unit,
    private val onShareClick: (Meme?) -> Unit,
    private val onMemeClick: (Meme?) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var memeList = mutableListOf<Meme?>()

    fun setData(memeList: List<Meme?>) {
        this.memeList = memeList.toMutableList()
        notifyDataSetChanged()
    }

    fun setFavouriteMeme(meme: Meme) {
        val pos = memeList.indexOf(meme)
        memeList[pos]?.isfavoruite = true
        notifyItemChanged(pos)
    }

    fun setLoading() {
        memeList.add(null)
        notifyItemInserted(memeList.size - 1)
    }

    fun closeLoading() {
        memeList.removeAt(memeList.size - 1)
        notifyItemRemoved(memeList.size)
    }

    inner class MemeVH(private val binding: ItemMemeBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(meme: Meme?) {
            with(binding) {
                meme?.let {
                    tvAuthor.text = meme.author
                    tvUps.text = meme.ups.toString()

                    if(meme.isfavoruite) {
                        ivFavourite.setImageResource(R.drawable.ic_favourite)
                    } else {
                        ivFavourite.setImageResource(R.drawable.ic_unfavourite)
                    }

                    ivMeme.load(meme.url) {
                        placeholder(R.drawable.ic_meme_placeholder)
                    }

                    ivFavourite.setOnClickListener {
                        onFavouriteClick(meme)
                    }

                    ivShare.setOnClickListener {
                        onShareClick(meme)
                    }

                    cvMeme.setOnClickListener {
                        onMemeClick(meme)
                    }
                }
            }
        }
    }

    inner class FooterVH(private val binding: ItemFooterBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun getItemViewType(position: Int): Int {
        return if (memeList[position] == null) TYPE_LOADING else TYPE_ITEM
    }

    override fun getItemCount(): Int {
        return memeList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is MemeVH -> {
                holder.bind(memeList[position])
            }
            is FooterVH -> {
                val layoutParams = holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
                layoutParams.isFullSpan = true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType) {
            TYPE_ITEM -> {
                val binding = ItemMemeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return MemeVH(binding)
            }

            TYPE_LOADING -> {
                val binding = ItemFooterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return FooterVH(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    companion object{
        const val TYPE_ITEM = 1
        const val TYPE_LOADING = 2
    }

}