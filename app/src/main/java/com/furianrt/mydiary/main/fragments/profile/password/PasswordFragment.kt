package com.furianrt.mydiary.main.fragments.profile.password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.furianrt.mydiary.R
import javax.inject.Inject

class PasswordFragment : Fragment(), PasswordContract.View {

    companion object {
        const val TAG = "PasswordFragment"
    }

    @Inject
    lateinit var mPresenter: PasswordContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(context!!).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_password, container, false)

        return view
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
