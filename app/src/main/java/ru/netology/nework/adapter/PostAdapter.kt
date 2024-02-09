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
import ru.netology.nework.databinding.CardPostBinding
import ru.netology.nework.dto.AttachmentType
import ru.netology.nework.dto.Post
import ru.netology.nework.extension.loadAttachment
import ru.netology.nework.extension.loadAvatar
import java.time.format.DateTimeFormatter

interface OnInteractionListener {
}

class PostAdapter(
    private val onInteractionListener: OnInteractionListener
) : PagingDataAdapter<Post, PostViewHolder>(PostDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position) ?: return
        holder.bind(post)
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

            imageContent.loadAttachment(post.attachment?.url)
            imageContent.isVisible = post.attachment?.type == AttachmentType.IMAGE

            videoContent.isVisible = post.attachment?.type == AttachmentType.VIDEO

            buttonOption.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.post_options)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.delete -> {
                                true
                            }

                            R.id.edit -> {
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

class PostDiffCallBack : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }

}
