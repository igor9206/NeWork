package ru.netology.nework.fragment.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nework.R
import ru.netology.nework.adapter.recyclerview.UserAdapter
import ru.netology.nework.adapter.tools.OnInteractionListener
import ru.netology.nework.databinding.FragmentUsersBinding
import ru.netology.nework.dto.FeedItem
import ru.netology.nework.dto.UserResponse
import ru.netology.nework.util.AppKey
import ru.netology.nework.viewmodel.UserViewModel

@AndroidEntryPoint
class UsersFragment : Fragment() {
    private val userViewModel: UserViewModel by activityViewModels()
    private val gson = Gson()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentUsersBinding.inflate(inflater, container, false)
        val parentNavController = parentFragment?.parentFragment?.findNavController()

        val arg = arguments?.getBoolean(AppKey.SELECT_USER) ?: false
        val selectedUsers = mutableListOf<Long>()

        val userAdapter = UserAdapter(object : OnInteractionListener {
            override fun like(feedItem: FeedItem) {}
            override fun delete(feedItem: FeedItem) {}
            override fun edit(feedItem: FeedItem) {}

            override fun selectUser(userResponse: UserResponse) {
                if (selectedUsers.contains(userResponse.id)) {
                    selectedUsers.remove(userResponse.id)
                } else {
                    selectedUsers.add(userResponse.id)
                }
            }

            override fun openCard(feedItem: FeedItem) {
                parentNavController?.navigate(
                    R.id.action_mainFragment_to_detailUserFragment,
                    bundleOf(AppKey.USER_ID to feedItem.id)
                )
            }

        }, arg, selectedUsers)
        binding.recyclerViewUser.adapter = userAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.dataUsers.collectLatest {
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

        binding.topAppBar.isVisible = arg
        binding.topAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.save -> {
                    setFragmentResult(
                        AppKey.USERS_FRAGMENT_RESULT,
                        bundleOf(AppKey.SELECT_USER to gson.toJson(selectedUsers))
                    )
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