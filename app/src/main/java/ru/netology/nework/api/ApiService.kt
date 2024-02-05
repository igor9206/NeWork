package ru.netology.nework.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import ru.netology.nework.dto.Post

interface ApiService {
    @GET()
    fun getLatestPage(count: Int): Response<List<Post>>

    @GET()
    fun getAfter(id: Int, count: Int): Response<List<Post>>

    @GET()
    fun getBefore(id: Int, count: Int): Response<List<Post>>
}