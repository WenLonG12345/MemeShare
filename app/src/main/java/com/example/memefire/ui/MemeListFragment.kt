package com.example.memefire.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memefire.R
import com.example.memefire.databinding.FragmentMemeListBinding
import com.example.memefire.model.ApiResult
import com.example.memefire.ui.adapter.MemeListAdapter
import com.example.memefire.utils.showToast
import com.example.memefire.viewmodel.MemeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MemeListFragment : Fragment(R.layout.fragment_meme_list) {

    private lateinit var binding: FragmentMemeListBinding
    private val memeViewModel by activityViewModels<MemeViewModel>()
    private val memeAdapter: MemeListAdapter by lazy { MemeListAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentMemeListBinding.bind(view)

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        binding.rvMeme.apply {
            adapter = memeAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }

        memeViewModel.meme.observe(viewLifecycleOwner, { result ->
            binding.progressBar.isVisible = result is ApiResult.Loading
            binding.tvEmptyResult.isVisible = result is ApiResult.Error

            when(result) {
                is ApiResult.Success -> {
                    memeAdapter.submitList(result.data)
                }
                is ApiResult.Error -> result.message?.showToast(requireContext())
                ApiResult.Loading -> Unit
            }
        })
    }

}