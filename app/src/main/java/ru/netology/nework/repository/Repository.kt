package ru.netology.nework.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.Post

interface Repository {
    val dataPost: Flow<PagingData<Post>>
    val dataEvent: Flow<PagingData<Event>>
}