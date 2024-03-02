package ru.netology.nework.fragment.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentDetailEventBinding

class DetailEventFragment : Fragment() {
    private lateinit var binding: FragmentDetailEventBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailEventBinding.inflate(inflater, container, false)

        return binding.root
    }

}