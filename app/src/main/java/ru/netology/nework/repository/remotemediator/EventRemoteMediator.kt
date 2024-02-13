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
import ru.netology.nework.entity.KeyType
import ru.netology.nework.entity.event.EventEntity
import ru.netology.nework.entity.event.EventRemoteKeyEntity
import ru.netology.nework.entity.event.toEntity
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalPagingApi::class)
@Singleton
class EventRemoteMediator @Inject constructor(
    private val apiService: ApiService,
    private val appDb: AppDb,
    private val eventDao: EventDao,
    private val eventRemoteKeyDao: EventRemoteKeyDao,
) : RemoteMediator<Int, EventEntity>() {


    override suspend fun load(loadType: LoadType, state: PagingState<Int, EventEntity>): MediatorResult {

        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    apiService.eventsGetLatestPageEvent(state.config.pageSize)
                }

                LoadType.PREPEND -> {
                    val id = eventRemoteKeyDao.max() ?: return MediatorResult.Success(false)
                    apiService.eventsGetAfterEvent(id, state.config.pageSize)
                }

                LoadType.APPEND -> {
                    val id = eventRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                    apiService.eventsGetBeforeEvent(id, state.config.pageSize)
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
                                    KeyType.AFTER,
                                    body.first().id
                                ),
                                EventRemoteKeyEntity(
                                    KeyType.BEFORE,
                                    body.last().id
                                )
                            )
                        )
                        eventDao.clearAll()
                    }

                    LoadType.PREPEND -> {
                        eventRemoteKeyDao.insert(
                            EventRemoteKeyEntity(
                                KeyType.AFTER,
                                body.first().id
                            )
                        )
                    }

                    LoadType.APPEND -> {
                        eventRemoteKeyDao.insert(
                            EventRemoteKeyEntity(
                                KeyType.BEFORE,
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