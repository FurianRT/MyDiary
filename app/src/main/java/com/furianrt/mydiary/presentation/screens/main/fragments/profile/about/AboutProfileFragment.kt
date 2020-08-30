/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.fragments.profile.about

import android.os.Bundle
import android.view.View
import com.furianrt.mydiary.R
import com.furianrt.mydiary.presentation.base.BaseFragment
import com.furianrt.mydiary.model.entity.MyProfile
import com.furianrt.mydiary.utils.getTime
import kotlinx.android.synthetic.main.fragment_about_profile.*
import org.joda.time.DateTime
import java.util.*
import javax.inject.Inject

class AboutProfileFragment : BaseFragment(R.layout.fragment_about_profile), AboutProfileContract.View {

    companion object {
        const val TAG = "AboutProfileFragment"
    }

    @Inject
    lateinit var presenter: AboutProfileContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_about_back.setOnClickListener { presenter.onButtonBackClick() }
    }

    override fun showProfileInfo(profile: MyProfile, is24TimeFormat: Boolean) {
        val datePattern = "dd.MM.yyyy"
        text_registration_date.text = DateTime(profile.creationTime).toString(datePattern, Locale.getDefault())
        val lastSyncTime = profile.lastSyncTime
        if (lastSyncTime == null) {
            text_last_sync_date?.text = getString(R.string.fragment_about_profile_sync_non)
        } else {
            text_last_sync_date.text = getString(
                    R.string.two_strings_pattern,
                    DateTime(lastSyncTime).toString(datePattern, Locale.getDefault()),
                    getTime(lastSyncTime, is24TimeFormat)
            )
        }
    }

    override fun returnToMenuView() {
        parentFragmentManager.popBackStack()
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
