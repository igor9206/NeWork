package ru.netology.nework.fragment.detailitem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.runtime.image.ImageProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.netology.nework.R
import ru.netology.nework.adapter.recyclerview.AvatarAdapter
import ru.netology.nework.adapter.tools.AvatarItemDecoration
import ru.netology.nework.databinding.FragmentDetailPostBinding
import ru.netology.nework.dto.AttachmentType
import ru.netology.nework.extension.loadAttachment
import ru.netology.nework.extension.loadAvatar
import ru.netology.nework.model.InvolvedItemType
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

        val avatarDecoration = AvatarItemDecoration(64)
        val likersAdapter = AvatarAdapter()
        val mentionedAdapter = AvatarAdapter()

        binding.recyclerLikers.apply {
            addItemDecoration(avatarDecoration)
            adapter = likersAdapter
        }
        binding.recyclerMentioned.apply {
            addItemDecoration(avatarDecoration)
            adapter = mentionedAdapter
        }

        val imageProvider =
            ImageProvider.fromResource(requireContext(), R.drawable.ic_location_on_24)

        postViewModel.postData.observe(viewLifecycleOwner) { post ->
            with(binding) {
                avatar.loadAvatar(post.authorAvatar)
                authorName.text = post.author
                lastWork.text = post.authorJob ?: "In search work"

                when (post.attachment?.type) {
                    AttachmentType.IMAGE -> {
                        imageContent.loadAttachment(post.attachment.url)
                        imageContent.isVisible = true
                    }

                    AttachmentType.VIDEO -> {
                        player = ExoPlayer.Builder(requireContext()).build().apply {
                            setMediaItem(MediaItem.fromUri(post.attachment.url))
                        }
                        videoContent.player = player
                        videoContent.isVisible = true
                    }

                    AttachmentType.AUDIO -> {
                        player = ExoPlayer.Builder(requireContext()).build().apply {
                            setMediaItem(MediaItem.fromUri(post.attachment.url))
                        }
                        videoContent.player = player
                        audioContent.isVisible = true
                    }

                    null -> {
                        imageContent.isVisible = false
                        videoContent.isVisible = false
                        audioContent.isVisible = false
                        player?.release()
                    }
                }

                datePublished.text =
                    post.published.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                content.text = post.content

                lifecycleScope.launch {
                    postViewModel.getInvolved(post.likeOwnerIds, InvolvedItemType.LIKERS)
                }

                lifecycleScope.launch {
                    postViewModel.getInvolved(post.mentionIds, InvolvedItemType.MENTIONED)
                }


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

            }
        }

        postViewModel.involvedData.observe(viewLifecycleOwner) { involved ->
            likersAdapter.submitList(involved.likers)
            mentionedAdapter.submitList(involved.mentioned)
        }

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        player?.apply {
            stop()
        }
        postViewModel.resetInvolved()
    }

}