package com.example.memefire.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


fun View.show(): View {
    if(visibility != View.VISIBLE) {
        alpha = 1f
        visibility = View.VISIBLE
    }
    return this
}

fun View.hide(): View {
    if(visibility != View.GONE) {
        visibility = View.GONE
    }
    return this
}

fun Any.showToast(context: Context, duration: Int = Toast.LENGTH_SHORT): Toast {
    return Toast.makeText(context, this.toString(), duration).apply { show() }
}

fun String.isEmailValid(): Boolean {
    return !android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isPasswordValid(): Boolean {
    return this.length > 7
}

fun Context.getLocalBitmapUri(imageView: ImageView): Uri? {
    val drawable: Drawable = imageView.drawable
    val bmp = if (drawable is BitmapDrawable) {
        (imageView.drawable as BitmapDrawable).bitmap
    } else null

    var bmpUri: Uri? = null
    if(bmp != null) {
        try {
            val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png")
            val out = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.close()
            bmpUri = Uri.fromFile(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return bmpUri
}

fun DialogFragment.setWidthPercent(percentage: Int) {
    val percent = percentage.toFloat() / 100
    val dm = Resources.getSystem().displayMetrics
    val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
    val percentWidth = rect.width() * percent
    dialog?.window?.setLayout(percentWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
}
