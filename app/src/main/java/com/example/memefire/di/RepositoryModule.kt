package com.example.memefire.di

import com.example.memefire.repository.ApiService
import com.example.memefire.repository.MemeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    @ViewModelScoped
    fun provideMemeRepo(
        apiService: ApiService
    ): MemeRepository = MemeRepository(apiService)
}