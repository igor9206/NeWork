package ru.netology.nework.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import ru.netology.nework.dto.FeedItem
import ru.netology.nework.dto.Post
import ru.netology.nework.model.AuthModel
import ru.netology.nework.model.PhotoModel

interface Repository {
    val dataAuth: StateFlow<AuthModel>
    val dataPost: Flow<PagingData<FeedItem>>
    val dataEvent: Flow<PagingData<FeedItem>>
    val dataUser: Flow<PagingData<FeedItem>>
    suspend fun register(login: String, name: String, pass: String, photo: PhotoModel?)
    suspend fun login(login: String, pass: String)
    suspend fun like(post: Post)
    suspend fun savePost(post: Post)
    suspend fun deletePost(id: Long)
}