/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.fragments.profile.menu

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.furianrt.mydiary.R
import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.presentation.base.BaseFragment
import com.furianrt.mydiary.model.entity.SyncProgressMessage
import com.furianrt.mydiary.presentation.screens.main.fragments.profile.about.AboutProfileFragment
import com.furianrt.mydiary.presentation.screens.main.fragments.profile.password.PasswordFragment
import com.furianrt.mydiary.presentation.screens.main.fragments.profile.signout.SignOutFragment
import com.furianrt.mydiary.services.SyncService
import com.furianrt.mydiary.utils.inTransaction
import kotlinx.android.synthetic.main.fragment_menu_profile.*
import javax.inject.Inject

class MenuProfileFragment : BaseFragment(R.layout.fragment_menu_profile), MenuProfileContract.View,
        View.OnClickListener {

    companion object {
        const val TAG = "MenuProfileFragment"
    }

    @Inject
    lateinit var presenter: MenuProfileContract.Presenter

    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            SyncService.getProgressMessage(intent)?.let {
                if (it.hasError || it.task == SyncProgressMessage.SYNC_FINISHED) {
                    button_sign_out?.isEnabled = true
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_sign_out.setOnClickListener(this)
        button_change_password.setOnClickListener(this)
        button_about.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        activity?.supportFragmentManager?.inTransaction {
            setPrimaryNavigationFragment(parentFragment)
        }
        when (v.id) {
            R.id.button_sign_out -> {
                analytics.sendEvent(MyAnalytics.EVENT_SIGN_OUT)
                presenter.onButtonSignOutClick()
            }
            R.id.button_change_password -> {
                analytics.sendEvent(MyAnalytics.EVENT_PASSWORD_CHANGE)
                presenter.onButtonChangePasswordClick()
            }
            R.id.button_about -> {
                analytics.sendEvent(MyAnalytics.EVENT_PROFILE_ABOUT)
                presenter.onButtonAboutClick()
            }
        }
    }

    override fun showAboutView() {
        parentFragmentManager.inTransaction {
            setCustomAnimations(R.anim.from_right, R.anim.to_left, R.anim.from_left, R.anim.to_right)
            replace(R.id.profile_container, AboutProfileFragment(), AboutProfileFragment.TAG)
                    .addToBackStack(null)
        }
    }

    override fun showPasswordView() {
        parentFragmentManager.inTransaction {
            setCustomAnimations(R.anim.from_right, R.anim.to_left, R.anim.from_left, R.anim.to_right)
            replace(R.id.profile_container, PasswordFragment(), PasswordFragment.TAG)
                    .addToBackStack(null)
        }
    }

    override fun showSignOutView() {
        parentFragmentManager.inTransaction {
            setCustomAnimations(R.anim.from_right, R.anim.to_left, R.anim.from_left, R.anim.to_right)
            replace(R.id.profile_container, SignOutFragment(), SignOutFragment.TAG)
                    .addToBackStack(null)
        }
    }

    override fun disableSignOut(disable: Boolean) {
        button_sign_out.isEnabled = !disable
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(requireContext())
                .registerReceiver(mBroadcastReceiver, IntentFilter(Intent.ACTION_SYNC))
        presenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(mBroadcastReceiver)
        presenter.detachView()
    }
}
