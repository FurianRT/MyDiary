package com.furianrt.mydiary.main.fragments.imagesettings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.furianrt.mydiary.R

class ImageSettingsFragment : Fragment() {

    private var listener: OnImageSettingsInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_image_settings, container, false)

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnImageSettingsInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnImageSettingsInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnImageSettingsInteractionListener {

    }
}
