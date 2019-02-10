package com.furianrt.mydiary.authentication.registration

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView

interface RegistrationContract {

    interface View : BaseView {

    }

    abstract class Presenter : BasePresenter<View>() {

    }
}