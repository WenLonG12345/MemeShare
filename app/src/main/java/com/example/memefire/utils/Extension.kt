package com.example.memefire.utils

import android.content.Context
import android.view.View
import android.widget.Toast

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
