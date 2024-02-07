package ru.netology.nework.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentRegisterBinding
import ru.netology.nework.viewmodel.AuthViewModel

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.buttonLogin.setOnClickListener {
            val pass = binding.passwordTextField.text.toString().trim()
            val confirmPass = binding.repeatPasswordTextField.text.toString().trim()

            if (pass == confirmPass) {
                authViewModel.register(
                    binding.loginTextField.text.toString().trim(),
                    binding.nameTextField.text.toString().trim(),
                    pass
                )
            } else {
                binding.apply {
                    passLayout.error = getString(R.string.passwords_dont_match)
                    repeatPassLayout.error = getString(R.string.passwords_dont_match)
                }
            }

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