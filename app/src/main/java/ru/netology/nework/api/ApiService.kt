package ru.netology.nework.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.netology.nework.dto.Post

interface ApiService {
    @GET("api/posts/latest")
    suspend fun getLatestPage(@Query("count") count: Int): Response<List<Post>>

    @GET("api/posts/{id}/after")
    suspend fun getAfter(@Path("id") id: Long, @Query("count") count: Int): Response<List<Post>>

    @GET("api/posts/{id}/before")
    suspend fun getBefore(@Path("id") id: Long, @Query("count") count: Int): Response<List<Post>>
}