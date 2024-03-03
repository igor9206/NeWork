package ru.netology.nework.fragment.detailitem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.runtime.image.ImageProvider
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentDetailPostBinding
import ru.netology.nework.dto.AttachmentType
import ru.netology.nework.extension.loadAttachment
import ru.netology.nework.extension.loadAvatar
import ru.netology.nework.viewmodel.PostViewModel
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class DetailPostFragment : Fragment() {
    private val postViewModel: PostViewModel by activityViewModels()
    private var player: ExoPlayer? = null
    private var placeMark: PlacemarkMapObject? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDetailPostBinding.inflate(inflater, container, false)


        val imageProvider =
            ImageProvider.fromResource(requireContext(), R.drawable.ic_location_on_24)

        postViewModel.postData.observe(viewLifecycleOwner) { post ->
            with(binding) {
                avatar.loadAvatar(post.authorAvatar)
                authorName.text = post.author
                lastWork.text = post.authorJob ?: "In search work"

                imageContent.isVisible = post.attachment?.type == AttachmentType.IMAGE
                imageContent.loadAttachment(post.attachment?.url)

                videoContent.isVisible = post.attachment?.type == AttachmentType.VIDEO
                videoContent.player = if (post.attachment?.type == AttachmentType.VIDEO) {
                    player = ExoPlayer.Builder(requireContext()).build()
                    player!!.apply {
                        setMediaItem(MediaItem.fromUri(post.attachment.url))
                        prepare()
                    }
                } else null

                audioContent.isVisible = post.attachment?.type == AttachmentType.AUDIO
                player = if (post.attachment?.type == AttachmentType.AUDIO) {
                    ExoPlayer.Builder(requireContext()).build().apply {
                        setMediaItem(MediaItem.fromUri(post.attachment.url))
                    }
                } else null
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

                datePublished.text =
                    post.published.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                content.text = post.content
                buttonLike.text = post.likeOwnerIds.size.toString()
                buttonLike.isChecked = post.likedByMe
                buttonMentioned.text = post.mentionIds.size.toString()

                val point =
                    if (post.coords != null) Point(post.coords.lat, post.coords.long) else null
                if (point != null) {
                    if (placeMark == null) {
                        placeMark = binding.map.mapWindow.map.mapObjects.addPlacemark()
                    }
                    placeMark?.apply {
                        geometry = point
                        setIcon(imageProvider)
                        isVisible = true
                    }
                    binding.map.mapWindow.map.move(
                        CameraPosition(
                            point,
                            13.0f,
                            0f,
                            0f
                        )
                    )
                } else {
                    placeMark = null
                }
                binding.map.isVisible = placeMark != null && point != null

            }
        }

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        player?.apply {
            pause()
            stop()
            release()
        }
        player = null
    }

}