package ru.netology.nework.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.auth.AuthState
import ru.netology.nework.databinding.FragmentMainBinding
import ru.netology.nework.viewmodel.AuthViewModel

@AndroidEntryPoint
class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        val childNavHostFragment =
            childFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        val childNavController = childNavHostFragment.navController
        binding.bottomNavigation.setupWithNavController(childNavController)

        var token: AuthState? = null
        binding.topAppBar.setOnMenuItemClickListener { menu ->
            when (menu.itemId) {
                R.id.user -> {
                    println(token)
                    if (token?.id != 0L && token?.id.toString().isNotEmpty()) {
                        findNavController().navigate(R.id.action_mainFragment_to_detailUserFragment)
                    } else {
                        findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
                    }
                    true
                }

                else -> false
            }
        }

        authViewModel.data.observe(viewLifecycleOwner) { state ->
            token = state
        }

        return binding.root
    }

}