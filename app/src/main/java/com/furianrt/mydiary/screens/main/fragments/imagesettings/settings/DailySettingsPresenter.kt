package com.furianrt.mydiary.screens.main.fragments.imagesettings.settings

import com.furianrt.mydiary.data.DataManager
import javax.inject.Inject

class DailySettingsPresenter @Inject constructor(
        private val dataManager: DataManager
) : DailySettingsContract.Presenter()