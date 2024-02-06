package ru.netology.nework.dto

data class Post(
    val id: Long,
    val authorId: Int,
    val author: String,
    val authorJob: String?,
    val authorAvatar: String?,
    val content: String,
    val published: String,
//    val coords: Coords?,
//    val link: String?,
//    val mentionIds: List<Int>,
//    val mentionedMe: Boolean,
//    val likeOwnerIds: List<Int>,
//    val likedByMe: Boolean,
//    val attachment: Attachment?,
//    val users: Map<String, User>
)

data class Coords(
    val lat: Double,
    val long: Double
)

data class Attachment(
    val url: String,
    val type: String
)

data class User(
    val name: String,
    val avatar: String
)
