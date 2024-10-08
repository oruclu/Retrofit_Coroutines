package com.example.retrofitlearn.service

import androidx.lifecycle.Observer
import com.example.retrofitlearn.model.PostModel
import io.reactivex.rxjava3.core.Observable
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface PostAPI {
    @GET("posts")
    //--    Request with Coroutines
    suspend fun fetchData(): Response<List<PostModel>>

//--    Request WITH RxJAVA
//fun fetchData(): Observable<List<PostModel>>

//--    Request with CALL
//fun fetchData(): Call<List<PostModel>>
}