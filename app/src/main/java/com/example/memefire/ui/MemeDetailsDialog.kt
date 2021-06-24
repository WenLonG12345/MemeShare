package com.example.memefire.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.memefire.R
import com.example.memefire.databinding.DialogMemeDetailsBinding
import com.example.memefire.model.ApiResult
import com.example.memefire.model.MemeEvent
import com.example.memefire.utils.setWidthPercent
import com.example.memefire.utils.showToast
import com.example.memefire.viewmodel.MemeViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MemeDetailsDialog(): DialogFragment(R.layout.dialog_meme_details) {

    private lateinit var binding: DialogMemeDetailsBinding
    private val viewModel by activityViewModels<MemeViewModel>()
    private val args by navArgs<MemeDetailsDialogArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = DialogMemeDetailsBinding.bind(view)

        setWidthPercent(90)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val meme = args.meme

        with(binding) {
           meme?.let {
               tvAuthor.text = meme.author
               tvUps.text = meme.ups.toString()

               if(meme.isfavoruite) {
                   ivFavourite.setImageResource(R.drawable.ic_favourite)
               } else {
                   ivFavourite.setImageResource(R.drawable.ic_unfavourite)
               }

               ivMeme.load(meme.url) {
                   placeholder(R.drawable.ic_meme_placeholder)
               }

               ivFavourite.setOnClickListener {
                   if(!meme.isfavoruite) {
                       viewModel.onAddFavMeme(meme)
                       ivFavourite.setImageResource(R.drawable.ic_favourite)
                   } else {
                       MaterialAlertDialogBuilder(requireContext())
                           .setCancelable(false)
                           .setMessage("Are you sure to remove this meme? Removed meme possibly cannot be found again.")
                           .setPositiveButton("Sure") { alertDialog, _ ->
                               viewModel.onRemoveFavMeme(meme)
                               alertDialog.dismiss()
                               dialog?.dismiss()
                           }
                           .setNegativeButton("Cancel") { dialog, _ ->
                               dialog.dismiss()
                           }
                           .show()
                   }

               }

               ivShare.setOnClickListener {
                   viewModel.onGetBitmap(requireContext(), meme).observe(viewLifecycleOwner, { bmpUri ->
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
        }

    }
}