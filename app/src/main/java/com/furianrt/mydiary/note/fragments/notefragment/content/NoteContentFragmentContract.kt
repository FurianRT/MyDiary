package com.furianrt.mydiary.note.fragments.notefragment.content

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView

interface NoteContentFragmentContract {

    interface View : BaseView {
    }

    abstract class Presenter : BasePresenter<View>() {

    }
}