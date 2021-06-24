package com.example.memefire.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.lifecycle.*
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.memefire.model.ApiResult
import com.example.memefire.model.Meme
import com.example.memefire.model.MemeEvent
import com.example.memefire.repository.MemeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MemeViewModel @Inject constructor(
    private val memeRepository: MemeRepository
) : ViewModel() {

    var memeList = mutableListOf<Meme?>()
    var profileMemeList = mutableListOf<Meme>()

    val auth = FirebaseAuth.getInstance()
    private val fireStore = FirebaseFirestore.getInstance()

    private val memeEventChannel = Channel<MemeEvent>()
    val memeEvent = memeEventChannel.receiveAsFlow()

    val memeAPI = memeRepository.getMemeList().asLiveData()

    fun onSignIn(email: String, password: String) =
        memeRepository.signIn(auth, email, password).asLiveData()


    fun onCreateNewUser(email: String, password: String) =
        memeRepository.createNewUser(auth, email, password).asLiveData()


    fun onFirebaseAuthWithGoogle(idToken: String) =
        memeRepository.firebaseAuthWithGoogle(auth, idToken).asLiveData()

    fun onAddFavMeme(meme: Meme?) {
        viewModelScope.launch {
            meme?.let { meme ->
                if (auth.currentUser == null) {
                    memeEventChannel.send(MemeEvent.NavigateToLoginFragment("Please Login First"))
                } else {
                    memeRepository.addFavMeme(fireStore, auth.currentUser, meme)
                        .collect { result ->
                            when (result) {
                                is ApiResult.Success -> {
                                    memeEventChannel.send(
                                        MemeEvent.AddFavMeme(
                                            "Added to Favourite",
                                            meme
                                        )
                                    )
                                }
                                is ApiResult.Error -> {
                                    memeEventChannel.send(MemeEvent.AddFavMeme(result.message))
                                }
                                ApiResult.Loading -> Unit

                            }
                        }
                }
            }
        }
    }

    fun onRemoveFavMeme(meme: Meme) {
        viewModelScope.launch {
            memeRepository.removeFavMeme(fireStore, auth.currentUser, meme)
                .collect { result ->
                    when (result) {
                        is ApiResult.Success -> {
                            memeEventChannel.send(MemeEvent.RemoveFavMeme("Remove From Favourite", meme))
                        }
                        is ApiResult.Error -> {
                            memeEventChannel.send(MemeEvent.RemoveFavMeme(result.message))
                        }
                        ApiResult.Loading -> Unit

                    }
                }
        }
    }


    // remove meme without using MemeEventChannel
    fun onRemoveProfileFavMeme(meme: Meme) =
        memeRepository.removeFavMeme(fireStore, auth.currentUser, meme).asLiveData()

    fun onGetFavMeme() = memeRepository.getFavMeme(fireStore, auth.currentUser).asLiveData()

    fun onGetBitmap(context: Context, meme: Meme): LiveData<Uri> {
        val livedata = MutableLiveData<Uri>()
        viewModelScope.launch {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(meme.url)
                .allowHardware(false) // Disable hardware bitmaps.
                .build()

            val result = (loader.execute(request) as SuccessResult).drawable
            val bitmap = (result as BitmapDrawable).bitmap

            try {
                val file = File(
                    context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                    "share_image_" + System.currentTimeMillis() + ".png"
                )
                val out = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
                out.close()
                val bmpUri = FileProvider.getUriForFile(
                    context,
                    context.applicationContext.packageName + ".provider",
                    file
                )
                livedata.postValue(bmpUri)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return livedata
    }


}