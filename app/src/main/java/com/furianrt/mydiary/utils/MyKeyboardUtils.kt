package com.furianrt.mydiary.utils

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import java.util.*

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun Activity.hideKeyboard() {
    currentFocus?.let {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

class KeyboardUtils(
        activity: Activity,
        private var callback: SoftKeyboardToggleListener?
) : ViewTreeObserver.OnGlobalLayoutListener {
    private val mRootView: View =
            (activity.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0)
    private val mScreenDensity: Float

    interface SoftKeyboardToggleListener {
        fun onToggleSoftKeyboard(isVisible: Boolean)
    }

    override fun onGlobalLayout() {
        val r = Rect()
        mRootView.getWindowVisibleDisplayFrame(r)

        val heightDiff = mRootView.rootView.height - (r.bottom - r.top)
        val dp = heightDiff / mScreenDensity
        val isVisible = dp > MAGIC_NUMBER

        if (callback != null && isVisible != sPrevValue) {
            sPrevValue = isVisible
            callback?.onToggleSoftKeyboard(isVisible)
        }
    }

    private fun removeListener() {
        callback = null
        mRootView.viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    init {
        mRootView.viewTreeObserver.addOnGlobalLayoutListener(this)
        mScreenDensity = activity.resources.displayMetrics.density
    }

    companion object {
        private const val MAGIC_NUMBER = 200
        private val sListenerMap = HashMap<SoftKeyboardToggleListener, KeyboardUtils>()
        private var sPrevValue = false

        fun isKeyboardVisible() = sPrevValue

        fun addKeyboardToggleListener(act: Activity, listener: SoftKeyboardToggleListener) {
            removeKeyboardToggleListener(listener)
            sListenerMap[listener] = KeyboardUtils(act, listener)
        }

        fun removeKeyboardToggleListener(listener: SoftKeyboardToggleListener) {
            if (sListenerMap.containsKey(listener)) {
                val k = sListenerMap[listener]
                k?.removeListener()
                sListenerMap.remove(listener)
            }
        }
    }
}