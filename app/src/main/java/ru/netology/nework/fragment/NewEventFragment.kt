package ru.netology.nework.fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.runtime.image.ImageProvider
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentNewEventBinding
import ru.netology.nework.dto.AttachmentType
import ru.netology.nework.extension.loadAttachment
import ru.netology.nework.util.AndroidUtils.focusAndShowKeyboard
import ru.netology.nework.util.AppKey
import ru.netology.nework.viewmodel.EventViewModel

class NewEventFragment : Fragment() {
    private lateinit var binding: FragmentNewEventBinding
    private val eventViewModel: EventViewModel by activityViewModels()
    private val gson = Gson()
    private val pointToken = object : TypeToken<Point>() {}.type
    private val usersToken = object : TypeToken<List<Long>>() {}.type
    private var placeMark: PlacemarkMapObject? = null

    private val startForPhotoResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = data?.data!!
                    val file = fileUri.toFile()

                    eventViewModel.setAttachment(fileUri, file, AttachmentType.IMAGE)
                }

                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT)
                        .show()
                }

                else -> {
                    Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }

    private val pickVideo =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                val file = uri.toFile(requireContext())!!
                val size = file.length()
                if (size > 15728640) {
                    Toast.makeText(requireContext(), "attachment > 15MB", Toast.LENGTH_SHORT).show()
                    return@registerForActivityResult
                }
                eventViewModel.setAttachment(
                    uri,
                    file,
                    AttachmentType.VIDEO
                )
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewEventBinding.inflate(inflater, container, false)

        val arg = arguments?.getString(AppKey.EDIT_EVENT)
        if (arg != null) {
            binding.textEvent.setText(arg)
        }

        binding.topAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.save -> {
                    eventViewModel.saveEvent(binding.textEvent.text.toString())
                    findNavController().navigateUp()
                    true
                }

                else -> false
            }
        }

        val bottomSheetNewEvent = BottomSheetNewEvent()
        binding.buttonSetDate.setOnClickListener {
            bottomSheetNewEvent.show(parentFragmentManager, BottomSheetNewEvent.TAG)
        }

        binding.addPhoto.setOnClickListener {
            ImagePicker.Builder(this)
                .crop()
                .maxResultSize(2048, 2048)
                .createIntent {
                    startForPhotoResult.launch(it)
                }
        }

        binding.removeImageAttachment.setOnClickListener {
            eventViewModel.removeAttachment()
        }

        binding.addFile.setOnClickListener {
            pickVideo.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
        }

        binding.addLocation.setOnClickListener {
            findNavController().navigate(R.id.action_newEventFragment_to_mapsFragment)
        }

        setFragmentResultListener(AppKey.MAPS_FRAGMENT_RESULT) { _, bundle ->
            val point = gson.fromJson<Point>(bundle.getString(AppKey.MAP_POINT), pointToken)
            if (point != null) {
                eventViewModel.setCoord(point)
            }
        }

        binding.removeLocation.setOnClickListener {
            eventViewModel.removeCoords()
        }

        binding.addUser.setOnClickListener {
            findNavController().navigate(
                R.id.action_newEventFragment_to_usersFragment2,
                bundleOf(AppKey.SELECT_USER to true)
            )
        }
        setFragmentResultListener(AppKey.USERS_FRAGMENT_RESULT) { _, bundle ->
            val selectedUsers =
                gson.fromJson<List<Long>>(bundle.getString(AppKey.SELECT_USER), usersToken)
            if (selectedUsers != null) {
                eventViewModel.setMentionId(selectedUsers)
            }
        }


        eventViewModel.attachmentData.observe(viewLifecycleOwner) { attachment ->
            when (attachment?.attachmentType) {
                AttachmentType.IMAGE -> {
                    binding.imageAttachment.loadAttachment(attachment.uri.toString())
                    binding.imageAttachmentContainer.isVisible = true
                    binding.imageAttachment.isVisible = true
                }

                AttachmentType.VIDEO -> {
                    println(attachment.uri)
                }

                AttachmentType.AUDIO -> {}
                null -> {
                    binding.imageAttachmentContainer.isVisible = false
                }
            }
        }

        val imageProvider =
            ImageProvider.fromResource(requireContext(), R.drawable.ic_location_on_24)

        eventViewModel.editedEvent.observe(viewLifecycleOwner) { event ->
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
            binding.mapContainer.isVisible = placeMark != null
        }

        binding.itemContainer.setOnClickListener {
            binding.textEvent.focusAndShowKeyboard()
        }

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

}