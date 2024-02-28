package ru.netology.nework.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.adapter.PagerAdapter
import ru.netology.nework.databinding.FragmentDetailUserBinding
import ru.netology.nework.extension.loadAvatar
import ru.netology.nework.util.AppKey
import ru.netology.nework.viewmodel.AuthViewModel
import ru.netology.nework.viewmodel.UserViewModel

@AndroidEntryPoint
class DetailUserFragment : Fragment() {
    private lateinit var binding: FragmentDetailUserBinding
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var pagerAdapter: PagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailUserBinding.inflate(inflater, container, false)

        val userId = arguments?.getLong(AppKey.USER_ID)
        if (userId != null){
            userViewModel.getUser(userId)
        }

        pagerAdapter = PagerAdapter(this, userId)
        binding.pager.adapter = pagerAdapter

        userViewModel.dataUser.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.mainPhoto.loadAvatar(it.avatar)
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            when (position) {
                0 -> tab.text = this.getString(R.string.wall)
                1 -> tab.text = this.getString(R.string.jobs)
            }
        }.attach()
    }

}