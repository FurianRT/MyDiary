package com.furianrt.mydiary.main.fragments.profile.password

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView

interface PasswordContract {

    interface View : BaseView {

    }

    abstract class Presenter : BasePresenter<View>() {

    }
}