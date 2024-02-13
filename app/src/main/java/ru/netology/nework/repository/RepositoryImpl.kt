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
import ru.netology.nework.dao.user.UserDao
import ru.netology.nework.db.AppDb
import ru.netology.nework.dto.FeedItem
import ru.netology.nework.dto.UserResponse
import ru.netology.nework.entity.event.EventEntity
import ru.netology.nework.entity.post.PostEntity
import ru.netology.nework.entity.user.UserEntity
import ru.netology.nework.entity.user.toEntity
import ru.netology.nework.repository.remotemediator.EventRemoteMediator
import ru.netology.nework.repository.remotemediator.PostRemoteMediator
import ru.netology.nework.repository.remotemediator.UserRemoteMediator
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalPagingApi::class)
@Singleton
class RepositoryImpl @Inject constructor(
    postDao: PostDao,
    postRemoteMediator: PostRemoteMediator,
    eventDao: EventDao,
    eventRemoteMediator: EventRemoteMediator,
    userDao: UserDao,
    userRemoteMediator: UserRemoteMediator
) : Repository {


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


}