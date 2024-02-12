package ru.netology.nework.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentNewPostBinding
import ru.netology.nework.viewmodel.PostViewModel

class NewPostFragment : Fragment() {
    private lateinit var binding: FragmentNewPostBinding
    private val postViewModel: PostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewPostBinding.inflate(inflater, container, false)

        binding.topAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.save -> {
                    findNavController().navigateUp()
                    true
                }

                else -> false
            }
        }


        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

}