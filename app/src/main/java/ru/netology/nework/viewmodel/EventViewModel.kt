package ru.netology.nework.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.dto.AttachmentType
import ru.netology.nework.dto.Coordinates
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.EventType
import ru.netology.nework.dto.FeedItem
import ru.netology.nework.model.AttachmentModel
import ru.netology.nework.repository.Repository
import java.io.File
import java.time.OffsetDateTime
import javax.inject.Inject

val emptyEvent = Event(
    id = 0,
    authorId = 0,
    author = "",
    authorJob = null,
    authorAvatar = null,
    content = "",
    datetime = OffsetDateTime.now(),
    published = OffsetDateTime.now(),
    coords = null,
    type = EventType.ONLINE,
    likeOwnerIds = listOf(),
    likedByMe = false,
    speakerIds = listOf(),
    participantsIds = listOf(),
    participatedByMe = false,
    attachment = null,
    link = null,
    users = mapOf(),
    ownedByMe = false
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: Repository,
    appAuth: AppAuth
) : ViewModel() {

    val data: Flow<PagingData<FeedItem>> = appAuth.authState
        .flatMapLatest { auth ->
            repository.dataEvent.map {
                it.map { feedItem ->
                    if (feedItem is Event) {
                        feedItem.copy(ownedByMe = auth.id == feedItem.authorId)
                    } else {
                        feedItem
                    }
                }
            }
        }.flowOn(Dispatchers.Default)

    private val _editedEvent = MutableLiveData(emptyEvent)
    val editedEvent: LiveData<Event> = _editedEvent

    private val _attachmentData: MutableLiveData<AttachmentModel?> = MutableLiveData(null)

    fun saveEvent(content: String) {
        val text = content.trim()
        if (_editedEvent.value?.content == text) {
            _editedEvent.value = emptyEvent
            return
        }
        _editedEvent.value = _editedEvent.value?.copy(content = text)
        _editedEvent.value?.let {
            viewModelScope.launch {
                val attachment = _attachmentData.value
                if (attachment == null) {
                    repository.saveEvent(it)
                } else {
                    repository.saveEventWithAttachment(
                        it, attachment
                    )
                }
            }
        }
        _editedEvent.value = emptyEvent
        _attachmentData.value = null
    }


    val attachmentData: LiveData<AttachmentModel?>
        get() = _attachmentData

    fun setAttachment(uri: Uri, file: File, attachmentType: AttachmentType) {
        _attachmentData.value = AttachmentModel(attachmentType, uri, file)
    }

    fun removeAttachment() {
        _attachmentData.value = null
    }

    fun deleteEvent(event: Event) = viewModelScope.launch {
        repository.deleteEvent(event.id)
    }

    fun setCoord(point: Point?) {
        if (point != null) {
            _editedEvent.value = _editedEvent.value?.copy(
                coords = Coordinates(point.latitude, point.longitude)
            )
        }
    }

    fun removeCoords() {
        _editedEvent.value = _editedEvent.value?.copy(
            coords = null
        )
    }

    fun setMentionId(selectedUsers: List<Long>) {
        _editedEvent.value = _editedEvent.value?.copy(
            speakerIds = selectedUsers
        )
    }

}

