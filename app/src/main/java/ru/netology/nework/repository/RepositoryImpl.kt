package ru.netology.nework.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.netology.nework.api.ApiService
import ru.netology.nework.dao.event.EventDao
import ru.netology.nework.dao.event.EventRemoteKeyDao
import ru.netology.nework.dao.post.PostDao
import ru.netology.nework.dao.post.PostRemoteKeyDao
import ru.netology.nework.db.AppDb
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.Post
import ru.netology.nework.entity.event.EventEntity
import ru.netology.nework.entity.post.PostEntity
import ru.netology.nework.repository.remotemediator.EventRemoteMediator
import ru.netology.nework.repository.remotemediator.PostRemoteMediator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryImpl @Inject constructor(
    apiService: ApiService,
    appDb: AppDb,
    postDao: PostDao,
    postRemoteKeyDao: PostRemoteKeyDao,
    eventDao: EventDao,
    eventRemoteKeyDao: EventRemoteKeyDao
) : Repository {

    @OptIn(ExperimentalPagingApi::class)
    override val dataPost: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 3, enablePlaceholders = false),
        pagingSourceFactory = { postDao.pagingSource() },
        remoteMediator = PostRemoteMediator(apiService, appDb, postDao, postRemoteKeyDao)
    ).flow
        .map {
            it.map(PostEntity::toDto)
        }

    @OptIn(ExperimentalPagingApi::class)
    override val dataEvent: Flow<PagingData<Event>> = Pager(
        config = PagingConfig(pageSize = 3, enablePlaceholders = false),
        pagingSourceFactory = { eventDao.pagingSource() },
        remoteMediator = EventRemoteMediator(apiService, appDb, eventDao, eventRemoteKeyDao)
    ).flow
        .map {
            it.map(EventEntity::toDto)
        }

}