package com.example.memefire.ui

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.memefire.R
import com.example.memefire.databinding.FragmentLoginBinding
import com.example.memefire.model.ApiResult
import com.example.memefire.utils.isEmailValid
import com.example.memefire.utils.isPasswordValid
import com.example.memefire.utils.showToast
import com.example.memefire.viewmodel.MemeViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding

    private val gso by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val memeViewModel by activityViewModels<MemeViewModel>()
    @Inject lateinit var progressDialog: CustomProgressDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentLoginBinding.bind(view)

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.signupFragment)
        }

        binding.btnGoogleSignIn.setOnClickListener {
            googleSignIn()
        }

        binding.btnLogin.setOnClickListener {
            when {
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
                    loginUser(binding.etEmail.text.toString(), binding.etPassword.text.toString())
                }
            }
        }
    }

    private fun googleSignIn() {
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        val signInIntent = mGoogleSignInClient.signInIntent
        googleSignInResult.launch(signInIntent)
    }

    private val googleSignInResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val idToken = task.result?.idToken
                    idToken?.let {
                        memeViewModel.onFirebaseAuthWithGoogle(it).observe(viewLifecycleOwner, { result ->
                            progressDialog.isVisible(result is ApiResult.Loading)
                            when(result) {
                                is ApiResult.Error -> result.message?.showToast(requireContext())
                                ApiResult.Loading -> Unit
                                is ApiResult.Success -> {
                                    "Successfully Log In".showToast(requireContext())
                                    findNavController().navigateUp()
                                }
                            }

                        })
                    }
                }catch (e: Exception) {
                    Timber.e("Google Sign in Failed - $e")
                }
            }
        }

    private fun loginUser(email: String, password: String) {
        memeViewModel.onSignIn(email, password).observe(viewLifecycleOwner, { result ->
            progressDialog.isVisible(result is ApiResult.Loading)
            when (result) {
                is ApiResult.Error -> result.message?.showToast(requireContext())
                ApiResult.Loading -> Unit
                is ApiResult.Success -> {
                    "Successfully Log In".showToast(requireContext())
                    findNavController().navigateUp()
                }
            }
        })
    }

}