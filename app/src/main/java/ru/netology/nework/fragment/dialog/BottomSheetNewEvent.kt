package ru.netology.nework.fragment.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.netology.nework.R
import ru.netology.nework.databinding.BottomSheetNewEventBinding
import ru.netology.nework.dto.EventType
import ru.netology.nework.viewmodel.EventViewModel
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class BottomSheetNewEvent : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetNewEventBinding
    private val eventViewModel: EventViewModel by activityViewModels()
    private var date = listOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetNewEventBinding.inflate(inflater, container, false)

        binding.dateTextField.addTextChangedListener {
            date = it.toString().trim().split(" ")
        }

        binding.radioButtonOnline.isChecked = true
        binding.radioGroup.setOnCheckedChangeListener { _, id ->
            when (id) {
                (R.id.radioButtonOnline) -> {
                    eventViewModel.setEventType(EventType.ONLINE)
                }

                else -> {
                    eventViewModel.setEventType(EventType.OFFLINE)
                }
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        if (date.isNotEmpty()) {
            try {
                val formatter =
                    DateTimeFormatter.ofPattern("MM/dd/uuuu'T'HH:mm:ssXXX", Locale.getDefault())
                val dateTime = OffsetDateTime.parse(
                    "${date[0]}T${date[1]}:00${OffsetDateTime.now().offset}",
                    formatter
                )
                eventViewModel.setDateTime(dateTime)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Unknown date format", Toast.LENGTH_SHORT).show()
            }
        }
        super.onDestroy()
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }

}