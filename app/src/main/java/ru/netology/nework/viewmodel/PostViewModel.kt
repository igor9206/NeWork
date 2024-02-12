package ru.netology.nework.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.netology.nework.dto.FeedItem
import ru.netology.nework.repository.Repository
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    val data: Flow<PagingData<FeedItem>> = repository.dataPost.map {
        it.map { post->
            post.copy()
        }
    }
}