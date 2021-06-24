package com.example.memefire.model

sealed class MemeEvent {
    data class AddFavMeme(val msg: String?, val meme: Meme? = null): MemeEvent()
    data class RemoveFavMeme(val msg: String?, val meme: Meme? = null): MemeEvent()
    data class NavigateToLoginFragment(val msg: String): MemeEvent()
}