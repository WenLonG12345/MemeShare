package com.example.memefire.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.memefire.repository.MemeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MemeViewModel @Inject constructor(
    private val memeRepository: MemeRepository
): ViewModel() {

    val auth = FirebaseAuth.getInstance()
    private val fireStore = FirebaseFirestore.getInstance()

    val meme = memeRepository.getMemeList().asLiveData()

}