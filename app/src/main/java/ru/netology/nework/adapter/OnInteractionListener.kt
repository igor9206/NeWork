package ru.netology.nework.adapter

import ru.netology.nework.dto.FeedItem
import ru.netology.nework.dto.UserResponse

interface OnInteractionListener {
    fun like(feedItem: FeedItem)
    fun delete(feedItem: FeedItem)
    fun edit(feedItem: FeedItem)
    fun selectUser(userResponse: UserResponse)
}