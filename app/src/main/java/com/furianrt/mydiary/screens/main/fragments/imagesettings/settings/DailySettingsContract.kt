package com.furianrt.mydiary.screens.main.fragments.imagesettings.settings

import com.furianrt.mydiary.base.mvp.BaseMvpView
import com.furianrt.mydiary.base.mvp.BaseMvpPresenter

interface DailySettingsContract {
    interface MvpView : BaseMvpView

    abstract class Presenter : BaseMvpPresenter<MvpView>()
}