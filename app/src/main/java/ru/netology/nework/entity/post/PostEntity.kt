package ru.netology.nework.entity.post

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nework.dto.Attachment
import ru.netology.nework.dto.Coordinates
import ru.netology.nework.dto.Post
import ru.netology.nework.dto.UserPreview
import java.time.OffsetDateTime

@Entity
@TypeConverters(
    CoordsConverter::class,
    MentionIdsConverter::class,
    AttachmentConverter::class,
    UsersConverter::class
)
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorJob: String? = null,
    val authorAvatar: String? = null,
    val content: String,
    val published: String,
    val coords: Coordinates? = null,
    val link: String? = null,
    val mentionIds: List<Long>,
    val mentionedMe: Boolean,
    val likeOwnerIds: List<Long>,
    val likedByMe: Boolean,
    val attachment: Attachment? = null,
    val users: Map<String, UserPreview>
) {
    fun toDto() = Post(
        id,
        authorId,
        author,
        authorJob,
        authorAvatar,
        content,
        OffsetDateTime.parse(published),
        coords,
        link,
        mentionIds,
        mentionedMe,
        likeOwnerIds,
        likedByMe,
        attachment,
        users
    )

    companion object {
        fun fromDto(post: Post) = PostEntity(
            post.id,
            post.authorId,
            post.author,
            post.authorJob,
            post.authorAvatar,
            post.content,
            post.published.toString(),
            post.coords,
            post.link,
            post.mentionIds,
            post.mentionedMe,
            post.likeOwnerIds,
            post.likedByMe,
            post.attachment,
            post.users
        )
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity.Companion::fromDto)

class CoordsConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromCoords(coords: Coordinates?): String? {
        return if (coords != null) gson.toJson(coords) else null
    }

    @TypeConverter
    fun toCoords(coords: String?): Coordinates? {
        return if (coords != null) gson.fromJson(coords, Coordinates::class.java) else null
    }
}

class MentionIdsConverter {
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Long>>() {}.type

    @TypeConverter
    fun fromMentionIds(mentionIds: List<Long>?): String? {
        return if (mentionIds != null) gson.toJson(mentionIds) else null
    }

    @TypeConverter
    fun toMentionIds(mentionIds: String?): List<Long>? {
        return if (mentionIds != null) gson.fromJson(mentionIds, typeToken) else null
    }

}

class AttachmentConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromAttachment(attachment: Attachment?): String? {
        return if (attachment != null) gson.toJson(attachment) else null
    }

    @TypeConverter
    fun toAttachment(attachment: String?): Attachment? {
        return if (attachment != null) gson.fromJson(
            attachment,
            Attachment::class.java
        ) else null
    }
}

class UsersConverter {
    private val gson = Gson()
    private val typeToken = object : TypeToken<Map<String, UserPreview>>() {}.type

    @TypeConverter
    fun fromAttachment(users: Map<String, UserPreview>?): String? {
        return if (users != null) gson.toJson(users) else null
    }

    @TypeConverter
    fun toAttachment(users: String?): Map<String, UserPreview>? {
        return if (users != null) gson.fromJson(users, typeToken) else null
    }
}
