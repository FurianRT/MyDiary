package com.furianrt.mydiary.main.fragments.authentication.registration

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.furianrt.mydiary.R
import com.furianrt.mydiary.main.fragments.authentication.AuthFragment
import com.furianrt.mydiary.main.fragments.authentication.done.DoneAuthFragment
import com.furianrt.mydiary.utils.animateShake
import com.furianrt.mydiary.utils.getThemeAccentColor
import com.furianrt.mydiary.utils.inTransaction
import com.furianrt.mydiary.utils.isNetworkAvailable
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_sheet_main.*
import kotlinx.android.synthetic.main.fragment_registration.view.*
import javax.inject.Inject

class RegistrationFragment : Fragment(), RegistrationContract.View {

    companion object {
        const val TAG = "RegistrationFragment"
        private const val SHAKE_DURATION = 400L
        private const val CLOSE_AFTER_DONE_DELAY = 2000L
    }

    @Inject
    lateinit var mPresenter: RegistrationContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(context!!).inject(this)
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
        view.edit_email.setOnFocusChangeListener { _, hasFocus ->
            mPresenter.onEmailFocusChange(view.edit_email.text.toString(), hasFocus)
        }
        view.view_alpha.setOnTouchListener { _, _ -> true }
        return view
    }

    override fun isNetworkAvailable() = context?.isNetworkAvailable() ?: false

    override fun showErrorNetworkConnection() {
        view?.text_error?.text = getString(R.string.network_error)
    }

    override fun showErrorPassword() {
        view?.layout_password?.animateShake(SHAKE_DURATION)
        view?.layout_password_repeat?.animateShake(SHAKE_DURATION)
        view?.text_error?.text = getString(R.string.fragment_registration_password_error)
    }

    override fun showErrorEmailFormat() {
        showEmailError()
        view?.text_error?.text = getString(R.string.fragment_registration_email_format)
    }

    override fun showErrorEmailExists() {
        showEmailError()
        view?.text_error?.text = getString(R.string.fragment_registration_error_email_exists)
    }

    override fun showMessageCorrectEmail() {
        showEmailSuccess()
    }

    override fun showMessageSuccessRegistration() {
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

    override fun showErrorShortPassword() {
        view?.layout_password?.animateShake(SHAKE_DURATION)
        view?.text_error?.text = getString(R.string.fragment_registration_error_short_password)
    }

    override fun showErrorEmptyPassword() {
        view?.layout_password?.animateShake(SHAKE_DURATION)
        view?.text_error?.text = getString(R.string.fragment_registration_empty_password)
    }

    override fun showErrorEmptyPasswordRepeat() {
        view?.layout_password_repeat?.animateShake(SHAKE_DURATION)
        view?.text_error?.text = getString(R.string.fragment_registration_empty_password)
    }

    override fun showErrorEmptyEmail() {
        showEmailError()
        view?.text_error?.text = getString(R.string.fragment_registration_empty_email)
    }

    private fun showEmailError() {
        view?.image_email?.setImageResource(R.drawable.ic_error)
        view?.image_email?.setColorFilter(ContextCompat.getColor(context!!, R.color.red), PorterDuff.Mode.SRC_IN)
        hideLoadingEmail()
        view?.image_email?.visibility = View.VISIBLE
    }

    private fun showEmailSuccess() {
        view?.text_error?.text = ""
        view?.image_email?.setImageResource(R.drawable.ic_done)
        view?.image_email?.setColorFilter(getThemeAccentColor(context!!), PorterDuff.Mode.SRC_IN)
        hideLoadingEmail()
        view?.image_email?.visibility = View.VISIBLE
    }

    override fun clearEmailMessages() {
        view?.text_error?.text = ""
        view?.image_email?.visibility = View.GONE
        hideLoadingEmail()
    }

    override fun showLoadingEmail() {
        view?.text_error?.text = ""
        view?.image_email?.visibility = View.GONE
        view?.progress_email?.visibility = View.VISIBLE
    }

    override fun hideLoadingEmail() {
        view?.progress_email?.visibility = View.GONE
    }

    override fun showLoading() {
        view?.view_alpha?.visibility = View.VISIBLE
        view?.progress_sign_up?.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        view?.view_alpha?.visibility = View.GONE
        view?.progress_sign_up?.visibility = View.GONE
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
        mPresenter.detachView()
    }

    override fun onDetach() {
        super.onDetach()
        (parentFragment as? AuthFragment?)?.onRegistrationFragmentDetach()
    }
}
