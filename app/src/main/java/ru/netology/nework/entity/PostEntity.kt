package ru.netology.nework.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import ru.netology.nework.dto.Attachment
import ru.netology.nework.dto.Coords
import ru.netology.nework.dto.Post
import ru.netology.nework.dto.User

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val authorId: Int,
    val author: String,
    val authorJob: String?,
    val authorAvatar: String?,
    val content: String,
    val published: String,
//    @Embedded
//    val coords: Coords? = null,
//    val link: String,
//    val mentionIds: List<Int>,
//    val mentionedMe: Boolean,
//    val likeOwnerIds: List<Int>,
//    val likedByMe: Boolean,
//    @Embedded
//    val attachment: Attachment? = null,
//    val users: Map<String, User>
) {
    fun toDto() = Post(
        id,
        authorId,
        author,
        authorJob,
        authorAvatar,
        content,
        published,
//        coords,
//        link,
//        mentionIds,
//        mentionedMe,
//        likeOwnerIds,
//        likedByMe,
//        attachment,
//        users
    )

    companion object {
        fun fromDto(post: Post) = PostEntity(
            post.id,
            post.authorId,
            post.author,
            post.authorJob,
            post.authorAvatar,
            post.content,
            post.published,
//            post.coords,
//            post.link,
//            post.mentionIds,
//            post.mentionedMe,
//            post.likeOwnerIds,
//            post.likedByMe,
//            post.attachment,
//            post.users
        )
    }
}

//fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
//fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)
//
//class CoordsConverter {
//    private val gson = Gson()
//
//    @TypeConverter
//    fun fromCoords(coords: Coords?): String? {
//        return if (coords != null) gson.toJson(coords) else null
//    }
//
//    fun toCoords(coords: String?): Coords? {
//        return if (coords != null) gson.fromJson(coords, Coords::class.java) else null
//    }
//}
//
//
//class AttachmentConverter {
//    private val gson = Gson()
//
//    @TypeConverter
//    fun fromAttachment(attachment: Attachment?): String? {
//        return if (attachment != null) gson.toJson(attachment) else null
//    }
//
//    @TypeConverter
//    fun toAttachment(attachment: String?): Attachment? {
//        return if (attachment != null) gson.fromJson(
//            attachment,
//            Attachment::class.java
//        ) else null
//    }
//}
