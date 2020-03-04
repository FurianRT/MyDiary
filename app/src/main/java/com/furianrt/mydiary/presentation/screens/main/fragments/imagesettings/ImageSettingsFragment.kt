/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.fragments.imagesettings

import android.content.Context
import android.os.Bundle
import android.view.View
import com.furianrt.mydiary.R
import com.furianrt.mydiary.presentation.base.BaseFragment
import com.furianrt.mydiary.presentation.screens.main.MainBottomSheetHolder
import com.furianrt.mydiary.presentation.screens.main.fragments.imagesettings.settings.DailySettingsFragment
import com.furianrt.mydiary.utils.inTransaction
import kotlinx.android.synthetic.main.fragment_image_settings.*
import javax.inject.Inject

class ImageSettingsFragment : BaseFragment(R.layout.fragment_image_settings), ImageSettingsContract.View {

    companion object {
        const val TAG = "ImageSettingsFragment"
    }

    @Inject
    lateinit var presenter: ImageSettingsContract.Presenter

    private var mListener: MainBottomSheetHolder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_image_settings_close.setOnClickListener { presenter.onButtonCloseClick() }
        if (childFragmentManager.findFragmentByTag(DailySettingsFragment.TAG) == null) {
            childFragmentManager.inTransaction {
                add(R.id.container_image_settings, DailySettingsFragment(), DailySettingsFragment.TAG)
            }
        }
    }

    override fun close() {
        mListener?.closeBottomSheet()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainBottomSheetHolder) {
            mListener = context
        } else {
            throw IllegalStateException()
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
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
