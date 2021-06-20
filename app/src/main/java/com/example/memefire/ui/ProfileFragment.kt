package com.example.memefire.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.memefire.R
import com.example.memefire.databinding.FragmentProfileBinding


class ProfileFragment : Fragment(R.layout.fragment_profile) {
   private lateinit var binding: FragmentProfileBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentProfileBinding.bind(view)


    }
}