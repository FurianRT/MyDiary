package com.furianrt.mydiary.main.fragments.profile.menu

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView

interface MenuProfileContract {

    interface View : BaseView {

    }

    abstract class Presenter : BasePresenter<View>() {

    }
}