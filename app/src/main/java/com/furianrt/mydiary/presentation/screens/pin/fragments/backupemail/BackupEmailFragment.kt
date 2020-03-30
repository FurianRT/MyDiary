/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.pin.fragments.backupemail

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.furianrt.mydiary.R
import com.furianrt.mydiary.presentation.base.BaseFragment
import com.furianrt.mydiary.utils.KeyboardUtils
import com.furianrt.mydiary.utils.animateShake
import kotlinx.android.synthetic.main.fragment_backup_email.*
import javax.inject.Inject

class BackupEmailFragment : BaseFragment(R.layout.fragment_backup_email), BackupEmailContract.View {

    companion object {
        const val TAG = "BackupEmailFragment"
    }

    @Inject
    lateinit var presenter: BackupEmailContract.Presenter

    private var mListener: OnBackupEmailFragmentListener? = null

    private val mTextChangeListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            image_email_error.visibility = View.GONE
        }
    }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(this)

        button_create_pin.setOnClickListener {
            presenter.onButtonDoneClick(edit_backup_email.text?.toString() ?: "")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            edit_backup_email.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO
        }

        presenter.onViewCreated(edit_backup_email.text?.toString() ?: "", savedInstanceState == null)
    }

    override fun showEmail(email: String) {
        edit_backup_email.setText(email)
    }

    override fun showEmailIsCorrect(email: String) {
        mListener?.onEmailEntered(email)
    }

    override fun showErrorEmailFormat() {
        image_email_error.visibility = View.VISIBLE
        edit_backup_email.animateShake()
        Toast.makeText(requireContext(), getString(R.string.fragment_backup_email_invalid_email), Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        KeyboardUtils.addKeyboardToggleListener(requireActivity(), mOnKeyboardToggleListener)
        edit_backup_email.addTextChangedListener(mTextChangeListener)
        presenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        KeyboardUtils.removeKeyboardToggleListener(mOnKeyboardToggleListener)
        edit_backup_email.removeTextChangedListener(mTextChangeListener)
        presenter.detachView()
    }

    override fun onAttach(context: Context) {
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
