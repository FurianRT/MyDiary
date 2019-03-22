package com.furianrt.mydiary.main.fragments.imagesettings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.furianrt.mydiary.R
import com.furianrt.mydiary.main.MainActivity
import kotlinx.android.synthetic.main.fragment_image_settings.view.*
import javax.inject.Inject

class ImageSettingsFragment : Fragment(), ImageSettingsContract.View {

    companion object {
        const val TAG = "ImageSettingsFragment"
    }

    @Inject
    lateinit var mPresenter: ImageSettingsContract.Presenter

    private var listener: OnImageSettingsInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(context!!).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_image_settings, container, false)

        view.button_image_settings_close.setOnClickListener { mPresenter.onButtonCloseClick() }

        return view
    }

    override fun close() {
        (activity as? MainActivity?)?.closeBottomSheet()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnImageSettingsInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnImageSettingsInteractionListener")
        }
    }

    override fun onResume() {
        super.onResume()
        mPresenter.attachView(this)
    }

    override fun onPause() {
        super.onPause()
        mPresenter.detachView()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnImageSettingsInteractionListener {

    }
}
