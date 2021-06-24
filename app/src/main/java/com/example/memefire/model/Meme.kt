package com.example.memefire.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Meme(
    val author: String = "",
    val postLink: String = "",
    val preview: List<String> = emptyList(),
    val subreddit: String = "",
    val title: String = "",
    val ups: Int = 0,
    val url: String = "",
    var isfavoruite: Boolean = false
): Parcelable