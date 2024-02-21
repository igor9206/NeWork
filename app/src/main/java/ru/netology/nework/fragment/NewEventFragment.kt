package ru.netology.nework.fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentNewEventBinding
import ru.netology.nework.dto.AttachmentType
import ru.netology.nework.extension.loadAttachment
import ru.netology.nework.util.AndroidUtils.focusAndShowKeyboard
import ru.netology.nework.viewmodel.EventViewModel

class NewEventFragment : Fragment() {
    private lateinit var binding: FragmentNewEventBinding
    private val eventViewModel: EventViewModel by activityViewModels()

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

        binding.itemContainer.setOnClickListener {
            binding.textEvent.focusAndShowKeyboard()
        }

        return binding.root
    }

}