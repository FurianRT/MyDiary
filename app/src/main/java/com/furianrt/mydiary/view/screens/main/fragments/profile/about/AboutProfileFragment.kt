package com.furianrt.mydiary.view.screens.main.fragments.profile.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.furianrt.mydiary.R
import com.furianrt.mydiary.view.base.BaseFragment
import com.furianrt.mydiary.data.model.MyProfile
import com.furianrt.mydiary.utils.getTime
import kotlinx.android.synthetic.main.fragment_about_profile.*
import kotlinx.android.synthetic.main.fragment_about_profile.view.*
import org.joda.time.DateTime
import java.util.*
import javax.inject.Inject

class AboutProfileFragment : BaseFragment(), AboutProfileContract.MvpView {

    companion object {
        const val TAG = "AboutProfileFragment"
    }

    @Inject
    lateinit var mPresenter: AboutProfileContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_about_profile, container, false)

        view.button_about_back.setOnClickListener { mPresenter.onButtonBackClick() }

        return view
    }

    override fun showProfileInfo(profile: MyProfile, is24TimeFormat: Boolean) {
        text_registration_date.text =
                DateTime(profile.creationTime).toString("dd.MM.yyyy", Locale.getDefault())
        val lastSyncTime = profile.lastSyncTime
        if (lastSyncTime == null) {
            text_last_sync_date?.text = getString(R.string.fragment_about_profile_sync_non)
        } else {
            val syncTimeString = DateTime(lastSyncTime).toString("dd.MM.yyyy", Locale.getDefault()) +
                    " " + getTime(lastSyncTime, is24TimeFormat)
            text_last_sync_date.text = syncTimeString
        }
    }

    override fun returnToMenuView() {
        fragmentManager?.popBackStack()
    }

    override fun onStart() {
        super.onStart()
        mPresenter.attachView(this)
        mPresenter.onViewStart()
    }

    override fun onStop() {
        super.onStop()
        mPresenter.detachView()
    }
}
