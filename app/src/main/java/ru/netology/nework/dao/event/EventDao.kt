package ru.netology.nework.dao.event

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.nework.entity.event.EventEntity
import ru.netology.nework.entity.post.PostEntity

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<EventEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity)

    @Query("DELETE FROM EventEntity WHERE id = :id")
    suspend fun deleteEvent(id: Long)

    @Query("SELECT * FROM eventEntity ORDER BY id DESC")
    fun pagingSource(): PagingSource<Int, EventEntity>

    @Query("DELETE FROM eventEntity")
    suspend fun clearAll()
}