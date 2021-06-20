package com.example.memefire.repository

import com.example.memefire.model.MemeResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("gimme/50")
    suspend fun getMemeList(): Response<MemeResponse>

}