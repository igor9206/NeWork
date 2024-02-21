package ru.netology.nework.repository.remotemediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.netology.nework.api.ApiService
import ru.netology.nework.dao.post.PostDao
import ru.netology.nework.dao.post.PostRemoteKeyDao
import ru.netology.nework.db.AppDb
import ru.netology.nework.entity.KeyType
import ru.netology.nework.entity.post.PostEntity
import ru.netology.nework.entity.post.PostRemoteKeyEntity
import ru.netology.nework.entity.post.toEntity
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalPagingApi::class)
@Singleton
class PostRemoteMediator @Inject constructor(
    private val apiService: ApiService,
    private val appDb: AppDb,
    private val postDao: PostDao,
    private val postRemoteKeyDao: PostRemoteKeyDao,
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun initialize(): InitializeAction {
        return if (postDao.isEmpty()) {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {

        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    val id = postRemoteKeyDao.max()
                    if (id != null) {
                        apiService.postsGetAfterPost(id, state.config.pageSize)
                    } else {
                        apiService.postsGetLatestPage(state.config.pageSize)
                    }
                }

                LoadType.PREPEND -> {
                    val id = postRemoteKeyDao.max() ?: return MediatorResult.Success(false)
                    apiService.postsGetAfterPost(id, state.config.pageSize)
                }

                LoadType.APPEND -> {
                    val id = postRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                    apiService.postsGetBeforePost(id, state.config.pageSize)
                }
            }

            if (!response.isSuccessful) {
                error(response)
            }

            val body = response.body()
                ?: error("Body is Empty: ${response.code()} + ${response.message()}")

            appDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        postRemoteKeyDao.clear()
                        postRemoteKeyDao.insert(
                            listOf(
                                PostRemoteKeyEntity(
                                    KeyType.AFTER,
                                    body.first().id
                                ),
                                PostRemoteKeyEntity(
                                    KeyType.BEFORE,
                                    body.last().id
                                )
                            )
                        )
                        postDao.clearAll()
                    }

                    LoadType.PREPEND -> {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                KeyType.AFTER,
                                body.first().id
                            )
                        )
                    }

                    LoadType.APPEND -> {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                KeyType.BEFORE,
                                body.last().id
                            )
                        )
                    }
                }

                postDao.insertAll(body.toEntity())
            }

            return MediatorResult.Success(body.isEmpty())
        } catch (e: Exception) {
            println("1 + ${e.message}")
            return MediatorResult.Error(e)
        }
    }
}