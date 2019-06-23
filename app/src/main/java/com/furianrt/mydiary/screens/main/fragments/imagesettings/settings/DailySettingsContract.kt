package com.furianrt.mydiary.screens.main.fragments.imagesettings.settings

import com.furianrt.mydiary.base.BaseMvpView
import com.furianrt.mydiary.base.BasePresenter

interface DailySettingsContract {
    interface MvpView : BaseMvpView {
    }

    abstract class Presenter : BasePresenter<MvpView>() {
    }
}