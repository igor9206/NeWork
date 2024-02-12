package ru.netology.nework.repository.remotemediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.netology.nework.api.ApiService
import ru.netology.nework.dao.event.EventDao
import ru.netology.nework.dao.event.EventRemoteKeyDao
import ru.netology.nework.db.AppDb
import ru.netology.nework.entity.event.EventEntity
import ru.netology.nework.entity.event.EventRemoteKeyEntity
import ru.netology.nework.entity.event.toEntity
import ru.netology.nework.entity.post.PostRemoteKeyEntity

@OptIn(ExperimentalPagingApi::class)
class EventRemoteMediator(
    private val apiService: ApiService,
    private val appDb: AppDb,
    private val eventDao: EventDao,
    private val eventRemoteKeyDao: EventRemoteKeyDao,
) : RemoteMediator<Int, EventEntity>() {


    override suspend fun load(loadType: LoadType, state: PagingState<Int, EventEntity>): MediatorResult {

        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    apiService.getLatestPageEvent(state.config.pageSize)
                }

                LoadType.PREPEND -> {
                    val id = eventRemoteKeyDao.max() ?: return MediatorResult.Success(false)
                    apiService.getAfterEvent(id, state.config.pageSize)
                }

                LoadType.APPEND -> {
                    val id = eventRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                    apiService.getBeforeEvent(id, state.config.pageSize)
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
                        eventRemoteKeyDao.clear()
                        eventRemoteKeyDao.insert(
                            listOf(
                                EventRemoteKeyEntity(
                                    PostRemoteKeyEntity.KeyType.AFTER,
                                    body.first().id
                                ),
                                EventRemoteKeyEntity(
                                    PostRemoteKeyEntity.KeyType.BEFORE,
                                    body.last().id
                                )
                            )
                        )
                        eventDao.clearAll()
                    }

                    LoadType.PREPEND -> {
                        eventRemoteKeyDao.insert(
                            EventRemoteKeyEntity(
                                PostRemoteKeyEntity.KeyType.AFTER,
                                body.first().id
                            )
                        )
                    }

                    LoadType.APPEND -> {
                        eventRemoteKeyDao.insert(
                            EventRemoteKeyEntity(
                                PostRemoteKeyEntity.KeyType.BEFORE,
                                body.last().id
                            )
                        )
                    }
                }

                eventDao.insertAll(body.toEntity())
            }

            return MediatorResult.Success(body.isEmpty())
        } catch (e: Exception) {
            println("1 + ${e.message}")
            return MediatorResult.Error(e)
        }
    }
}