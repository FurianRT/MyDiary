package com.furianrt.mydiary.view.screens.main.fragments.profile.menu

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.furianrt.mydiary.R
import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.view.base.BaseFragment
import com.furianrt.mydiary.data.model.SyncProgressMessage
import com.furianrt.mydiary.screens.main.fragments.profile.about.AboutProfileFragment
import com.furianrt.mydiary.screens.main.fragments.profile.password.PasswordFragment
import com.furianrt.mydiary.screens.main.fragments.profile.signout.SignOutFragment
import com.furianrt.mydiary.view.services.sync.SyncService
import com.furianrt.mydiary.utils.inTransaction
import kotlinx.android.synthetic.main.fragment_menu_profile.*
import kotlinx.android.synthetic.main.fragment_menu_profile.view.*
import javax.inject.Inject

class MenuProfileFragment : BaseFragment(), MenuProfileContract.MvpView, View.OnClickListener {

    companion object {
        const val TAG = "MenuProfileFragment"
    }

    @Inject
    lateinit var mPresenter: MenuProfileContract.Presenter

    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            SyncService.getProgressMessage(intent)?.let {
                if (it.hasError || it.taskIndex == SyncProgressMessage.SYNC_FINISHED) {
                   button_sign_out?.isEnabled = true
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_menu_profile, container, false)

        view.button_sign_out.setOnClickListener(this)
        view.button_change_password.setOnClickListener(this)
        view.button_about.setOnClickListener(this)

        return view
    }

    override fun onClick(v: View) {
        activity?.supportFragmentManager?.inTransaction {
            setPrimaryNavigationFragment(parentFragment)
        }
        when (v.id) {
            R.id.button_sign_out -> {
                analytics.sendEvent(MyAnalytics.EVENT_SIGN_OUT)
                mPresenter.onButtonSignOutClick()
            }
            R.id.button_change_password -> {
                analytics.sendEvent(MyAnalytics.EVENT_PASSWORD_CHANGE)
                mPresenter.onButtonChangePasswordClick()
            }
            R.id.button_about -> {
                analytics.sendEvent(MyAnalytics.EVENT_PROFILE_ABOUT)
                mPresenter.onButtonAboutClick()
            }
        }
    }

    override fun showAboutView() {
        fragmentManager?.inTransaction {
            setCustomAnimations(R.anim.from_right, R.anim.to_left, R.anim.from_left, R.anim.to_right)
            replace(R.id.profile_container, AboutProfileFragment(), AboutProfileFragment.TAG)
                    .addToBackStack(null)
        }
    }

    override fun showPasswordView() {
        fragmentManager?.inTransaction {
            setCustomAnimations(R.anim.from_right, R.anim.to_left, R.anim.from_left, R.anim.to_right)
            replace(R.id.profile_container, PasswordFragment(), PasswordFragment.TAG)
                    .addToBackStack(null)
        }
    }

    override fun showSignOutView() {
        fragmentManager?.inTransaction {
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
        mPresenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(mBroadcastReceiver)
        mPresenter.detachView()
    }
}
