package ru.netology.nework.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nework.adapter.OnInteractionListener
import ru.netology.nework.adapter.RecyclerViewAdapter
import ru.netology.nework.dao.user.UserDao
import ru.netology.nework.databinding.FragmentUsersBinding
import ru.netology.nework.dto.FeedItem
import ru.netology.nework.dto.UserResponse
import ru.netology.nework.viewmodel.UserViewModel
import javax.inject.Inject

@AndroidEntryPoint
class UsersFragment : Fragment() {
    private lateinit var binding: FragmentUsersBinding
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUsersBinding.inflate(inflater, container, false)

        val arg = arguments?.getString("selectUser")

        val userAdapter = RecyclerViewAdapter(object : OnInteractionListener {
            override fun like(feedItem: FeedItem) {
                TODO("Not yet implemented")
            }

            override fun delete(feedItem: FeedItem) {
                TODO("Not yet implemented")
            }

            override fun edit(feedItem: FeedItem) {
                TODO("Not yet implemented")
            }

            override fun selectUser(userResponse: UserResponse) {
            }
        }, arg)
        binding.recyclerViewUser.adapter = userAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.dataUser.collectLatest {
                    userAdapter.submitData(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            userAdapter.loadStateFlow.collectLatest {
                binding.swipeRefresh.isRefreshing =
                    it.refresh is LoadState.Loading
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            userAdapter.refresh()
        }


        return binding.root
    }

}