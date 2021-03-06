package com.example.memefire.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.example.memefire.R
import com.example.memefire.databinding.FragmentProfileBinding
import com.example.memefire.model.ApiResult
import com.example.memefire.model.Meme
import com.example.memefire.ui.LoginFragment.Companion.LOGIN_SUCCESSFUL
import com.example.memefire.ui.adapter.ProfileHeaderAdapter
import com.example.memefire.ui.adapter.ProfileMemeAdapter
import com.example.memefire.utils.showToast
import com.example.memefire.viewmodel.MemeViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var binding: FragmentProfileBinding

    private val viewModel by activityViewModels<MemeViewModel>()
    private lateinit var profileMemeAdapter: ProfileMemeAdapter
    private lateinit var concatAdapter: ConcatAdapter
    private val profileHeaderAdapter by lazy { ProfileHeaderAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentProfileBinding.bind(view)

        val savedStateHandle = findNavController().currentBackStackEntry?.savedStateHandle
        savedStateHandle?.getLiveData<Boolean>(LOGIN_SUCCESSFUL)
            ?.observe(viewLifecycleOwner, { success ->
                if (!success) {
                    val startDestination = findNavController().graph.startDestination
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(startDestination, true)
                        .build()
                    findNavController().navigate(startDestination, null, navOptions)
                }
            })

        setupOnClickListener()

        if (viewModel.auth.currentUser == null) {
            findNavController().navigate(R.id.loginFragment)
        }

        // User Profile
        viewModel.auth.currentUser?.let { user ->
            binding.collapsingToolbar.title = if(user.displayName == null) user.email else user.displayName
            user.photoUrl?.let {
                binding.ivAvatar.load(it) {
                    placeholder(R.drawable.ic_profile)
                    transformations(CircleCropTransformation())
                }
            }

            binding.tvUsername.text = user.displayName
            binding.tvEmail.text = user.email
        }

        // Fav Meme
        profileMemeAdapter = ProfileMemeAdapter(
            onFavouriteClick = { onClearFavouriteMeme(it) },
            onShareClick = { meme ->
                meme?.let {
                    viewModel.onGetBitmap(requireContext(), it)
                        .observe(viewLifecycleOwner, { bmpUri ->
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_STREAM, bmpUri)
                                type = "image/*"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Share Meme")
                            startActivity(shareIntent)
                        })
                }
            }
        )

        val listOfAdapters = listOf(profileMemeAdapter)
        concatAdapter = ConcatAdapter(listOfAdapters)

        binding.rvProfileMeme.apply {
            adapter = concatAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }

        viewModel.onGetFavMeme().observe(viewLifecycleOwner, { result ->
            binding.memeLoading.isVisible = result is ApiResult.Loading
            binding.tvEmptyResult.isVisible = result is ApiResult.Error

            when (result) {
                is ApiResult.Success -> {
                    viewModel.profileMemeList = result.data
                    profileMemeAdapter.setData(viewModel.profileMemeList)
                    profileHeaderAdapter.setCount(viewModel.profileMemeList.size)

                    // after profileMemeAdapter load finish, only add to its header
                    concatAdapter.addAdapter(0, profileHeaderAdapter)
                }
                is ApiResult.Error -> result.message?.showToast(requireContext())
                ApiResult.Loading -> Unit
            }
        })
    }

    private fun onClearFavouriteMeme(meme: Meme?) {
        MaterialAlertDialogBuilder(requireContext())
            .setCancelable(false)
            .setMessage("Are you sure to remove this meme? Removed meme possibly cannot be found again.")
            .setPositiveButton("Sure") { dialog, _ ->
                meme?.let {
                    viewModel.onRemoveProfileFavMeme(meme).observe(viewLifecycleOwner, { result ->
                        when (result) {
                            is ApiResult.Error -> result.message?.showToast(requireContext())
                            ApiResult.Loading -> Unit
                            is ApiResult.Success -> {
                                profileMemeAdapter.deleteItem(meme)
                                dialog.dismiss()
                            }
                        }
                    })
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun setupOnClickListener() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_logout -> {
                    viewModel.auth.signOut()
                    findNavController().navigateUp()
                }
            }
            false
        }
    }

}