package com.furianrt.mydiary.dialogs.categories

import com.furianrt.mydiary.base.BaseMvpView
import com.furianrt.mydiary.base.BasePresenter

interface CategoriesDialogContract {

    interface MvpView : BaseMvpView {
    }

    abstract class Presenter : BasePresenter<MvpView>() {
    }
}