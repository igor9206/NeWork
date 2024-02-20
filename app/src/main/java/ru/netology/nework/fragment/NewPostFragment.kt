package ru.netology.nework.fragment

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.runtime.image.ImageProvider
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentNewPostBinding
import ru.netology.nework.dto.AttachmentType
import ru.netology.nework.viewmodel.PostViewModel
import java.io.File
import java.io.FileOutputStream

class NewPostFragment : Fragment() {
    private lateinit var binding: FragmentNewPostBinding
    private val postViewModel: PostViewModel by activityViewModels()
    private var placeMark: PlacemarkMapObject? = null

    private val startForPhotoResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = data?.data!!
                    val file = fileUri.toFile()

                    postViewModel.setAttachment(fileUri, file, AttachmentType.IMAGE)
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
                postViewModel.setAttachment(
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
        binding = FragmentNewPostBinding.inflate(inflater, container, false)

        val arg = arguments?.getString(EDIT_POST)
        if (arg != null) {
            binding.edit.setText(arg)
        }

        binding.topAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.save -> {
                    postViewModel.savePost(binding.edit.text.toString())
                    findNavController().navigateUp()
                    true
                }

                else -> false
            }
        }

        binding.addPhoto.setOnClickListener {
            ImagePicker.Builder(this)
                .crop()
                .maxResultSize(2048, 2048)
                .createIntent {
                    startForPhotoResult.launch(it)
                }
        }

        binding.addFile.setOnClickListener {
            pickVideo.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
        }

        binding.remove.setOnClickListener {
            postViewModel.removePhoto()
        }

        binding.addLocation.setOnClickListener {
            findNavController().navigate(R.id.action_newPostFragment_to_mapsFragment)
        }

        binding.addUser.setOnClickListener {
            findNavController().navigate(
                R.id.action_newPostFragment_to_usersFragment2,
                bundleOf("selectUser" to "selectUser")
            )
        }


        postViewModel.attachmentData.observe(viewLifecycleOwner) { attachment ->
            when (attachment?.attachmentType) {
                AttachmentType.IMAGE -> {
                    binding.preview.setImageURI(attachment.uri)
                    binding.imageContainer.isVisible = true
                }

                AttachmentType.VIDEO -> {
                    println(attachment.uri)
                }

                AttachmentType.AUDIO -> {}
                null -> {
                    binding.imageContainer.isVisible = false
                }
            }
        }

        val imageProvider =
            ImageProvider.fromResource(requireContext(), R.drawable.ic_location_on_24)
        postViewModel.coordData.observe(viewLifecycleOwner) { point ->
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
                binding.map.isVisible = true
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

}


fun Uri.toFile(context: Context): File? {
    val inputStream = context.contentResolver.openInputStream(this)
    val tempFile = File.createTempFile("temp", ".jpg")

    val outputStream = FileOutputStream(tempFile)
    inputStream?.use { input ->
        outputStream.use { output ->
            input.copyTo(output)
        }
    }
    return tempFile
}
