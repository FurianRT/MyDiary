package com.furianrt.mydiary.main.fragments.profile

import android.animation.Animator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.Fragment
import com.furianrt.mydiary.R
import com.furianrt.mydiary.main.MainActivity
import com.furianrt.mydiary.main.fragments.profile.menu.MenuProfileFragment
import com.furianrt.mydiary.utils.KeyboardUtils
import com.furianrt.mydiary.utils.inTransaction
import kotlinx.android.synthetic.main.fragment_profile.view.*
import javax.inject.Inject

class ProfileFragment : Fragment(), ProfileContract.View {

    companion object {
        const val TAG = "ProfileFragment"
        private const val ANIMATION_CONTAINER_DURATION = 800L
    }

    @Inject
    lateinit var mPresenter: ProfileContract.Presenter

    private val mOnKeyboardToggleListener = object : KeyboardUtils.SoftKeyboardToggleListener {
        override fun onToggleSoftKeyboard(isVisible: Boolean) {
            if (!isVisible) {
                activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
                view?.profile_container
                        ?.animate()
                        ?.translationY(0f)
                        ?.setDuration(ANIMATION_CONTAINER_DURATION)
                        ?.interpolator = OvershootInterpolator()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(context!!).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        KeyboardUtils.addKeyboardToggleListener(activity!!, mOnKeyboardToggleListener)

        view.button_profile_close.setOnClickListener { mPresenter.onButtonCloseClick() }

        if (childFragmentManager.findFragmentByTag(MenuProfileFragment.TAG) == null) {
            childFragmentManager.inTransaction {
                add(R.id.profile_container, MenuProfileFragment(), MenuProfileFragment.TAG)
            }
        }

        return view
    }

    override fun close() {
        (activity as? MainActivity?)?.closeBottomSheet()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

    override fun onDetach() {
        super.onDetach()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onStart() {
        super.onStart()
        mPresenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        mPresenter.detachView()
    }

    override fun onDestroy() {
        super.onDestroy()
        KeyboardUtils.removeKeyboardToggleListener(mOnKeyboardToggleListener)
    }

    fun isBackStackEmpty() = childFragmentManager.backStackEntryCount == 0

    fun pushContainerUp() {
        view?.let {
            if (it.profile_container.translationY == 0f) {
                it.profile_container
                        .animate()
                        .translationY(-it.profile_container.y)
                        .setDuration(ANIMATION_CONTAINER_DURATION)
                        .setInterpolator(OvershootInterpolator())
                        .setListener(object : Animator.AnimatorListener {
                            override fun onAnimationRepeat(animation: Animator?) {}
                            override fun onAnimationCancel(animation: Animator?) {}
                            override fun onAnimationStart(animation: Animator?) {}
                            override fun onAnimationEnd(animation: Animator?) {
                                //повторная прорисовка контекстного меню
                                it.profile_container.requestLayout()   //todo иногда обрезается контекстное меню
                            }
                        })
            }
        }
    }
}