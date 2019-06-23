package com.furianrt.mydiary.screens.main.fragments.profile.password.success

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.furianrt.mydiary.R
import com.furianrt.mydiary.base.BaseFragment

class PasswordSuccessFragment : BaseFragment() {

    companion object {
        const val TAG = "PasswordSuccessFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_password_success, container, false)
    }
}
