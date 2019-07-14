package com.furianrt.mydiary.view.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.furianrt.mydiary.analytics.MyAnalytics
import javax.inject.Inject

abstract class BaseFragment : Fragment(), BaseView {

    @Inject
    lateinit var analytics: MyAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
    }
}