package com.furianrt.mydiary.screens.gallery.fragments.pager.image

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.signature.ObjectKey
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.general.GlideApp
import kotlinx.android.synthetic.main.fragment_gallery_image.view.*

class GalleryImageFragment : Fragment() {

    private lateinit var mImage: MyImage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mImage = arguments?.getParcelable(ARG_IMAGE) ?: throw IllegalArgumentException()
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_gallery_image, container, false)

        GlideApp.with(this)
                .load(Uri.parse(mImage.uri))
                .signature(ObjectKey(mImage.editedTime))
                .into(view.image_gallery)

        return view
    }

    companion object {

        private const val ARG_IMAGE = "image"

        @JvmStatic
        fun newInstance(image: MyImage) =
                GalleryImageFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_IMAGE, image)
                    }
                }
    }
}
