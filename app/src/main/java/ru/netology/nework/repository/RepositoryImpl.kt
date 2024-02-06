package ru.netology.nework.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.netology.nework.api.ApiService
import ru.netology.nework.dao.PostDao
import ru.netology.nework.dao.PostRemoteKeyDao
import ru.netology.nework.db.AppDb
import ru.netology.nework.dto.Post
import ru.netology.nework.entity.post.PostEntity
import ru.netology.nework.repository.remotemediator.PostRemoteMediator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryImpl @Inject constructor(
    apiService: ApiService,
    appDb: AppDb,
    postDao: PostDao,
    postRemoteKeyDao: PostRemoteKeyDao
) : Repository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 3, enablePlaceholders = false),
        pagingSourceFactory = { postDao.pagingSource() },
        remoteMediator = PostRemoteMediator(apiService, appDb, postDao, postRemoteKeyDao)
    ).flow
        .map {
            it.map(PostEntity::toDto)
        }

}