package com.furianrt.mydiary.gallery.fragments.pager.image

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.general.GlideApp
import kotlinx.android.synthetic.main.fragment_gallery_image.view.*

class GalleryImageFragment : Fragment() {

    private var mImage: MyImage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { mImage = it.getParcelable(ARG_IMAGE) }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_gallery_image, container, false)

        GlideApp.with(this)
                .load(Uri.parse(mImage?.uri))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
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
