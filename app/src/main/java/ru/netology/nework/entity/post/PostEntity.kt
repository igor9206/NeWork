package ru.netology.nework.entity.post

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.netology.nework.dto.Attachment
import ru.netology.nework.dto.Coordinates
import ru.netology.nework.dto.Post
import ru.netology.nework.dto.UserPreview
import ru.netology.nework.entity.converter.AttachmentConverter
import ru.netology.nework.entity.converter.CoordsConverter
import ru.netology.nework.entity.converter.MentionIdsConverter
import ru.netology.nework.entity.converter.UsersConverter
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
    val users: Map<String, UserPreview>,
    val ownedByMe: Boolean = false,
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
        users,
        ownedByMe
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
            post.users,
            post.ownedByMe
        )
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity.Companion::fromDto)
