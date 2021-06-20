package com.example.memefire.model

data class Meme(
    val author: String,
    val postLink: String,
    val preview: List<String>,
    val subreddit: String,
    val title: String,
    val ups: Int,
    val url: String
)