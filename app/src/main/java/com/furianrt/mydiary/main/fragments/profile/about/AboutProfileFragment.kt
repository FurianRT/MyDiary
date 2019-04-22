package com.furianrt.mydiary.main.fragments.profile.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyProfile
import kotlinx.android.synthetic.main.fragment_about_profile.*
import kotlinx.android.synthetic.main.fragment_about_profile.view.*
import org.joda.time.DateTime
import java.util.*
import javax.inject.Inject

class AboutProfileFragment : Fragment(), AboutProfileContract.View {

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

    override fun showProfileInfo(profile: MyProfile) {
        text_registration_date.text =
                DateTime(profile.registrationTime).toString("dd.MM.yyyy", Locale.getDefault())
        text_has_premium.text = if (profile.hasPremium) {
            getString(R.string.fragment_about_profile_yup)
        } else {
            getString(R.string.fragment_about_profile_sync_non)
        }
        if (profile.lastSyncTime == 0L) {
            text_last_sync_date?.text = getString(R.string.fragment_about_profile_sync_non)
        } else {
            text_last_sync_date.text =
                    DateTime(profile.lastSyncTime).toString("dd.MM.yyyy hh:mm", Locale.getDefault())
        }
    }

    override fun returnToMenuView() {
        fragmentManager?.popBackStack()
    }

    override fun onResume() {
        super.onResume()
        mPresenter.attachView(this)
        mPresenter.onViewResume()
    }

    override fun onPause() {
        super.onPause()
        mPresenter.detachView()
    }
}
