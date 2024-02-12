package ru.netology.nework.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.Post
import ru.netology.nework.dto.Token
import ru.netology.nework.dto.UserResponse

interface ApiService {

    /////////////////////////// WALL posts by a single user   ////////////////////////////////////
    @POST("api/{authorId}/wall/{id}/likes")
    suspend fun wallLikesPost(
        @Path("authorId") authorId: Long,
        @Path("id") id: String,
    ): Response<Post>

    @DELETE("api/{authorId}/wall/{id}/likes")
    suspend fun wallUnLikesPost(
        @Path("authorId") authorId: Long,
        @Path("id") id: String,
    ): Response<Post>

    @GET("api/{authorId}/wall")
    suspend fun wallGetAllPost(
        @Path("authorId") authorId: Long,
    ): Response<List<Post>>

    @GET("api/{authorId}/wall/{id}/newer")
    suspend fun wallGetNewer(
        @Path("authorId") authorId: Long,
        @Path("id") id: Long,
    ): Response<List<Post>>

    @GET("api/{authorId}/wall/{id}/before")
    suspend fun wallGetBefore(
        @Path("authorId") authorId: Long,
        @Path("id") id: Long,
        @Query("count") count: Long,
    ): Response<List<Post>>

    @GET("api/{authorId}/wall/{id}/after")
    suspend fun wallGetAfter(
        @Path("authorId") authorId: Long,
        @Path("id") id: Long,
        @Query("count") count: Long,
    ): Response<List<Post>>

    @GET("api/{authorId}/wall/{id}")
    suspend fun wallGetPost(
        @Path("authorId") authorId: Long,
        @Path("id") id: Long,
    ): Response<Post>

    @GET("api/{authorId}/wall/latest")
    suspend fun wallGetLatest(
        @Path("authorId") authorId: Long,
        @Query("count") count: Long,
    ): Response<Post>


    /////////////// MY WALL posts are filtered by your authorization token    /////////////////////
    @POST("api/my/wall/{id}/likes")
    suspend fun myWallLikesPost(
        @Path("id") id: String,
    ): Response<Post>

    @DELETE("api/my/wall/{id}/likes")
    suspend fun myWallUnLikesPost(
        @Path("id") id: String,
    ): Response<Post>

    @GET("api/my/wall")
    suspend fun myWallGetAllPost(): Response<List<Post>>

    @GET("api/my/wall/{id}/newer")
    suspend fun myWallGetNewer(
        @Path("id") id: Long,
    ): Response<List<Post>>

    @GET("api/my/wall/{id}/before")
    suspend fun myWallGetBefore(
        @Path("id") id: Long,
        @Query("count") count: Long,
    ): Response<List<Post>>

    @GET("api/my/wall/{id}/after")
    suspend fun myWallGetAfter(
        @Path("id") id: Long,
        @Query("count") count: Long,
    ): Response<List<Post>>

    @GET("api/my/wall/{id}")
    suspend fun myWallGetPost(
        @Path("id") id: Long,
    ): Response<Post>

    @GET("api/my/wall/latest")
    suspend fun myWallGetLatest(
        @Query("count") count: Long,
    ): Response<Post>


    /////////////////////////////////////   USERS   /////////////////////////////////////////////
    @FormUrlEncoded
    @POST("api/users/registration")
    suspend fun usersRegistration(
        @Field("login") login: String,
        @Field("pass") pass: String,
        @Field("name") name: String
    ): Response<Token>

    @Multipart
    @POST("api/users/registration")
    suspend fun usersRegistrationWithPhoto(
        @Query("login") login: String,
        @Query("pass") pass: String,
        @Query("name") name: String,
        @Part file: MultipartBody.Part
    ): Response<Token>

    @FormUrlEncoded
    @POST("api/users/authentication")
    suspend fun usersAuthentication(
        @Field("login") login: String,
        @Field("pass") pass: String
    ): Response<Token>

    @GET("api/users")
    suspend fun usersGetAllUser(): Response<UserResponse>

    @GET("api/users/{id}")
    suspend fun usersGetUser(
        @Path("id") id: Long,
    ): Response<UserResponse>

    ////////////////////////////////////// POSTS ////////////////////////////////////////////////
    @GET("api/posts/latest")
    suspend fun getLatestPage(@Query("count") count: Int): Response<List<Post>>

    @GET("api/posts/{id}/after")
    suspend fun getAfter(@Path("id") id: Long, @Query("count") count: Int): Response<List<Post>>

    @GET("api/posts/{id}/before")
    suspend fun getBefore(@Path("id") id: Long, @Query("count") count: Int): Response<List<Post>>

    // EVENT REQUEST
    @GET("api/events/latest")
    suspend fun getLatestPageEvent(@Query("count") count: Int): Response<List<Event>>

    @GET("api/events/{id}/after")
    suspend fun getAfterEvent(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<Event>>

    @GET("api/events/{id}/before")
    suspend fun getBeforeEvent(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<Event>>

}