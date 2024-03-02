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
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentDetailEventBinding
import ru.netology.nework.dto.AttachmentType
import ru.netology.nework.extension.loadAttachment
import ru.netology.nework.extension.loadAvatar
import ru.netology.nework.viewmodel.EventViewModel
import java.time.format.DateTimeFormatter

class DetailEventFragment : Fragment() {
    private lateinit var binding: FragmentDetailEventBinding
    private val eventViewModel: EventViewModel by activityViewModels()
    private var player: ExoPlayer? = null
    private var placeMark: PlacemarkMapObject? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailEventBinding.inflate(inflater, container, false)

        val imageProvider =
            ImageProvider.fromResource(requireContext(), R.drawable.ic_location_on_24)

        eventViewModel.eventData.observe(viewLifecycleOwner) { event ->
            with(binding) {
                avatar.loadAvatar(event.authorAvatar)
                authorName.text = event.author
                lastWork.text = event.authorJob ?: "In search work"

                imageContent.isVisible = event.attachment?.type == AttachmentType.IMAGE
                imageContent.loadAttachment(event.attachment?.url)

                videoContent.isVisible = event.attachment?.type == AttachmentType.VIDEO
                videoContent.player = if (event.attachment?.type == AttachmentType.VIDEO) {
                    player = ExoPlayer.Builder(requireContext()).build()
                    player!!.apply {
                        setMediaItem(MediaItem.fromUri(event.attachment.url))
                        prepare()
                    }
                } else null


                audioContent.isVisible = event.attachment?.type == AttachmentType.AUDIO
                player = if (event.attachment?.type == AttachmentType.AUDIO) {
                    ExoPlayer.Builder(requireContext()).build().apply {
                        setMediaItem(MediaItem.fromUri(event.attachment.url))
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


                typeEvent.text = event.type.toString()
                dateEvent.text =
                    event.datetime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                        .toString()
                content.text = event.content

                buttonLike.isChecked = event.likedByMe
                buttonLike.text = event.likeOwnerIds.size.toString()

                participantsButton.text = event.participantsIds.size.toString()

                val point =
                    if (event.coords != null) Point(event.coords.lat, event.coords.long) else null
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