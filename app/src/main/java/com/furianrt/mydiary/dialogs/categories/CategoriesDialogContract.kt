package com.furianrt.mydiary.dialogs.categories

import com.furianrt.mydiary.base.mvp.BaseMvpView
import com.furianrt.mydiary.base.mvp.BaseMvpPresenter

interface CategoriesDialogContract {

    interface MvpView : BaseMvpView

    abstract class Presenter : BaseMvpPresenter<MvpView>()
}