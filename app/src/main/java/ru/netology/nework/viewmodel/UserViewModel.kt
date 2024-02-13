package ru.netology.nework.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.netology.nework.dto.FeedItem
import ru.netology.nework.repository.Repository
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    val dataUser: Flow<PagingData<FeedItem>> =
        repository.dataUser.map {
            it.map { userResponse ->
                userResponse
            }
        }.flowOn(Dispatchers.Default)

}