package com.furianrt.mydiary.screens.main.fragments.imagesettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.furianrt.mydiary.R
import com.furianrt.mydiary.screens.main.MainActivity
import com.furianrt.mydiary.screens.main.fragments.imagesettings.settings.DailySettingsFragment
import com.furianrt.mydiary.utils.inTransaction
import kotlinx.android.synthetic.main.fragment_image_settings.view.*
import javax.inject.Inject

class ImageSettingsFragment : Fragment(), ImageSettingsContract.View {

    companion object {
        const val TAG = "ImageSettingsFragment"
    }

    @Inject
    lateinit var mPresenter: ImageSettingsContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_image_settings, container, false)

        view.button_image_settings_close.setOnClickListener { mPresenter.onButtonCloseClick() }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (childFragmentManager.findFragmentByTag(DailySettingsFragment.TAG) == null) {
            childFragmentManager.inTransaction {
                add(R.id.container_image_settings, DailySettingsFragment(), DailySettingsFragment.TAG)
            }
        }
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
}
