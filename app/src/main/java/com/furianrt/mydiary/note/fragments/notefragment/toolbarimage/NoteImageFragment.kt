package com.furianrt.mydiary.note.fragments.notefragment.toolbarimage


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.general.GlideApp
import com.furianrt.mydiary.note.fragments.notefragment.NoteFragment
import kotlinx.android.synthetic.main.fragment_note_image.view.*

private const val ARG_NOTE_IMAGE = "noteImage"

class NoteImageFragment : androidx.fragment.app.Fragment() {

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

        GlideApp.with(this)
                .load(mImage?.url)
                .into(view.image_note)

        view.layout_toolbar_image_root.setOnClickListener {
            (parentFragment as NoteFragment).onToolbarImageClick()
        }

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
