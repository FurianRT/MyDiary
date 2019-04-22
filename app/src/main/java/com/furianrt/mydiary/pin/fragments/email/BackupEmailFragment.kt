package com.furianrt.mydiary.pin.fragments.email

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.furianrt.mydiary.R
import com.furianrt.mydiary.utils.KeyboardUtils
import kotlinx.android.synthetic.main.fragment_backup_email.*
import kotlinx.android.synthetic.main.fragment_backup_email.view.*
import javax.inject.Inject

class BackupEmailFragment : Fragment(), BackupEmailContract.View {

    companion object {
        const val TAG = "BackupEmailFragment"
    }

    @Inject
    lateinit var mPresenter: BackupEmailContract.Presenter

    private var mListener: OnBackupEmailFragmentListener? = null

    private val mOnKeyboardToggleListener = object : KeyboardUtils.SoftKeyboardToggleListener {
        override fun onToggleSoftKeyboard(isVisible: Boolean) {
            //повторная прорисовка контекстного меню
            edit_backup_email.performClick()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_backup_email, container, false)

        view.button_create_pin.setOnClickListener {
            mPresenter.onButtonDoneClick(edit_backup_email.text?.toString() ?: "")
        }

        return view
    }

    override fun showEmailIsCorrect(email: String) {
        mListener?.onEmailEntered(email)
    }

    override fun showErrorEmailFormat() {

    }

    override fun showErrorEmptyEmail() {

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

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnBackupEmailFragmentListener) {
            mListener = context
        } else {
            throw IllegalStateException()
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnBackupEmailFragmentListener {
        fun onEmailEntered(email: String)
    }
}
