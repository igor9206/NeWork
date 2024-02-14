package ru.netology.nework.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.dto.FeedItem
import ru.netology.nework.dto.Post
import ru.netology.nework.repository.Repository
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: Repository,
    appAuth: AppAuth
) : ViewModel() {

    val data: Flow<PagingData<FeedItem>> = appAuth.authState
        .flatMapLatest { auth ->
            repository.dataPost.map {
                it.map { feedItem ->
                    if (feedItem is Post) {
                        feedItem.copy(
                            ownedByMe = auth.id == feedItem.authorId,
                            likedByMe = !feedItem.likeOwnerIds.none { id ->
                                id == auth.id
                            }
                        )
                    } else {
                        feedItem
                    }
                }
            }
        }.flowOn(Dispatchers.Default)

    fun like(post: Post) = viewModelScope.launch {
        repository.like(post)
    }

}