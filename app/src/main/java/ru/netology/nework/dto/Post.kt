package ru.netology.nework.dto

import java.time.OffsetDateTime

data class Post(
    val id: Long,
    val authorId: Int,
    val author: String,
    val authorJob: String?,
    val authorAvatar: String?,
    val content: String,
    val published: OffsetDateTime,
    val coords: Coords? = null,
    val link: String?,
    val mentionIds: List<Int>,
    val mentionedMe: Boolean,
    val likeOwnerIds: List<Int>,
    val likedByMe: Boolean,
    val attachment: Attachment?,
    val users: Map<String, User>
)

data class Coords(
    val lat: Double,
    val long: Double
)

data class Attachment(
    val url: String,
    val type: AttachmentType
)

enum class AttachmentType {
    IMAGE,
    VIDEO,
    AUDIO,
}

data class User(
    val name: String,
    val avatar: String
)
