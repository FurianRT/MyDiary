package com.furianrt.mydiary.authentication.login

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView

interface LoginContract {

    interface View : BaseView {

    }

    abstract class Presenter : BasePresenter<View>() {

    }
}