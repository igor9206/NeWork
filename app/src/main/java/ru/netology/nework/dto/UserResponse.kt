package ru.netology.nework.dto

data class UserResponse(
    override val id: Long,
    val login: String,
    val name: String,
    val avatar: String? = null
): FeedItem