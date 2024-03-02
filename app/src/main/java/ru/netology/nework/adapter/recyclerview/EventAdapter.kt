package ru.netology.nework.adapter.recyclerview

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.R
import ru.netology.nework.adapter.tools.FeedItemCallBack
import ru.netology.nework.adapter.tools.OnInteractionListener
import ru.netology.nework.databinding.CardEventBinding
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.EventType
import ru.netology.nework.dto.FeedItem
import ru.netology.nework.extension.loadAttachment
import ru.netology.nework.extension.loadAvatar
import java.time.format.DateTimeFormatter

class EventAdapter(
    private val onInteractionListener: OnInteractionListener
) : PagingDataAdapter<FeedItem, EventViewHolder>(FeedItemCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding =
            CardEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val item = getItem(position) as Event
        holder.bind(item)
    }
}

class EventViewHolder(
    private val binding: CardEventBinding,
    private val onInteractionListener: OnInteractionListener
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
            buttonLike.isChecked = event.likedByMe

            buttonPlayEvent.isVisible = event.type == EventType.ONLINE
            buttonOption.isVisible = event.ownedByMe

            imageContent.loadAttachment(event.attachment?.url.toString())
            imageContent.isVisible = event.attachment != null

            buttonGroup.text = event.speakerIds.size.toString()

            buttonOption.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.post_options)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.delete -> {
                                onInteractionListener.delete(event)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.edit(event)
                                true
                            }

                            else -> false
                        }
                    }
                    gravity = Gravity.END
                }
                    .show()
            }

            buttonLike.setOnClickListener {
                onInteractionListener.like(event)
            }

            cardEvent.setOnClickListener {
                onInteractionListener.openCard(event)
            }


        }
    }
}