package ru.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.dto.Post
import ru.netology.nework.repository.Repository
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
}