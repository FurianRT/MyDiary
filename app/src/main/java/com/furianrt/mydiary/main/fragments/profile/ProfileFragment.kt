package com.furianrt.mydiary.main.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.furianrt.mydiary.R
import com.furianrt.mydiary.main.MainActivity
import com.furianrt.mydiary.main.fragments.profile.menu.MenuProfileFragment
import com.furianrt.mydiary.utils.inTransaction
import kotlinx.android.synthetic.main.fragment_profile.view.*
import javax.inject.Inject

class ProfileFragment : Fragment(), ProfileContract.View {

    companion object {
        const val TAG = "ProfileFragment"
    }

    @Inject
    lateinit var mPresenter: ProfileContract.Presenter


    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(context!!).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        view.button_profile_close.setOnClickListener { mPresenter.onButtonCloseClick() }

        if (childFragmentManager.findFragmentByTag(MenuProfileFragment.TAG) == null) {
            childFragmentManager.inTransaction {
                add(R.id.profile_container, MenuProfileFragment(), MenuProfileFragment.TAG)
            }
        }

        return view
    }



    override fun close() {
        (activity as? MainActivity?)?.closeBottomSheet()
    }

    override fun onStart() {
        super.onStart()
        mPresenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        mPresenter.detachView()
    }

    fun isBackStackEmpty() = childFragmentManager.backStackEntryCount == 0
}