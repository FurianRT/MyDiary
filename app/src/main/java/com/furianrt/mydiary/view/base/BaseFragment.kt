package com.furianrt.mydiary.view.base

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.furianrt.mydiary.R
import com.furianrt.mydiary.analytics.MyAnalytics
import javax.inject.Inject

abstract class BaseFragment : Fragment(), BaseView {

    @Inject
    lateinit var analytics: MyAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        requireActivity().overridePendingTransition(R.anim.screen_right_in, R.anim.screen_left_out)
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
        requireActivity().overridePendingTransition(R.anim.screen_right_in, R.anim.screen_left_out)
    }
}