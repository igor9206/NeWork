package ru.netology.nework.entity.post

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "postKey")
data class PostRemoteKeyEntity(
    @PrimaryKey
    val type: KeyType,
    val id: Long
) {
    enum class KeyType {
        AFTER,
        BEFORE
    }
}
