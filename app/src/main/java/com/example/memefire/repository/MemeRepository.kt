package com.example.memefire.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.memefire.model.ApiResult
import com.example.memefire.model.Meme
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MemeRepository @Inject constructor(
    private val apiService: ApiService
) {

    fun getMemeList() = flow {
        emit(ApiResult.Loading)
        val call = apiService.getMemeList()

        if(call.isSuccessful) {
            call.body()?.let {
                emit(ApiResult.Success(it.memes))
            }
        } else {
            emit(ApiResult.Error(call.errorBody()?.string()))
        }
    }

}