package ru.netology.nework.fragment.newitem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentNewJobBinding
import ru.netology.nework.viewmodel.JobViewModel

@AndroidEntryPoint
class NewJobFragment : Fragment() {
    private lateinit var binding: FragmentNewJobBinding
    private val jobViewModel: JobViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewJobBinding.inflate(inflater, container, false)

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }


        binding.buttonJobCreate.setOnClickListener {
            val name = binding.nameTextField.text.toString()
            val position = binding.positionTextField.text.toString()
            val link = binding.linkTextField.text.toString()
            val startWork = binding.startWork.text.toString()
            val finishWork = binding.finishWork.text.toString()
            jobViewModel.saveJob(name, position, link, startWork, finishWork)
            findNavController().navigateUp()
        }

        binding.datePicker.setOnClickListener {
            val dialogView =
                LayoutInflater.from(requireContext()).inflate(R.layout.dialog_date_picker, null)
            val dateStart = dialogView.findViewById<TextInputEditText>(R.id.dateStart)
            val dateFinish = dialogView.findViewById<TextInputEditText>(R.id.dateFinish)
            MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Ok") { _, _ ->
                    binding.startWork.text = dateStart.text.toString()
                    binding.finishWork.text =
                        if (dateFinish.text!!.isEmpty()) null else dateFinish.text.toString()
                }
                .setNegativeButton("Cancel") { _, _ ->

                }
                .show()
        }

        return binding.root
    }

}