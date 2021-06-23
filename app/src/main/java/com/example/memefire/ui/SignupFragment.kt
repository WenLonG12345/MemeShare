package com.example.memefire.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.memefire.R
import com.example.memefire.databinding.FragmentSignupBinding
import com.example.memefire.model.ApiResult
import com.example.memefire.utils.isEmailValid
import com.example.memefire.utils.isPasswordValid
import com.example.memefire.utils.showToast
import com.example.memefire.viewmodel.MemeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignupFragment : Fragment(R.layout.fragment_signup) {

    private lateinit var binding: FragmentSignupBinding
    private val memeViewModel by activityViewModels<MemeViewModel>()
    @Inject lateinit var progressDialog: CustomProgressDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentSignupBinding.bind(view)

        binding.btnSignUp.setOnClickListener {
            when{
                binding.etEmail.text.toString().isEmpty() -> {
                    binding.etEmail.error = "Email cannot be empty"
                }
                binding.etPassword.text.toString().isEmpty() -> {
                    binding.etPassword.error = "Password cannot be empty"
                }
                binding.etEmail.text.toString().isEmailValid() -> {
                    binding.etEmail.error = "Invalid email"
                }
                !binding.etPassword.text.toString().isPasswordValid() -> {
                    binding.etPassword.error = "Password size must be more than 7"
                }
                else -> {
                    signUpUser(binding.etEmail.text.toString(), binding.etPassword.text.toString())
                }
            }
        }

        binding.tvBackToLogin.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun signUpUser(email: String, password: String) {
        memeViewModel.onCreateNewUser(email, password).observe(viewLifecycleOwner, { result ->
            progressDialog.isVisible(result is ApiResult.Loading)
            when(result) {
                is ApiResult.Success -> {
                    "Successfully Registered".showToast(requireContext())
                    val action = SignupFragmentDirections.actionSignupFragmentToMemeListFragment()
                    findNavController().navigate(action)
                }
                is ApiResult.Error -> {
                    result.message?.showToast(requireContext())
                }
                ApiResult.Loading -> Unit

            }
        })
    }
}