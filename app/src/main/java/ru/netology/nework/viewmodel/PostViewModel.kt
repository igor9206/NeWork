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
import ru.netology.nework.dto.FeedItem
import ru.netology.nework.dto.Post
import ru.netology.nework.model.AttachmentModel
import ru.netology.nework.repository.Repository
import java.io.File
import java.time.OffsetDateTime
import javax.inject.Inject

val emptyPost = Post(
    id = 0,
    authorId = 0,
    author = "",
    authorJob = null,
    authorAvatar = null,
    content = "",
    published = OffsetDateTime.now(),
    coords = null,
    link = null,
    mentionIds = emptyList(),
    mentionedMe = false,
    likeOwnerIds = emptyList(),
    likedByMe = false,
    attachment = null,
    users = mapOf(),
    ownedByMe = false,
)

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

    private val editedPost = MutableLiveData(emptyPost)

    private val _attachmentData: MutableLiveData<AttachmentModel?> = MutableLiveData(null)
    val attachmentData: LiveData<AttachmentModel?>
        get() = _attachmentData

    private val _coordData: MutableLiveData<Point?> = MutableLiveData(null)
    val coordData: LiveData<Point?> = _coordData

    private val _mentionIdData: MutableLiveData<List<Long>> = MutableLiveData(listOf())
    val mentionIdData: LiveData<List<Long>?> = _mentionIdData


    fun savePost(content: String) {
        val text = content.trim()
        if (editedPost.value?.content == text) {
            editedPost.value = emptyPost
            return
        }
        editedPost.value = editedPost.value?.copy(content = text)
        editedPost.value?.let {
            viewModelScope.launch {
                val attachment = _attachmentData.value
                val coord = if (_coordData.value == null) null else Coordinates(
                    _coordData.value!!.latitude,
                    _coordData.value!!.longitude
                )
                if (attachment == null) {
                    repository.savePost(
                        it.copy(
                            coords = coord,
                            mentionIds = _mentionIdData.value!!
                        )
                    )
                } else {
                    repository.savePostWithAttachment(
                        it.copy(
                            coords = coord,
                            mentionIds = _mentionIdData.value!!
                        ), attachment
                    )
                }
            }
        }
        editedPost.value = emptyPost
        _attachmentData.value = null
        _coordData.value = null
        _mentionIdData.value = emptyList()
    }

    fun deletePost(post: Post) = viewModelScope.launch {
        repository.deletePost(post.id)
    }

    fun like(post: Post) = viewModelScope.launch {
        repository.like(post)
    }

    fun edit(post: Post) {
        editedPost.value = post
    }

    fun setAttachment(uri: Uri, file: File, attachmentType: AttachmentType) {
        _attachmentData.value = AttachmentModel(attachmentType, uri, file)
    }

    fun removePhoto() {
        _attachmentData.value = null
    }

    fun setCoord(point: Point?) {
        _coordData.value = point
    }

    fun removeCoords() {
        _coordData.value = null
    }

    fun setMentionId(selectedUsers: MutableList<Long>) {
        _mentionIdData.value = selectedUsers
    }

}