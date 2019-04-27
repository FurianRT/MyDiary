package com.furianrt.mydiary.main.fragments.authentication

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.Fragment
import com.furianrt.mydiary.R
import com.furianrt.mydiary.main.MainActivity
import com.furianrt.mydiary.main.fragments.authentication.login.LoginFragment
import com.furianrt.mydiary.main.fragments.authentication.registration.RegistrationFragment
import com.furianrt.mydiary.utils.KeyboardUtils
import com.furianrt.mydiary.utils.dpToPx
import com.furianrt.mydiary.utils.hideKeyboard
import com.furianrt.mydiary.utils.inTransaction
import kotlinx.android.synthetic.main.fragment_auth.*
import kotlinx.android.synthetic.main.fragment_auth.view.*
import javax.inject.Inject

class AuthFragment : Fragment(), AuthContract.View {

    companion object {
        const val TAG = "AuthFragment"
        private const val ANIMATION_BUTTON_DURATION = 500L
        private const val ANIMATION_BUTTON_START_DELAY = 400L
        private const val ANIMATION_BUTTON_TRANSLATION_VALUE_DP = 100f
        private const val ANIMATION_CONTAINER_DURATION = 600L
        private const val BUNDLE_CREATE_BUTTON_TRANSLATION_Y = "createButtonTranslationY"
    }

    @Inject
    lateinit var mPresenter: AuthContract.Presenter

    private val mOnKeyboardToggleListener = object : KeyboardUtils.SoftKeyboardToggleListener {
        override fun onToggleSoftKeyboard(isVisible: Boolean) {
            if (isVisible) {
                auth_container
                        .animate()
                        .translationY(-auth_container.y)
                        .setDuration(ANIMATION_CONTAINER_DURATION)
                        .setInterpolator(OvershootInterpolator())
                        .setListener(object : Animator.AnimatorListener {
                            override fun onAnimationRepeat(animation: Animator?) {}
                            override fun onAnimationCancel(animation: Animator?) {}
                            override fun onAnimationStart(animation: Animator?) {}
                            override fun onAnimationEnd(animation: Animator?) {
                                //повторная прорисовка контекстного меню
                                auth_container?.requestLayout()   //todo иногда обрезается контекстное меню
                            }
                        })
            } else {
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_auth, container, false)

        view.button_auth_close.setOnClickListener { mPresenter.onButtonCloseClick() }
        view.button_create_account.setOnClickListener { mPresenter.onButtonCreateAccountClick() }

        savedInstanceState?.let {
            view.card_create_account.translationY = it.getFloat(BUNDLE_CREATE_BUTTON_TRANSLATION_Y, 0f)
        }

        if (childFragmentManager.findFragmentByTag(LoginFragment.TAG) == null) {
            childFragmentManager.inTransaction {
                add(R.id.auth_container, LoginFragment(), LoginFragment.TAG)
            }
        }

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putFloat(BUNDLE_CREATE_BUTTON_TRANSLATION_Y, card_create_account.translationY)
        super.onSaveInstanceState(outState)
    }

    override fun showRegistrationView() {
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
        card_create_account.animate()
                .translationY(dpToPx(ANIMATION_BUTTON_TRANSLATION_VALUE_DP).toFloat())
                .setDuration(ANIMATION_BUTTON_DURATION)
                .setInterpolator(AnticipateOvershootInterpolator())
                .startDelay = ANIMATION_BUTTON_START_DELAY
    }

    override fun closeSheet() {
        (activity as? MainActivity?)?.closeBottomSheet()
    }

    override fun onResume() {
        super.onResume()
        mPresenter.attachView(this)
        KeyboardUtils.addKeyboardToggleListener(requireActivity(), mOnKeyboardToggleListener)
    }

    override fun onPause() {
        super.onPause()
        KeyboardUtils.removeKeyboardToggleListener(mOnKeyboardToggleListener)
        mPresenter.detachView()
    }

    fun onRegistrationFragmentDetach() {
        card_create_account?.animate()
                ?.translationY(0f)
                ?.setDuration(ANIMATION_BUTTON_DURATION)
                ?.interpolator = OvershootInterpolator()
    }

    fun isBackStackEmpty() = childFragmentManager.backStackEntryCount == 0

    fun clearFocus() {
        activity?.hideKeyboard()
        auth_container?.clearFocus()
    }
}
