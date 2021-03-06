/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.fragments.authentication

import android.animation.Animator
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.OvershootInterpolator
import com.furianrt.mydiary.R
import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.presentation.base.BaseFragment
import com.furianrt.mydiary.presentation.screens.main.MainBottomSheetHolder
import com.furianrt.mydiary.presentation.screens.main.fragments.authentication.login.LoginFragment
import com.furianrt.mydiary.presentation.screens.main.fragments.authentication.registration.RegistrationFragment
import com.furianrt.mydiary.utils.KeyboardUtils
import com.furianrt.mydiary.utils.dpToPx
import com.furianrt.mydiary.utils.hideKeyboard
import com.furianrt.mydiary.utils.inTransaction
import kotlinx.android.synthetic.main.fragment_auth.*
import javax.inject.Inject

class AuthFragment : BaseFragment(R.layout.fragment_auth), AuthContract.View {

    companion object {
        const val TAG = "AuthFragment"
        private const val ANIMATION_BUTTON_DURATION = 500L
        private const val ANIMATION_BUTTON_START_DELAY = 400L
        private const val ANIMATION_BUTTON_TRANSLATION_VALUE_DP = 100f
        private const val ANIMATION_CONTAINER_DURATION = 600L
        private const val BUNDLE_CREATE_BUTTON_TRANSLATION_Y = "createButtonTranslationY"
    }

    @Inject
    lateinit var presenter: AuthContract.Presenter

    private var mListener: MainBottomSheetHolder? = null

    private val mOnKeyboardToggleListener = object : KeyboardUtils.SoftKeyboardToggleListener {
        override fun onToggleSoftKeyboard(isVisible: Boolean) {
            if (!isVisible) {
                activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
                auth_container
                        .animate()
                        .translationY(0f)
                        .setDuration(ANIMATION_CONTAINER_DURATION)
                        .interpolator = OvershootInterpolator()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_auth_close.setOnClickListener { presenter.onButtonCloseClick() }
        button_create_account.setOnClickListener { presenter.onButtonCreateAccountClick() }
        image_create_account.setOnClickListener { presenter.onButtonCreateAccountClick() }

        savedInstanceState?.let {
            card_create_account.translationY = it.getFloat(BUNDLE_CREATE_BUTTON_TRANSLATION_Y, 0f)
        }

        if (childFragmentManager.findFragmentByTag(LoginFragment.TAG) == null) {
            childFragmentManager.inTransaction {
                add(R.id.auth_container, LoginFragment(), LoginFragment.TAG)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putFloat(BUNDLE_CREATE_BUTTON_TRANSLATION_Y, card_create_account.translationY)
        super.onSaveInstanceState(outState)
    }

    override fun showRegistrationView() {
        analytics.sendEvent(MyAnalytics.EVENT_SIGN_UP)
        if (childFragmentManager.findFragmentByTag(RegistrationFragment.TAG) == null) {
            activity?.supportFragmentManager?.inTransaction {
                setPrimaryNavigationFragment(this@AuthFragment)
            }
            childFragmentManager.inTransaction {
                setCustomAnimations(R.anim.from_right, R.anim.to_left, R.anim.from_left, R.anim.to_right)
                replace(R.id.auth_container, RegistrationFragment(), RegistrationFragment.TAG)
                addToBackStack(null)
            }
        }
        hideRegistrationButton()
    }

    override fun close() {
        mListener?.closeBottomSheet()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainBottomSheetHolder) {
            mListener = context
        } else {
            throw IllegalStateException()
        }
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

    override fun onDetach() {
        super.onDetach()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        activity?.currentFocus?.hideKeyboard()
        activity?.currentFocus?.clearFocus()
        mListener = null
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
        KeyboardUtils.addKeyboardToggleListener(requireActivity(), mOnKeyboardToggleListener)
    }

    override fun onStop() {
        super.onStop()
        KeyboardUtils.removeKeyboardToggleListener(mOnKeyboardToggleListener)
        presenter.detachView()
    }

    fun showRegistrationButton() {
        card_create_account?.animate()
                ?.translationY(0f)
                ?.setDuration(ANIMATION_BUTTON_DURATION)
                ?.setInterpolator(OvershootInterpolator())
                ?.setListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {}
                    override fun onAnimationCancel(animation: Animator?) {}
                    override fun onAnimationStart(animation: Animator?) {}
                    override fun onAnimationEnd(animation: Animator?) {
                        button_create_account?.isEnabled = true
                    }
                })
    }

    fun hideRegistrationButton() {
        button_create_account?.isEnabled = false
        card_create_account.animate()
                .translationY(dpToPx(ANIMATION_BUTTON_TRANSLATION_VALUE_DP).toFloat())
                .setDuration(ANIMATION_BUTTON_DURATION)
                .setInterpolator(AnticipateOvershootInterpolator())
                .startDelay = ANIMATION_BUTTON_START_DELAY
    }

    fun isBackStackEmpty() = childFragmentManager.backStackEntryCount == 0

    fun pushContainerUp() {
        if (auth_container?.translationY == 0f) {
            auth_container
                    ?.animate()
                    ?.translationY(-auth_container.y)
                    ?.setDuration(ANIMATION_CONTAINER_DURATION)
                    ?.setInterpolator(OvershootInterpolator())
                    ?.setListener(object : Animator.AnimatorListener {
                        override fun onAnimationRepeat(animation: Animator?) {}
                        override fun onAnimationCancel(animation: Animator?) {}
                        override fun onAnimationStart(animation: Animator?) {}
                        override fun onAnimationEnd(animation: Animator?) {
                            //повторная прорисовка контекстного меню
                            auth_container?.requestLayout()   //todo иногда обрезается контекстное меню
                        }
                    })
        }
    }

    fun enableSignUpButton(enable: Boolean) {
        button_create_account.isEnabled = enable
    }
}
