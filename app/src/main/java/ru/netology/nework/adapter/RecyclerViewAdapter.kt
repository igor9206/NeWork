package ru.netology.nework.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.R
import ru.netology.nework.databinding.CardEventBinding
import ru.netology.nework.databinding.CardPostBinding
import ru.netology.nework.databinding.CardUserBinding
import ru.netology.nework.dto.AttachmentType
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.EventType
import ru.netology.nework.dto.FeedItem
import ru.netology.nework.dto.Post
import ru.netology.nework.dto.UserResponse
import ru.netology.nework.extension.loadAttachment
import ru.netology.nework.extension.loadAvatar
import java.time.format.DateTimeFormatter

interface OnInteractionListener {
    fun like(feedItem: FeedItem)
    fun delete(feedItem: FeedItem)
    fun edit(feedItem: FeedItem)
    fun selectUser(userResponse: UserResponse)
}

class RecyclerViewAdapter(
    private val onInteractionListener: OnInteractionListener,
    private val selectUser: String?
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(PostDiffCallBack()) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Post -> R.layout.card_post
            is Event -> R.layout.card_event
            is UserResponse -> R.layout.card_user
            null -> error("unknown item type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.card_post -> {
                val binding =
                    CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(binding, onInteractionListener)
            }

            R.layout.card_event -> {
                val binding =
                    CardEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                EventViewHolder(binding)
            }

            R.layout.card_user -> {
                val binding =
                    CardUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                UserViewHolder(binding, onInteractionListener, selectUser)
            }

            else -> error("unknown view type: $viewType")
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Post -> (holder as? PostViewHolder)?.bind(item)
            is Event -> (holder as? EventViewHolder)?.bind(item)
            is UserResponse -> (holder as? UserViewHolder)?.bind(item)
            null -> error("unknown view type")
        }
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        with(binding) {
            avatar.loadAvatar(post.authorAvatar)
            authorName.text = post.author
            datePublication.text =
                post.published.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
            content.text = post.content
            buttonLike.text = post.likeOwnerIds.size.toString()
            buttonLike.isChecked = post.likedByMe

            imageContent.loadAttachment(post.attachment?.url)
            imageContent.isVisible = post.attachment?.type == AttachmentType.IMAGE

            videoContent.isVisible = post.attachment?.type == AttachmentType.VIDEO

            buttonLike.setOnClickListener {
                onInteractionListener.like(post)
            }

            buttonOption.isVisible = post.ownedByMe
            buttonOption.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.post_options)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.delete -> {
                                onInteractionListener.delete(post)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.edit(post)
                                true
                            }

                            else -> false
                        }
                    }
                    gravity = Gravity.END
                }
                    .show()
            }


        }
    }
}

class EventViewHolder(
    private val binding: CardEventBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(event: Event) {
        with(binding) {
            avatar.loadAvatar(event.authorAvatar)
            authorName.text = event.author
            datePublication.text =
                event.published.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
            content.text = event.content
            typeEvent.text = event.type.toString()
            dateEvent.text = event.datetime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
            buttonLike.text = event.likeOwnerIds.size.toString()

            buttonPlayEvent.isVisible = event.type == EventType.ONLINE
            buttonOption.isVisible = event.ownedByMe
        }
    }
}

class UserViewHolder(
    private val binding: CardUserBinding,
    private val onInteractionListener: OnInteractionListener,
    private val selectUser: String?
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(userResponse: UserResponse) {
        println(userResponse)
        with(binding) {
            authorName.text = userResponse.name
            checkBox.isVisible = selectUser != null

            checkBox.setOnClickListener {
                onInteractionListener.selectUser(userResponse)
            }
        }
    }
}

class PostDiffCallBack : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem == newItem
    }

}
