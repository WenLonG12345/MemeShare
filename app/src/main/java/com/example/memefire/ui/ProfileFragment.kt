package com.example.memefire.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.example.memefire.R
import com.example.memefire.databinding.FragmentProfileBinding
import com.example.memefire.model.ApiResult
import com.example.memefire.model.Meme
import com.example.memefire.model.MemeEvent
import com.example.memefire.ui.adapter.ProfileMemeAdapter
import com.example.memefire.utils.showToast
import com.example.memefire.viewmodel.MemeViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {
   private lateinit var binding: FragmentProfileBinding

   private val viewModel by activityViewModels<MemeViewModel>()
    private lateinit var profileMemeAdapter: ProfileMemeAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentProfileBinding.bind(view)

        setupOnClickListener()

        if(viewModel.auth.currentUser == null) {
            findNavController().navigate(R.id.loginFragment)
        }

        // User Profile
        viewModel.auth.currentUser?.let { user ->
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
                    viewModel.onGetBitmap(requireContext(), it).observe(viewLifecycleOwner, { bmpUri ->
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

        binding.rvProfileMeme.apply {
            adapter = profileMemeAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }

        viewModel.onGetFavMeme().observe(viewLifecycleOwner, { result ->
            binding.progressBar.isVisible = result is ApiResult.Loading
            binding.tvEmptyResult.isVisible = result is ApiResult.Error

            when(result) {
                is ApiResult.Success -> {
                    viewModel.profileMemeList = result.data
                    profileMemeAdapter.setData(viewModel.profileMemeList)
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
                    viewModel.onRemoveFavMeme(meme).observe(viewLifecycleOwner, { result ->
                        when(result) {
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

        binding.ivLogout.setOnClickListener {
            viewModel.auth.signOut()
            findNavController().navigateUp()
        }
    }

}