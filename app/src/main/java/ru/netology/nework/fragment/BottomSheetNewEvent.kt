package ru.netology.nework.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.netology.nework.R
import ru.netology.nework.databinding.BottomSheetNewEventBinding
import ru.netology.nework.databinding.FragmentNewEventBinding

class BottomSheetNewEvent : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetNewEventBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetNewEventBinding.inflate(inflater, container, false)

        return binding.root
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }

}