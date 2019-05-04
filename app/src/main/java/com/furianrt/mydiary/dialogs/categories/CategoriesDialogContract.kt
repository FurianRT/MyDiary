package com.furianrt.mydiary.dialogs.categories

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView

interface CategoriesDialogContract {

    interface View : BaseView {
    }

    abstract class Presenter : BasePresenter<View>() {
    }
}