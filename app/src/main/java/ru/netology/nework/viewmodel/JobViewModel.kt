package ru.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.dto.Job
import ru.netology.nework.repository.Repository
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class JobViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {
    val data: LiveData<List<Job>> = repository.dataJob


    fun getJobs(userId: Long?) = viewModelScope.launch {
        if (userId == null) {
            repository.getMyJobs()
        } else {
            repository.getJobs(userId)
        }
    }

    fun saveJob(
        name: String,
        position: String,
        link: String?,
        startWork: String,
        finishWork: String
    ) = viewModelScope.launch {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu'T'HH:mm:ssXXX", Locale.getDefault())
        repository.saveJob(
            Job(
                id = 0,
                name = name,
                position = position,
                start = OffsetDateTime.parse(
                    "${startWork}T00:00:00${OffsetDateTime.now().offset}",
                    formatter
                ),
                finish = if (finishWork.isEmpty()) OffsetDateTime.now() else OffsetDateTime.parse(
                    "${finishWork}T00:00:00${OffsetDateTime.now().offset}",
                    formatter
                ),
                link = link
            )
        )
    }

    fun deleteJob(id: Long) = viewModelScope.launch {
        repository.deleteJob(id)
    }


}