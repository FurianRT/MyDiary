package com.furianrt.mydiary.main.fragments.splash

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.fragment.app.Fragment
import com.furianrt.mydiary.R
import com.furianrt.mydiary.utils.inTransaction
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_splash.*

class SplashFragment : Fragment() {

    companion object {
        const val TAG = "SplashFragment"
        private const val CLOSE_DELAY = 2000L
    }

    private val mHandler = Handler()
    private val mCloseRunnable = Runnable {
        activity?.layout_main_root?.viewTreeObserver?.removeOnGlobalLayoutListener(mOnGlobalLayoutListener)
        fragmentManager?.inTransaction {
            setCustomAnimations(R.anim.to_bottom, R.anim.to_bottom)
            remove(this@SplashFragment)
        }
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
    }

    private val mOnGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val mWidth = this.resources.displayMetrics.widthPixels
        val mHeight = this.resources.displayMetrics.heightPixels
        image_logo?.let {
            it.x = mWidth / 2f - it.width / 2f
            it.y = mHeight / 2f - it.height / 2f + 80f
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_splash, container, false)

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        activity?.layout_main_root?.viewTreeObserver?.addOnGlobalLayoutListener(mOnGlobalLayoutListener)
        mHandler.postDelayed(mCloseRunnable, CLOSE_DELAY)
    }

    override fun onDetach() {
        super.onDetach()
        mHandler.removeCallbacks(mCloseRunnable)
    }
}
