package ru.netology.nework.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.netology.nework.repository.Repository
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    repository: Repository
) : ViewModel() {

    val data = repository.data.cachedIn(viewModelScope)
}