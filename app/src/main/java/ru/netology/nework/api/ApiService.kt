package ru.netology.nework.api

import retrofit2.Response
import retrofit2.http.GET
import ru.netology.nework.dto.Post

interface ApiService {

    @GET("api/posts")
    suspend fun getAllPosts(): Response<List<Post>>
}