package com.furianrt.mydiary.premium

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView

interface PremiumContract {

    interface View : BaseView {

    }

    abstract class Presenter : BasePresenter<View>() {

    }
}