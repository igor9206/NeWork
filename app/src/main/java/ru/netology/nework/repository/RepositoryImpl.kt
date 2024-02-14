package ru.netology.nework.repository

import android.content.Context
import android.widget.Toast
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nework.R
import ru.netology.nework.api.ApiService
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.dao.event.EventDao
import ru.netology.nework.dao.post.PostDao
import ru.netology.nework.dao.user.UserDao
import ru.netology.nework.dto.FeedItem
import ru.netology.nework.dto.Post
import ru.netology.nework.entity.event.EventEntity
import ru.netology.nework.entity.post.PostEntity
import ru.netology.nework.entity.user.UserEntity
import ru.netology.nework.model.AuthModel
import ru.netology.nework.model.PhotoModel
import ru.netology.nework.repository.remotemediator.EventRemoteMediator
import ru.netology.nework.repository.remotemediator.PostRemoteMediator
import ru.netology.nework.repository.remotemediator.UserRemoteMediator
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalPagingApi::class)
@Singleton
class RepositoryImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val appAuth: AppAuth,
    private val apiService: ApiService,
    private val postDao: PostDao,
    postRemoteMediator: PostRemoteMediator,
    eventDao: EventDao,
    eventRemoteMediator: EventRemoteMediator,
    userDao: UserDao,
    userRemoteMediator: UserRemoteMediator
) : Repository {

    override val dataAuth: StateFlow<AuthModel> = appAuth.authState

    override val dataPost: Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(pageSize = 4, enablePlaceholders = false),
        pagingSourceFactory = { postDao.pagingSource() },
        remoteMediator = postRemoteMediator
    ).flow
        .map {
            it.map(PostEntity::toDto)
        }


    override val dataEvent: Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(pageSize = 3, enablePlaceholders = false),
        pagingSourceFactory = { eventDao.pagingSource() },
        remoteMediator = eventRemoteMediator
    ).flow
        .map {
            it.map(EventEntity::toDto)
        }

    override val dataUser: Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(pageSize = 4, enablePlaceholders = false),
        pagingSourceFactory = { userDao.pagingSource() },
        remoteMediator = userRemoteMediator
    ).flow.map {
        it.map(UserEntity::toDto)
    }

    override suspend fun register(login: String, name: String, pass: String, photo: PhotoModel?) {
        try {
            val response = if (photo != null) {
                val part = MultipartBody.Part.createFormData(
                    "file",
                    photo.file.name,
                    photo.file.asRequestBody()
                )
                apiService.usersRegistrationWithPhoto(login, pass, name, part)
            } else {
                apiService.usersRegistration(login, pass, name)
            }

            if (!response.isSuccessful) {
                when (response.code()) {
                    403 -> {
                        toastMsg(context.getString(R.string.the_user_is_already_registered))
                    }

                    415 -> {
                        toastMsg(context.getString(R.string.incorrect_photo_format))
                    }

                    else -> toastMsg("${context.getString(R.string.unknown_error)}: ${response.code()}")
                }
                return
            }

            val body = response.body() ?: throw Exception("Body is Empty")
            appAuth.setAuth(body.id, body.token)
        } catch (e: Exception) {
            toastMsg("${context.getString(R.string.unknown_error)}: ${e.message}")
        }
    }

    override suspend fun login(login: String, pass: String) {
        try {
            val response = apiService.usersAuthentication(login, pass)
            if (!response.isSuccessful) {
                when (response.code()) {
                    400 -> {
                        toastMsg(context.getString(R.string.incorrect_password))
                    }

                    404 -> {
                        toastMsg(context.getString(R.string.user_unregistered))
                    }

                    else -> toastMsg("${context.getString(R.string.unknown_error)}: ${response.code()}")
                }
                return
            }

            val body = response.body() ?: throw Exception("Body is Empty")
            appAuth.setAuth(body.id, body.token)
        } catch (e: Exception) {
            toastMsg("${context.getString(R.string.unknown_error)}: ${e.message}")
        }
    }

    override suspend fun like(post: Post) {
        try {
            val response = when (post.likedByMe) {
                true -> {
                    apiService.postsUnLikePost(post.id)
                }

                else -> {
                    apiService.postsLikePost(post.id)
                }
            }

            if (!response.isSuccessful) {
                error(response.code())
            }

            val body = response.body() ?: error(response.code())

            postDao.insert(PostEntity.fromDto(body))
        } catch (e: Exception) {
            error(e)
        }
    }

    override suspend fun savePost(post: Post) {
        try {
            val response = apiService.postsSavePost(post)
            if (!response.isSuccessful) {
                error(response.code())
            }

            val body = response.body() ?: error(response.code())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: Exception) {
            error(e)
        }
    }

    override suspend fun deletePost(id: Long) {
        try {
            val response = apiService.postsDeletePost(id)
            if (!response.isSuccessful) {
                error(response.code())
            }
            postDao.deletePost(id)
        } catch (e: Exception) {
            error(e)
        }
    }


    private fun toastMsg(msg: String) {
        Toast.makeText(
            context,
            msg,
            Toast.LENGTH_SHORT
        ).show()
    }

}