package ru.netology.nework.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.FeedItem
import ru.netology.nework.dto.Post
import ru.netology.nework.dto.UserResponse

interface Repository {
    val dataPost: Flow<PagingData<FeedItem>>
    val dataEvent: Flow<PagingData<FeedItem>>
    val dataUser: Flow<PagingData<FeedItem>>
}