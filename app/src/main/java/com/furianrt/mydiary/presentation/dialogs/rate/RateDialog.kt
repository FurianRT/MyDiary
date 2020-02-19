/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.rate

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.furianrt.mydiary.R
import com.furianrt.mydiary.presentation.base.BaseDialog
import kotlinx.android.synthetic.main.dialog_rate.view.*
import javax.inject.Inject

class RateDialog : BaseDialog(), RateDialogContract.View {

    companion object {
        const val TAG = "MoodsDialog"
    }

    @Inject
    lateinit var presenter: RateDialogContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_rate, null)

        view.button_later.setOnClickListener { presenter.onButtonLaterClick() }
        view.button_never.setOnClickListener { presenter.onButtonNeverClick() }
        view.button_rate.setOnClickListener { presenter.onButtonRateClick(view.rating.rating.toInt()) }
        view.rating.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { _, rating, _ ->
            view.button_rate.text = getString(R.string.dialog_rate_continue, rating.toInt(), view.rating.max)
        }

        view.button_rate.text = getString(R.string.dialog_rate_continue, 0, view.rating.max)

        return AlertDialog.Builder(requireContext())
                .setView(view)
                .create()
    }

    override fun openAppPage() {
        val playMarketPage = "market://details?id=${requireContext().packageName}"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(playMarketPage)
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), getString(R.string.phone_related_error), Toast.LENGTH_SHORT).show()
        }
    }

    override fun sendEmailToSupport(supportEmail: String) {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$supportEmail"))
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.low_rate_email_subject))
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.low_rate_email_text))
        startActivity(Intent.createChooser(intent, getString(R.string.low_rate_email_title)))
    }

    override fun close() {
        dismiss()
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView()
    }
}