package com.furianrt.mydiary.utils

import android.content.Context
import android.view.inputmethod.InputMethodManager

fun showKeyboard(context: Context) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT)
}