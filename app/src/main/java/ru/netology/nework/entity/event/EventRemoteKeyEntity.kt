package ru.netology.nework.entity.event

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nework.entity.post.PostRemoteKeyEntity

@Entity
data class EventRemoteKeyEntity(
    @PrimaryKey
    val type: PostRemoteKeyEntity.KeyType,
    val id: Long
) {
    enum class KeyType {
        AFTER,
        BEFORE
    }
}
