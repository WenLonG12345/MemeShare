package com.example.memefire.repository

import com.example.memefire.model.ApiResult
import com.example.memefire.model.Meme
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class MemeRepository @Inject constructor(
    private val apiService: ApiService
) {

    fun getMemeList() = flow {
        try{
            emit(ApiResult.Loading)
            val call = apiService.getMemeList()

            if(call.isSuccessful) {
                call.body()?.let {
                    emit(ApiResult.Success(it.memes))
                }
            } else {
                emit(ApiResult.Error(call.errorBody()?.string()))
            }
        }catch (e: Exception) {
            emit(ApiResult.Error(e.toString()))
        }

    }


    fun signIn(auth: FirebaseAuth, email: String, password: String): Flow<ApiResult<AuthResult>> {
        return flow {
            emit(ApiResult.Loading)
            try {
                val result = auth.signInWithEmailAndPassword(email, password)
                    .await()
                emit(ApiResult.Success(result))
            } catch (e: Exception) {
                emit(ApiResult.Error(e.toString()))
            }
        }
    }

    fun createNewUser(
        auth: FirebaseAuth,
        email: String,
        password: String
    ): Flow<ApiResult<AuthResult>> {
        return flow {
            emit(ApiResult.Loading)
            try {
                val result = auth.createUserWithEmailAndPassword(email, password)
                    .await()
                emit(ApiResult.Success(result))
            } catch (e: Exception) {
                emit(ApiResult.Error(e.toString()))
            }
        }
    }

    fun firebaseAuthWithGoogle(auth: FirebaseAuth, idToken: String) = flow {
        emit(ApiResult.Loading)
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        try{
            val result = auth.signInWithCredential(credential).await()
            emit(ApiResult.Success(result))
        }catch (e: Exception) {
            emit(ApiResult.Error(e.toString()))
        }
    }

    fun addFavMeme(
        fireStore: FirebaseFirestore,
        user: FirebaseUser?,
        meme: Meme
    ) = flow {
        try{
            emit(ApiResult.Loading)

            user?.email?.let { email ->
                fireStore.collection(email)
                    .document(meme.postLink.substringAfterLast("/"))
                    .set(meme)
                    .await()

                emit(ApiResult.Success(true))
            }

        }catch (e: Exception) {
            Timber.e(e)
            emit(ApiResult.Error(e.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun removeFavMeme(
        fireStore: FirebaseFirestore,
        user: FirebaseUser?,
        meme: Meme
    ) = flow {
        try {
            emit(ApiResult.Loading)

            user?.email?.let { email ->
                fireStore.collection(email)
                    .document(meme.postLink.substringAfterLast("/"))
                    .delete()
                    .await()

                emit(ApiResult.Success(true))
            }

        } catch (e: Exception) {
            emit(ApiResult.Error(e.toString()))
        }
    }

    fun getFavMeme(
        fireStore: FirebaseFirestore,
        user: FirebaseUser?
    ) = flow {
        try {
            emit(ApiResult.Loading)

            user?.email?.let { email ->
                val memeList = mutableListOf<Meme>()

                val docList = fireStore.collection(email).get().await().documents

                for (doc in docList) {
                    val movie = doc.toObject(Meme::class.java)
                    movie?.let { memeList.add(it) }
                }

                emit(ApiResult.Success(memeList))
            }
        }catch (e: Exception) {
            emit(ApiResult.Error(e.toString()))
        }
    }.flowOn(Dispatchers.IO)

}