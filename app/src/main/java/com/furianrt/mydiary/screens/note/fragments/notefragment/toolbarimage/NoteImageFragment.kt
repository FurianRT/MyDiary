package com.furianrt.mydiary.screens.note.fragments.notefragment.toolbarimage

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
import com.furianrt.mydiary.screens.note.fragments.notefragment.NoteFragment
import kotlinx.android.synthetic.main.fragment_note_image.view.*

class NoteImageFragment : Fragment() {

    private lateinit var mImage: MyImage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mImage = arguments?.getParcelable(ARG_NOTE_IMAGE) ?: throw IllegalArgumentException()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_note_image, container, false)

        GlideApp.with(this)
                .load(Uri.parse(mImage.uri))
                .signature(ObjectKey(mImage.editedTime))
                .into(view.image_note)

        view.layout_toolbar_image_root.setOnClickListener {
            (parentFragment as NoteFragment).onToolbarImageClick()
        }

        return view
    }

    companion object {

        private const val ARG_NOTE_IMAGE = "noteImage"

        @JvmStatic
        fun newInstance(image: MyImage) =
                NoteImageFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_NOTE_IMAGE, image)
                    }
                }
    }
}
