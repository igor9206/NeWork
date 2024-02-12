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
class EventViewModel @Inject constructor(
    repository: Repository
) : ViewModel() {

    val data: Flow<PagingData<FeedItem>> = repository.dataEvent.map {
        it.map { event ->
            event.copy()
        }
    }


}