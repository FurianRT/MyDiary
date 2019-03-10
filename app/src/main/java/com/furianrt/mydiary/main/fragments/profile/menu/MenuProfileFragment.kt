package com.furianrt.mydiary.main.fragments.profile.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.furianrt.mydiary.R
import com.furianrt.mydiary.main.fragments.profile.password.PasswordFragment
import com.furianrt.mydiary.main.fragments.profile.signout.SignOutFragment
import com.furianrt.mydiary.utils.inTransaction
import kotlinx.android.synthetic.main.fragment_menu_profile.view.*
import javax.inject.Inject

class MenuProfileFragment : Fragment(), MenuProfileContract.View, View.OnClickListener {

    companion object {
        const val TAG = "MenuProfileFragment"
    }

    @Inject
    lateinit var mPresenter: MenuProfileContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(context!!).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_menu_profile, container, false)

        view.button_sign_out.setOnClickListener(this)
        view.button_change_password.setOnClickListener(this)

        return view
    }

    override fun onClick(v: View) {
        activity?.supportFragmentManager?.inTransaction {
            setPrimaryNavigationFragment(parentFragment)
        }
        when (v.id) {
            R.id.button_sign_out -> mPresenter.onButtonSignOutClick()
            R.id.button_change_password -> mPresenter.onButtonChangePasswordClick()
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

    override fun onStart() {
        super.onStart()
        mPresenter.attachView(this)

    }

    override fun onStop() {
        super.onStop()
        mPresenter.detachView()
    }
}
