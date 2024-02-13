package ru.netology.nework.repository.remotemediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import ru.netology.nework.api.ApiService
import ru.netology.nework.dao.user.UserDao
import ru.netology.nework.entity.user.UserEntity
import ru.netology.nework.entity.user.toEntity
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalPagingApi::class)
@Singleton
class UserRemoteMediator @Inject constructor(
    private val apiService: ApiService,
    private val userDao: UserDao
) : RemoteMediator<Int, UserEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UserEntity>
    ): MediatorResult {
        try {
            if (loadType == LoadType.REFRESH) {
                val response = apiService.usersGetAllUser()

                if (!response.isSuccessful) {
                    error(response)
                }

                val body = response.body() ?: error(response.code())
                userDao.insertAll(body.toEntity())
            }
            return MediatorResult.Success(true)
        } catch (e: Exception) {
            println("1 + ${e.message}")
            return MediatorResult.Error(e)
        }

    }
}