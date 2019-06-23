package com.furianrt.mydiary.base

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.furianrt.mydiary.analytics.MyAnalytics
import javax.inject.Inject

abstract class BaseDialog : DialogFragment(), BaseView {

    @Inject
    lateinit var analytics: MyAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
    }
}