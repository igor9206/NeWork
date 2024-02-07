package ru.netology.nework.api

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import ru.netology.nework.dto.AuthItem
import ru.netology.nework.dto.Post

interface ApiService {

    // POST REQUEST
    @GET("api/posts/latest")
    suspend fun getLatestPage(@Query("count") count: Int): Response<List<Post>>

    @GET("api/posts/{id}/after")
    suspend fun getAfter(@Path("id") id: Long, @Query("count") count: Int): Response<List<Post>>

    @GET("api/posts/{id}/before")
    suspend fun getBefore(@Path("id") id: Long, @Query("count") count: Int): Response<List<Post>>


    // REGISTRATION-AUTHENTICATION
    @FormUrlEncoded
    @POST("api/users/registration")
    suspend fun registration(
        @Field("login") login: String,
        @Field("pass") pass: String,
        @Field("name") name: String
    ): Response<AuthItem>

    @FormUrlEncoded
    @POST("api/users/authentication")
    suspend fun authentication(
        @Field("login") login: String,
        @Field("pass") pass: String
    ): Response<AuthItem>

}