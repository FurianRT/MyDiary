package com.furianrt.mydiary.authentication.registration

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView

interface RegistrationContract {

    interface View : BaseView {
        fun close()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonCancelClick()
    }
}