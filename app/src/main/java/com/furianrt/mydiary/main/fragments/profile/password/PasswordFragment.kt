package com.furianrt.mydiary.main.fragments.profile.password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.furianrt.mydiary.R
import com.furianrt.mydiary.main.fragments.profile.password.success.PasswordSuccessFragment
import com.furianrt.mydiary.utils.animateShake
import com.furianrt.mydiary.utils.inTransaction
import com.furianrt.mydiary.utils.isNetworkAvailable
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_sheet_main.*
import kotlinx.android.synthetic.main.fragment_password.view.*
import javax.inject.Inject

class PasswordFragment : Fragment(), PasswordContract.View {

    companion object {
        const val TAG = "PasswordFragment"
        private const val SHAKE_DURATION = 400L
        private const val CLOSE_AFTER_DONE_DELAY = 2000L
    }

    @Inject
    lateinit var mPresenter: PasswordContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(context!!).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_password, container, false)

        view.button_password_cancel.setOnClickListener { mPresenter.onButtonCancelClick() }
        view.button_password_save.setOnClickListener {
            val oldPassword = view.edit_old_password.text?.toString() ?: ""
            val newPassword = view.edit_new_password.text?.toString() ?: ""
            val repeatPassword = view.edit_password_repeat.text?.toString() ?: ""
            mPresenter.onButtonSaveClick(oldPassword, newPassword, repeatPassword)
        }
        view.view_alpha.setOnTouchListener { _, _ -> true }

        return view
    }

    override fun isNetworkAvailable() = context?.isNetworkAvailable() ?: false

    override fun showLoading() {
        view?.view_alpha?.visibility = View.VISIBLE
        view?.progress_change_password?.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        view?.view_alpha?.visibility = View.GONE
        view?.progress_change_password?.visibility = View.GONE
    }

    override fun showErrorEmptyOldPassword() {
        view?.layout_old_password?.animateShake(SHAKE_DURATION)
        view?.text_error?.text = getString(R.string.fragment_password_enter_old_password)
    }

    override fun showErrorEmptyNewPassword() {
        view?.layout_new_password?.animateShake(SHAKE_DURATION)
        view?.text_error?.text = getString(R.string.fragment_password_enter_new_password)
    }

    override fun showErrorEmptyRepeatPassword() {
        view?.layout_password_repeat?.animateShake(SHAKE_DURATION)
        view?.text_error?.text = getString(R.string.fragment_registration_repeat_password)
    }

    override fun showErrorWrongOldPassword() {
        view?.layout_old_password?.animateShake(SHAKE_DURATION)
        view?.text_error?.text = getString(R.string.wrong_password)
    }

    override fun showErrorWrongPasswordRepeat() {
        view?.layout_new_password?.animateShake(SHAKE_DURATION)
        view?.layout_password_repeat?.animateShake(SHAKE_DURATION)
        view?.text_error?.text = getString(R.string.fragment_registration_password_error)
    }

    override fun showErrorNetworkConnection() {
        view?.text_error?.text = getString(R.string.network_error)
    }

    override fun showErrorShortNewPassword() {
        view?.layout_new_password?.animateShake(SHAKE_DURATION)
        view?.text_error?.text = getString(R.string.fragment_registration_error_short_password)
    }

    override fun showSuccessPasswordChange() {
        fragmentManager?.let {
            if (it.findFragmentByTag(PasswordSuccessFragment.TAG) == null) {
                it.inTransaction {
                    setCustomAnimations(R.anim.scale_up, R.anim.scale_up)
                    add(R.id.profile_container, PasswordSuccessFragment(), PasswordSuccessFragment.TAG)
                }
            }
        }
        activity?.let {
            it.main_sheet_container.postDelayed({
                BottomSheetBehavior.from(it.main_sheet_container).state = BottomSheetBehavior.STATE_COLLAPSED
            }, CLOSE_AFTER_DONE_DELAY)
        }
    }

    override fun returnToMenuView() {
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
}
