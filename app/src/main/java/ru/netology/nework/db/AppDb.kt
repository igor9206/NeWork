package ru.netology.nework.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.netology.nework.dao.PostDao
import ru.netology.nework.dao.PostRemoteKeyDao
import ru.netology.nework.entity.post.PostEntity
import ru.netology.nework.entity.post.PostRemoteKeyEntity

@Database(
    entities = [
        PostEntity::class,
        PostRemoteKeyEntity::class
    ], version = 1
)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
}