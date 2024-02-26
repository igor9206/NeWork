package ru.netology.nework.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.filter
import androidx.paging.flatMap
import androidx.paging.map
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nework.R
import ru.netology.nework.adapter.tools.OnInteractionListener
import ru.netology.nework.adapter.PostAdapter
import ru.netology.nework.databinding.FragmentPostsBinding
import ru.netology.nework.dto.FeedItem
import ru.netology.nework.dto.Post
import ru.netology.nework.dto.UserResponse
import ru.netology.nework.model.AuthModel
import ru.netology.nework.viewmodel.AuthViewModel
import ru.netology.nework.viewmodel.PostViewModel

const val EDIT_POST = "edit_post_content"

@AndroidEntryPoint
class PostsFragment : Fragment() {
    private lateinit var binding: FragmentPostsBinding
    private val postViewModel: PostViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostsBinding.inflate(inflater, container, false)
        val parentNavController = parentFragment?.parentFragment?.findNavController()

        var token: AuthModel? = null
        authViewModel.dataAuth.observe(viewLifecycleOwner) { state ->
            token = state
        }

        val userId = arguments?.getLong("userId")

        val postAdapter = PostAdapter(object : OnInteractionListener {
            override fun like(feedItem: FeedItem) {
                if (token?.id != 0L && token?.id.toString().isNotEmpty()) {
                    postViewModel.like(feedItem as Post)
                } else {
                    parentNavController?.navigate(R.id.action_mainFragment_to_loginFragment)
                }
            }

            override fun delete(feedItem: FeedItem) {
                postViewModel.deletePost(feedItem as Post)
            }

            override fun edit(feedItem: FeedItem) {
                feedItem as Post
                postViewModel.edit(feedItem)
                parentNavController?.navigate(
                    R.id.action_mainFragment_to_newPostFragment,
                    bundleOf(EDIT_POST to feedItem.content)
                )
            }

            override fun selectUser(userResponse: UserResponse) {
                TODO("Not yet implemented")
            }
        })

        binding.recyclerViewPost.adapter = postAdapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                postViewModel.data.collectLatest {
                    if (userId != null) {
                        postAdapter.submitData(it.filter { feedItem ->
                            feedItem is Post && feedItem.authorId == userId
                        })
                    } else {
                        postAdapter.submitData(it)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            postAdapter.loadStateFlow.collectLatest {
                binding.swipeRefresh.isRefreshing =
                    it.refresh is LoadState.Loading
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            postAdapter.refresh()
        }

        binding.buttonNewPost.isVisible = userId == null
        binding.buttonNewPost.setOnClickListener {
            if (token?.id != 0L && token?.id.toString().isNotEmpty()) {
                parentNavController?.navigate(R.id.action_mainFragment_to_newPostFragment)
            } else {
                parentNavController?.navigate(R.id.action_mainFragment_to_loginFragment)
            }
        }

        return binding.root
    }

}