package com.furianrt.mydiary.main.fragments.profile.password.success

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.furianrt.mydiary.R

class PasswordSuccessFragment : Fragment() {

    companion object {
        const val TAG = "PasswordSuccessFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_password_success, container, false)
    }
}
