package com.furianrt.mydiary.authentication

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.Fragment
import com.furianrt.mydiary.R
import com.furianrt.mydiary.authentication.login.LoginFragment
import com.furianrt.mydiary.authentication.registration.RegistrationFragment
import com.furianrt.mydiary.main.MainActivity
import com.furianrt.mydiary.note.fragments.notefragment.inTransaction
import com.furianrt.mydiary.utils.KeyboardUtils
import com.furianrt.mydiary.utils.hideKeyboard
import kotlinx.android.synthetic.main.fragment_auth.view.*
import javax.inject.Inject

class AuthFragment : Fragment(), AuthContract.View {

    companion object {
        const val TAG = "AuthFragment"
    }

    @Inject
    lateinit var mPresenter: AuthContract.Presenter

    private val mOnKeyboardToggleListener = object : KeyboardUtils.SoftKeyboardToggleListener {
        override fun onToggleSoftKeyboard(isVisible: Boolean) {
            if (isVisible) {
                view?.let {
                    it.card_auth_container
                            .animate()
                            .translationY(-it.card_auth_container.y)
                            .setDuration(600L)
                            .setInterpolator(OvershootInterpolator())
                            .setListener(object : Animator.AnimatorListener {
                                override fun onAnimationRepeat(animation: Animator?) {}
                                override fun onAnimationCancel(animation: Animator?) {}
                                override fun onAnimationStart(animation: Animator?) {}
                                override fun onAnimationEnd(animation: Animator?) {
                                    //повторная прорисовка контекстного меню
                                    it.card_auth_container.requestLayout()   //todo иногда обрезается контекстное меню
                                }
                            })
                            .start()
                }
            } else {
                view?.card_auth_container
                        ?.animate()
                        ?.translationY(0f)
                        ?.setDuration(600L)
                        ?.setInterpolator(OvershootInterpolator())
                        ?.start()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(context!!).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_auth, container, false)

        KeyboardUtils.addKeyboardToggleListener(activity!!, mOnKeyboardToggleListener)

        view.button_auth_close.setOnClickListener { mPresenter.onButtonCloseClick() }
        view.button_create_account.setOnClickListener { mPresenter.onButtonCreateAccountClick() }

        if (childFragmentManager.findFragmentByTag(LoginFragment.TAG) == null) {
            childFragmentManager.inTransaction {
                add(R.id.card_auth_container, LoginFragment(), LoginFragment.TAG)
            }
        }

        return view
    }

    override fun showRegistrationView() {
        if (childFragmentManager.findFragmentByTag(RegistrationFragment.TAG) == null) {
            activity?.supportFragmentManager?.inTransaction {
                setPrimaryNavigationFragment(this@AuthFragment)
            }
            childFragmentManager.inTransaction {
                setCustomAnimations(R.anim.from_right, R.anim.to_left, R.anim.from_left, R.anim.to_right)
                replace(R.id.card_auth_container, RegistrationFragment(), RegistrationFragment.TAG)
                addToBackStack(null)
            }
        }
        view?.card_create_account?.startAnimation(
                AnimationUtils
                        .loadAnimation(context, R.anim.to_bottom)
                        .apply {
                            startOffset = 300L
                            fillAfter = true
                        }
        )
    }

    override fun closeSheet() {
        (activity as? MainActivity?)?.closeBottomSheet()
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

    fun onRegistrationFragmentDetach() {
        view?.card_create_account?.startAnimation(
                AnimationUtils
                        .loadAnimation(context, R.anim.from_bottom)
                        .apply { fillAfter = true }
        )
    }

    fun isBackStackEmpty() = childFragmentManager.backStackEntryCount == 0

    fun clearFocus() {
        activity?.hideKeyboard()
        view?.card_auth_container?.clearFocus()
    }
}
