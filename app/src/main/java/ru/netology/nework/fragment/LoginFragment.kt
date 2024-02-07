package ru.netology.nework.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentLoginBinding
import ru.netology.nework.viewmodel.AuthViewModel

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.buttonLogin.setOnClickListener {
            val login = binding.loginTextField.text.toString().trim()
            val pass = binding.passwordTextField.text.toString().trim()

            authViewModel.login(login, pass)
        }

        authViewModel.data.observe(viewLifecycleOwner) { state ->
            val token = state.token.toString()
            if (state.id != 0L && token.isNotEmpty()) {
                findNavController().navigate(R.id.mainFragment)
            }
        }

        return binding.root
    }


}