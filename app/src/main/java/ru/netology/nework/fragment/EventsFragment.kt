package ru.netology.nework.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nework.R
import ru.netology.nework.adapter.EventAdapter
import ru.netology.nework.adapter.tools.OnInteractionListener
import ru.netology.nework.databinding.FragmentEventsBinding
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.FeedItem
import ru.netology.nework.dto.UserResponse
import ru.netology.nework.model.AuthModel
import ru.netology.nework.viewmodel.AuthViewModel
import ru.netology.nework.viewmodel.EventViewModel

@AndroidEntryPoint
class EventsFragment : Fragment() {
    private lateinit var binding: FragmentEventsBinding
    private val eventViewModel: EventViewModel by viewModels()
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventsBinding.inflate(inflater, container, false)
        val parentNavController = parentFragment?.parentFragment?.findNavController()

        var token: AuthModel? = null
        authViewModel.dataAuth.observe(viewLifecycleOwner) { state ->
            token = state
        }

        val eventAdapter = EventAdapter(object : OnInteractionListener {
            override fun like(feedItem: FeedItem) {
                TODO("Not yet implemented")
            }

            override fun delete(feedItem: FeedItem) {
                eventViewModel.deleteEvent(feedItem as Event)
            }

            override fun edit(feedItem: FeedItem) {
                TODO("Not yet implemented")
            }

            override fun selectUser(userResponse: UserResponse) {
                TODO("Not yet implemented")
            }
        })

        binding.recyclerViewEvent.adapter = eventAdapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                eventViewModel.data.collectLatest {
                    println(it)
                    eventAdapter.submitData(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            eventAdapter.loadStateFlow.collectLatest {
                binding.swipeRefreshEvent.isRefreshing =
                    it.refresh is LoadState.Loading
            }
        }

        binding.swipeRefreshEvent.setOnRefreshListener {
            eventAdapter.refresh()
        }

        binding.buttonNewEvent.setOnClickListener {
            if (token?.id != 0L && token?.id.toString().isNotEmpty()) {
                parentNavController?.navigate(R.id.action_mainFragment_to_newEventFragment)
            } else {
                parentNavController?.navigate(R.id.action_mainFragment_to_loginFragment)
            }
        }


        return binding.root
    }

}