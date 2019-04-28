package com.furianrt.mydiary.screens.main.fragments.authentication.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.furianrt.mydiary.R
import com.furianrt.mydiary.screens.main.fragments.authentication.done.DoneAuthFragment
import com.furianrt.mydiary.utils.animateShake
import com.furianrt.mydiary.utils.inTransaction
import com.furianrt.mydiary.utils.isNetworkAvailable
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_sheet_main.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import javax.inject.Inject

class LoginFragment : Fragment(), LoginContract.View {

    companion object {
        const val TAG = "LoginFragment"
        private const val CLOSE_AFTER_DONE_DELAY = 2000L
        private const val ANIMATION_SHAKE_DURATION = 400L
    }

    @Inject
    lateinit var mPresenter: LoginContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        view.button_forgot_password.setOnClickListener { mPresenter.onButtonForgotClick() }
        view.button_sing_in.setOnClickListener {
            mPresenter.onButtonSignInClick(
                    view.edit_email.text?.toString() ?: "",
                    view.edit_password.text?.toString() ?: ""
            )
        }
        view.view_alpha.setOnTouchListener { _, _ -> true }

        return view
    }

    override fun isNetworkAvailable() = context?.isNetworkAvailable() ?: false

    override fun showErrorNetworkConnection() {
        image_logo.setImageResource(R.drawable.image_disconnected)
        Toast.makeText(requireContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show()
    }

    override fun showErrorEmptyPassword() {
        layout_password.animateShake(ANIMATION_SHAKE_DURATION)
    }

    override fun showErrorEmptyEmail() {
        layout_email.animateShake(ANIMATION_SHAKE_DURATION)
    }

    override fun showErrorWrongCredential() {
        layout_password.animateShake(ANIMATION_SHAKE_DURATION)
        layout_email.animateShake(ANIMATION_SHAKE_DURATION)
    }

    override fun showLoading() {
        image_logo.setImageResource(R.drawable.diary_logo)
        view_alpha.visibility = View.VISIBLE
        progress_sign_in.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        view_alpha.visibility = View.GONE
        progress_sign_in.visibility = View.INVISIBLE
    }

    override fun showLoginSuccess() {
        fragmentManager?.let {
            if (it.findFragmentByTag(DoneAuthFragment.TAG) == null) {
                it.inTransaction {
                    setCustomAnimations(R.anim.scale_up, R.anim.scale_up)
                    add(R.id.auth_container, DoneAuthFragment(), DoneAuthFragment.TAG)
                }
            }
        }
        activity?.let {
            it.main_sheet_container.postDelayed({
                BottomSheetBehavior.from(it.main_sheet_container).state = BottomSheetBehavior.STATE_COLLAPSED
            }, CLOSE_AFTER_DONE_DELAY)
        }
    }

    override fun onResume() {
        super.onResume()
        mPresenter.attachView(this)
    }

    override fun onPause() {
        super.onPause()
        mPresenter.detachView()
    }
}
