package com.furianrt.mydiary.note.fragments.notefragment.toolbarimage


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyImage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_note_image.view.*
import java.io.File

private const val ARG_NOTE_IMAGE = "noteImage"

class NoteImageFragment : Fragment() {

    private var mImage: MyImage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mImage = it.getParcelable(ARG_NOTE_IMAGE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_note_image, container, false)

        Picasso.get()
                .load(File(mImage?.url))
                .into(view.image_note)

        return view
    }


    companion object {
        @JvmStatic
        fun newInstance(image: MyImage) =
                NoteImageFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_NOTE_IMAGE, image)
                    }
                }
    }
}
