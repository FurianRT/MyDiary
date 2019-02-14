package com.furianrt.mydiary.authentication.registration

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.furianrt.mydiary.R
import com.furianrt.mydiary.authentication.AuthFragment
import com.furianrt.mydiary.utils.getThemeAccentColor
import com.furianrt.mydiary.utils.isNetworkAvailable
import kotlinx.android.synthetic.main.fragment_registration.view.*
import javax.inject.Inject

class RegistrationFragment : Fragment(), RegistrationContract.View {

    companion object {
        const val TAG = "RegistrationFragment"
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
        return view
    }

    override fun isNetworkAvailable() = context?.isNetworkAvailable() ?: false

    override fun showErrorNetworkConnection() {
        view?.text_error?.text = getString(R.string.fragment_registration_network_error)
    }

    override fun showErrorPassword() {
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
        Toast.makeText(context, "Регистрация успешна!", Toast.LENGTH_SHORT).show()
    }

    override fun showErrorShortPassword() {
        view?.text_error?.text = getString(R.string.fragment_registration_error_short_password)
    }

    override fun showErrorEmptyPassword() {
        view?.text_error?.text = getString(R.string.fragment_registration_empty_password)
    }

    override fun showErrorEmptyPasswordRepeat() {
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

    }

    override fun hideLoading() {

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
