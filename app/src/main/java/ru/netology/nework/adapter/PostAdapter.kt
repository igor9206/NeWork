package ru.netology.nework.adapter

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SimpleExoPlayer
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.R
import ru.netology.nework.adapter.tools.FeedItemCallBack
import ru.netology.nework.adapter.tools.OnInteractionListener
import ru.netology.nework.databinding.CardPostBinding
import ru.netology.nework.dto.AttachmentType
import ru.netology.nework.dto.FeedItem
import ru.netology.nework.dto.Post
import ru.netology.nework.extension.loadAttachment
import ru.netology.nework.extension.loadAvatar
import java.time.format.DateTimeFormatter

class PostAdapter(
    private val onInteractionListener: OnInteractionListener,
) : PagingDataAdapter<FeedItem, PostViewHolder>(FeedItemCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding =
            CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener, parent.context)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = getItem(position) as Post
        holder.bind(item)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    private var player: ExoPlayer? = null

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

            videoContent.player =
                if (post.attachment?.type == AttachmentType.VIDEO) {
                    player = ExoPlayer.Builder(context).build()
                    player!!.apply {
                        setMediaItem(MediaItem.fromUri(post.attachment.url))
                    }
                } else null
            videoContent.isVisible = post.attachment?.type == AttachmentType.VIDEO

            player = if (post.attachment?.type == AttachmentType.AUDIO) {
                ExoPlayer.Builder(context).build().apply {
                    setMediaItem(MediaItem.fromUri(post.attachment.url))
                }
            } else null
            audioContent.isVisible = post.attachment?.type == AttachmentType.AUDIO
            playPauseAudio.setOnClickListener {
                if (player?.isPlaying == true) {
                    player!!.playWhenReady = !player!!.playWhenReady
                } else {
                    player?.apply {
                        prepare()
                        play()
                    }
                }
            }
            player?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    binding.playPauseAudio.setIconResource(
                        if (isPlaying) R.drawable.ic_pause_24 else R.drawable.ic_play_arrow_24
                    )
                }
            })

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