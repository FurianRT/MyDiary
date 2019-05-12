package com.furianrt.mydiary.screens.main.fragments.authentication.login

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.furianrt.mydiary.R
import com.furianrt.mydiary.screens.main.fragments.authentication.AuthFragment
import com.furianrt.mydiary.screens.main.fragments.authentication.done.DoneAuthFragment
import com.furianrt.mydiary.screens.main.fragments.authentication.forgot.ForgotPassFragment
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
        private const val CHANGE_ACTIVITY_FLAG_DELAY = 200L
    }

    @Inject
    lateinit var mPresenter: LoginContract.Presenter

    private val mHandler = Handler()
    private val mChangeActivityFlag: Runnable = Runnable {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }
    private val mOnEditFocusChangeListener: (v: View, hasFocus: Boolean) -> Unit = { _, hasFocus ->
        if (hasFocus) {
            (parentFragment as AuthFragment).pushContainerUp()
            mHandler.postDelayed(mChangeActivityFlag, CHANGE_ACTIVITY_FLAG_DELAY)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        view.button_forgot_password.setOnClickListener {
            (parentFragment as AuthFragment).hideRegistrationButton()
            mPresenter.onButtonForgotClick(edit_email.text?.toString() ?: "")
        }
        view.button_sing_in.setOnClickListener {
            mPresenter.onButtonSignInClick(
                    view.edit_email.text?.toString() ?: "",
                    view.edit_password.text?.toString() ?: ""
            )
        }
        view.edit_email.setOnFocusChangeListener(mOnEditFocusChangeListener)
        view.edit_password.setOnFocusChangeListener(mOnEditFocusChangeListener)
        view.edit_email.setOnClickListener { mOnEditFocusChangeListener.invoke(it, true) }
        view.edit_password.setOnClickListener { mOnEditFocusChangeListener.invoke(it, true) }
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
        Toast.makeText(requireContext(), getString(R.string.empty_password), Toast.LENGTH_SHORT).show()
    }

    override fun showErrorEmptyEmail() {
        layout_email.animateShake(ANIMATION_SHAKE_DURATION)
        Toast.makeText(requireContext(), getString(R.string.empty_email), Toast.LENGTH_SHORT).show()
    }

    override fun showErrorWrongCredential() {
        layout_password.animateShake(ANIMATION_SHAKE_DURATION)
        layout_email.animateShake(ANIMATION_SHAKE_DURATION)
        Toast.makeText(requireContext(), getString(R.string.fragment_login_wrong_credential), Toast.LENGTH_SHORT).show()
    }

    override fun showLoading() {
        image_logo.setImageResource(R.drawable.diary_logo)
        view_alpha.visibility = View.VISIBLE
        progress_sign_in.visibility = View.VISIBLE
        (parentFragment as? AuthFragment?)?.enableSignUpButton(false)
    }

    override fun hideLoading() {
        view_alpha.visibility = View.GONE
        progress_sign_in.visibility = View.INVISIBLE
        (parentFragment as? AuthFragment?)?.enableSignUpButton(true)
    }

    override fun showLoginSuccess() {
        if (fragmentManager?.findFragmentByTag(DoneAuthFragment.TAG) == null) {
            fragmentManager?.inTransaction {
                val message = getString(R.string.fragment_done_auth_done)
                setCustomAnimations(R.anim.scale_up, R.anim.scale_up)
                add(R.id.auth_container, DoneAuthFragment.newInstance(message), DoneAuthFragment.TAG)
            }
        }

        activity?.main_sheet_container?.postDelayed({
            activity?.let {
                BottomSheetBehavior.from(it.main_sheet_container).state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }, CLOSE_AFTER_DONE_DELAY)
    }

    override fun showForgotPassView(email: String) {
        if (fragmentManager?.findFragmentByTag(ForgotPassFragment.TAG) == null) {
            activity?.supportFragmentManager?.inTransaction {
                setPrimaryNavigationFragment(parentFragment)
            }
            fragmentManager?.inTransaction {
                setCustomAnimations(R.anim.from_right, R.anim.to_left, R.anim.from_left, R.anim.to_right)
                replace(R.id.auth_container, ForgotPassFragment.newInstance(email), ForgotPassFragment.TAG)
                addToBackStack(null)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mPresenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        mHandler.removeCallbacks(mChangeActivityFlag)
        mPresenter.detachView()
    }
}
