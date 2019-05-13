package com.furianrt.mydiary.screens.main.fragments.authentication.registration

import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.furianrt.mydiary.R
import com.furianrt.mydiary.screens.main.fragments.authentication.AuthFragment
import com.furianrt.mydiary.screens.main.fragments.authentication.done.DoneAuthFragment
import com.furianrt.mydiary.utils.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_sheet_main.*
import kotlinx.android.synthetic.main.fragment_registration.*
import kotlinx.android.synthetic.main.fragment_registration.view.*
import javax.inject.Inject

class RegistrationFragment : Fragment(), RegistrationContract.View {

    companion object {
        const val TAG = "RegistrationFragment"
        private const val ANIMATION_SHAKE_DURATION = 400L
        private const val CLOSE_AFTER_DONE_DELAY = 2000L
        private const val CHANGE_ACTIVITY_FLAG_DELAY = 200L
    }

    @Inject
    lateinit var mPresenter: RegistrationContract.Presenter

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
        val view = inflater.inflate(R.layout.fragment_registration, container, false)

        view.button_cancel.setOnClickListener { mPresenter.onButtonCancelClick() }
        view.button_sign_up.setOnClickListener {
            mPresenter.onButtonSignUpClick(
                    view.edit_email.text.toString(),
                    view.edit_password.text.toString(),
                    view.edit_password_repeat.text.toString()
            )
        }
        view.edit_email.setOnFocusChangeListener { v, hasFocus ->
            mOnEditFocusChangeListener.invoke(v, hasFocus)
            mPresenter.onEmailFocusChange(view.edit_email.text.toString(), hasFocus)
        }
        view.edit_password.setOnFocusChangeListener(mOnEditFocusChangeListener)
        view.edit_password_repeat.setOnFocusChangeListener(mOnEditFocusChangeListener)
        view.edit_email.setOnClickListener { mOnEditFocusChangeListener.invoke(it, true) }
        view.edit_password.setOnClickListener { mOnEditFocusChangeListener.invoke(it, true) }
        view.edit_password_repeat.setOnClickListener { mOnEditFocusChangeListener.invoke(it, true) }
        view.view_alpha.setOnTouchListener { _, _ -> true }
        return view
    }

    override fun isNetworkAvailable() = context?.isNetworkAvailable() ?: false

    override fun showErrorNetworkConnection() {
        text_error.text = getString(R.string.network_error)
    }

    override fun showErrorPassword() {
        layout_password.animateShake(ANIMATION_SHAKE_DURATION)
        layout_password_repeat.animateShake(ANIMATION_SHAKE_DURATION)
        text_error.text = getString(R.string.fragment_registration_password_error)
    }

    override fun showErrorEmailFormat() {
        showEmailError()
        text_error.text = getString(R.string.wrong_email_format)
    }

    override fun showErrorEmailExists() {
        showEmailError()
        text_error.text = getString(R.string.fragment_registration_error_email_exists)
    }

    override fun showMessageCorrectEmail() {
        showEmailSuccess()
    }

    override fun showMessageSuccessRegistration() {
        fragmentManager?.let {
            if (it.findFragmentByTag(DoneAuthFragment.TAG) == null) {
                it.inTransaction {
                    val message = getString(R.string.fragment_done_auth_done)
                    setCustomAnimations(R.anim.scale_up, R.anim.scale_up)
                    add(R.id.auth_container, DoneAuthFragment.newInstance(message), DoneAuthFragment.TAG)
                }
            }
        }
        activity?.let {
            it.main_sheet_container.postDelayed({
                BottomSheetBehavior.from(it.main_sheet_container).state = BottomSheetBehavior.STATE_COLLAPSED
            }, CLOSE_AFTER_DONE_DELAY)
        }
    }

    override fun showErrorShortPassword() {
        layout_password.animateShake(ANIMATION_SHAKE_DURATION)
        text_error.text = getString(R.string.fragment_registration_error_short_password)
    }

    override fun showErrorEmptyPassword() {
        layout_password.animateShake(ANIMATION_SHAKE_DURATION)
        text_error.text = getString(R.string.empty_password)
    }

    override fun showErrorEmptyPasswordRepeat() {
        layout_password_repeat.animateShake(ANIMATION_SHAKE_DURATION)
        text_error.text = getString(R.string.empty_password)
    }

    override fun showErrorEmptyEmail() {
        showEmailError()
        text_error.text = getString(R.string.empty_email)
    }

    private fun showEmailError() {
        image_email.setImageResource(R.drawable.ic_error)
        image_email.setColorFilter(ContextCompat.getColor(requireContext(), R.color.red), PorterDuff.Mode.SRC_IN)
        hideLoadingEmail()
        image_email.visibility = View.VISIBLE
    }

    private fun showEmailSuccess() {
        text_error.text = ""
        image_email.setImageResource(R.drawable.ic_done)
        image_email.setColorFilter(getThemeAccentColor(requireContext()), PorterDuff.Mode.SRC_IN)
        hideLoadingEmail()
        image_email.visibility = View.VISIBLE
    }

    override fun clearEmailMessages() {
        text_error.text = ""
        image_email.visibility = View.GONE
        hideLoadingEmail()
    }

    override fun showLoadingEmail() {
        text_error.text = ""
        image_email.visibility = View.GONE
        progress_email.visibility = View.VISIBLE
    }

    override fun hideLoadingEmail() {
        progress_email.visibility = View.GONE
    }

    override fun showLoading() {
        view_alpha.visibility = View.VISIBLE
        progress_sign_up.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        view_alpha.visibility = View.GONE
        progress_sign_up.visibility = View.GONE
    }

    override fun close() {
        fragmentManager?.popBackStack()
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

    override fun onDetach() {
        super.onDetach()
        (parentFragment as? AuthFragment?)?.showRegistrationButton()
        activity?.currentFocus?.hideKeyboard()
        activity?.currentFocus?.clearFocus()
    }
}
