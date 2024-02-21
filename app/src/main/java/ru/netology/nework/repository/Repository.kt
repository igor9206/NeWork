package ru.netology.nework.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import ru.netology.nework.dto.AttachmentType
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.FeedItem
import ru.netology.nework.dto.Post
import ru.netology.nework.model.AttachmentModel
import ru.netology.nework.model.AuthModel

interface Repository {
    val dataAuth: StateFlow<AuthModel>
    val dataPost: Flow<PagingData<FeedItem>>
    val dataEvent: Flow<PagingData<FeedItem>>
    val dataUser: Flow<PagingData<FeedItem>>
    suspend fun register(
        login: String,
        name: String,
        pass: String,
        attachmentModel: AttachmentModel?
    )

    suspend fun login(login: String, pass: String)
    suspend fun like(post: Post)
    suspend fun savePost(post: Post)
    suspend fun savePostWithAttachment(post: Post, attachmentModel: AttachmentModel)
    suspend fun deletePost(id: Long)

    suspend fun saveEvent(event: Event)
    suspend fun saveEventWithAttachment(event: Event, attachmentModel: AttachmentModel)
    suspend fun deleteEvent(id: Long)
}